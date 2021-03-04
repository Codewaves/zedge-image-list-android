package com.example.zedgeimagelist.api

import com.example.zedgeimagelist.enqueueResponse
import com.example.zedgeimagelist.model.Image
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiTest {
    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ImageService::class.java)

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun imagePageFetchAndParseIsCorrect() {
        mockWebServer.enqueueResponse("api-response.json", 200)

        runBlocking {
            val actual = api.getImagePage(1)

            val expected = listOf(
                Image(
                    0,
                    6068966,
                    "heart, love, ornament",
                    "https://cdn.pixabay.com/photo/2021/03/04/17/51/heart-6068966_150.jpg",
                    "https://pixabay.com/get/g7c8ae4f115956ea16e8b6f6d4becfb180560ce49639f0532be6ffe56eae155bbf2c20d6ad70030d10c9fd1864ff1db4a0976e67b169db1a2474d2cce4bf692a1_640.jpg",
                    "https://pixabay.com/get/g302e33b3942ec99104ee99a3be0b3fe9ba98c8c99d4454cce8999aeecfdbffdee503624b060dd84496d1a728c8de5ec3945529cd8c422c9bbe215c288f96580f_1280.jpg",
                    0,
                    false)
            )

            assertEquals(expected, actual.hits)

            val request = mockWebServer.takeRequest()
            assert(request.path?.contains("page=1") == true)
        }
    }
}