package com.example.zedgeimagelist.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.zedgeimagelist.MockImageService
import com.example.zedgeimagelist.data.CachedImagePageProvider
import com.example.zedgeimagelist.model.Image
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DbTest {
    private var imagesDao: ImageDao? = null
    private var imageDatabase: ImageDatabase? = null

    @Before
    fun onCreateDB() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        imageDatabase = Room.inMemoryDatabaseBuilder(context, ImageDatabase::class.java).build()
        imagesDao = imageDatabase!!.imagesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        imageDatabase!!.close()
    }

    @Test
    fun imageCachingIsOk() {
        val testPageNo = 1
        val pageProvider = CachedImagePageProvider(MockImageService(), imageDatabase!!)

        runBlocking {
            pageProvider.getPage(testPageNo)

            val imagesFromDb = imagesDao!!.imagesByPage(testPageNo)

            val expected = listOf(
                Image(
                    1,
                    6068966,
                    "heart, love, ornament",
                    "https://cdn.pixabay.com/photo/2021/03/04/17/51/heart-6068966_150.jpg",
                    "https://pixabay.com/get/g7c8ae4f115956ea16e8b6f6d4becfb180560ce49639f0532be6ffe56eae155bbf2c20d6ad70030d10c9fd1864ff1db4a0976e67b169db1a2474d2cce4bf692a1_640.jpg",
                    "https://pixabay.com/get/g302e33b3942ec99104ee99a3be0b3fe9ba98c8c99d4454cce8999aeecfdbffdee503624b060dd84496d1a728c8de5ec3945529cd8c422c9bbe215c288f96580f_1280.jpg",
                    testPageNo,
                    false
                )
            )

            Assert.assertEquals(expected, imagesFromDb)
        }
    }
}