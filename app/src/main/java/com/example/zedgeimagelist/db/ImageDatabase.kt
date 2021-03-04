package com.example.zedgeimagelist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.zedgeimagelist.model.Image

@Database(
    entities = [Image::class],
    version = 1,
    exportSchema = false
)
abstract class ImageDatabase : RoomDatabase() {

    abstract fun imagesDao(): ImageDao

    companion object {
        fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                ImageDatabase::class.java, "Images.db")
                .build()
    }
}