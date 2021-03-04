package com.example.zedgeimagelist.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import com.example.zedgeimagelist.db.ImageDatabase
import com.example.zedgeimagelist.model.Image
import com.example.zedgeimagelist.model.ImageListResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

class ImageRepository(
    private val context: Context,
    private val imageDatabase: ImageDatabase,
    private val pageProvider: ImagePageProvider
) {
    private var lastPage = 1
    private var isFetchInProgress = false
    private var isListComplete = false
    private var isErrorEncountered = false

    private val imageCache = mutableListOf<Image>()
    private val imageResults = MutableSharedFlow<ImageListResult>(replay = 1)

    fun getImage(uid: Long): LiveData<Image> {
        return imageDatabase.imagesDao().imageById(uid)
    }

    fun favoredFlow(): Flow<ImageListResult> {
        return imageDatabase.imagesDao().favorites().asFlow()
            .map { ImageListResult.Success(it) }
    }

    suspend fun toggleFavorite(uid: Long, isFavored: Boolean) {
        imageDatabase.imagesDao().toggleFavorite(uid, isFavored)

        imageCache.find { it.uid == uid }?.favorite = isFavored
        imageResults.emit(ImageListResult.Success(imageCache))
    }

    suspend fun imageListFlow(): Flow<ImageListResult> {
        if (lastPage == 1 && !isFetchInProgress && !isListComplete) requestNextPage()

        return imageResults
    }

    suspend fun fetchNextPage() {
        if (isFetchInProgress || isListComplete || isErrorEncountered) return
        requestNextPage()
    }

    suspend fun retry() {
        isErrorEncountered = false
        fetchNextPage()
    }

    private suspend fun requestNextPage() {
        isFetchInProgress = true

        try {
            imageResults.emit(ImageListResult.Loading(imageCache))

            val images = pageProvider.getPage(lastPage)
            imageCache.addAll(images)

            lastPage++
            imageResults.emit(ImageListResult.Success(imageCache))
        } catch (exception: HttpException) {
            // Detect end of image stream by 400 error
            if (exception.code() == 400) {
                isListComplete = true
                imageResults.emit(ImageListResult.Success(imageCache))
            } else {
                setErrorState(exception)
            }
        } catch (exception: Exception) {
            setErrorState(exception)
        }

        isFetchInProgress = false
    }

    private suspend fun setErrorState(exception: Exception) {
        isErrorEncountered = true
        imageResults.emit(ImageListResult.Error(exception, imageCache))
    }
}