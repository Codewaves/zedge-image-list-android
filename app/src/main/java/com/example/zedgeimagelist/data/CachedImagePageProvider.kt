package com.example.zedgeimagelist.data

import com.example.zedgeimagelist.api.ImageService
import com.example.zedgeimagelist.db.ImageDatabase
import com.example.zedgeimagelist.model.Image


class CachedImagePageProvider(
    private val imageService: ImageService,
    private val imageDatabase: ImageDatabase,
) : ImagePageProvider {
    override suspend fun getPage(page: Int): List<Image> {
        val imageDao = imageDatabase.imagesDao()
        var images = imageDao.imagesByPage(page)
        if (images.isEmpty()) {
            val response = imageService.getImagePage(page)
            images = response.hits.onEach { it.page = page }

            // Add to db cache
            val ids = imageDao.insertAll(images)
            images.forEachIndexed { index, image -> image.uid = ids[index] }
        }

        return images
    }
}