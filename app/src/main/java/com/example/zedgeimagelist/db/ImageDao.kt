package com.example.zedgeimagelist.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.zedgeimagelist.model.Image


@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Image>): List<Long>

    @Query("SELECT * FROM images WHERE page = :page ORDER BY uid ASC")
    suspend fun imagesByPage(page: Int): List<Image>

    @Query("SELECT * FROM images WHERE uid = :uid")
    fun imageById(uid: Long): LiveData<Image>

    @Query("DELETE FROM images")
    suspend fun clearImages()

    @Query("SELECT * from images WHERE favorite = 1 ORDER BY uid ASC")
    fun favorites(): LiveData<List<Image>>

    @Query("UPDATE images SET favorite = :isFavored WHERE uid = :uid")
    suspend fun toggleFavorite(uid: Long, isFavored: Boolean)
}