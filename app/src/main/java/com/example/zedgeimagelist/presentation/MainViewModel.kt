package com.example.zedgeimagelist.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zedgeimagelist.api.ImageService
import com.example.zedgeimagelist.data.ImageRepository
import com.example.zedgeimagelist.model.ImageListResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val state: SavedStateHandle,
    private val imageRepository: ImageRepository,
) : ViewModel() {
    companion object {
        const val SCROLL_FETCH_THRESHOLD = ImageService.PAGE_SIZE / 2

        const val STATE_FAVORITE_LIST = "favorite_list"
    }

    val favoriteListState: MutableLiveData<Boolean> by lazy {
        state.getLiveData(STATE_FAVORITE_LIST, false)
    }

    fun toggleFavoriteList() {
        state[STATE_FAVORITE_LIST] = !(favoriteListState.value ?: true)
    }

    suspend fun imageList(favorite: Boolean): Flow<ImageListResult> {
        return if (favorite) imageRepository.favoredFlow() else imageRepository.imageListFlow()
    }

    fun listScrolled(lastVisibleItemPosition: Int, totalItemCount: Int) {
        if (lastVisibleItemPosition + SCROLL_FETCH_THRESHOLD >= totalItemCount
            && favoriteListState.value != true) {
            viewModelScope.launch {
                imageRepository.fetchNextPage()
            }
        }
    }

    fun listRetry() {
        viewModelScope.launch {
            imageRepository.retry()
        }
    }
}