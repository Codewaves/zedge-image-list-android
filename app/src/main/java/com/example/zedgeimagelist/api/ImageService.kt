package com.example.zedgeimagelist.api

import com.example.zedgeimagelist.model.ImagePage
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageService {
    companion object {
        const val PAGE_SIZE = 100
    }
    @GET("?per_page=$PAGE_SIZE&q=flower")
    suspend fun getImagePage(@Query("page") page: Int): ImagePage
}