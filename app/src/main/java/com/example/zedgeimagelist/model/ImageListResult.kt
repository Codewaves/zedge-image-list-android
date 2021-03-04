package com.example.zedgeimagelist.model

sealed class ImageListResult {
    data class Success(val data: List<Image>) : ImageListResult()
    data class Loading(val data: List<Image>) : ImageListResult()
    data class Error(val error: Exception, val data: List<Image>) : ImageListResult()
}