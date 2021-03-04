package com.example.zedgeimagelist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class Image(
    @PrimaryKey(autoGenerate = true) var uid: Long,
    val id: Long,
    val tags: String,
    val previewURL: String,
    val webformatURL: String,
    val largeImageURL: String,
    var page: Int,
    var favorite: Boolean,
)