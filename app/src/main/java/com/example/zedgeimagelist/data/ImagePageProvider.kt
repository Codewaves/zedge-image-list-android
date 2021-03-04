package com.example.zedgeimagelist.data

import com.example.zedgeimagelist.model.Image

interface ImagePageProvider {
    suspend fun getPage(page: Int): List<Image>
}