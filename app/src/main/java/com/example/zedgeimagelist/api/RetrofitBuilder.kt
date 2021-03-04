package com.example.zedgeimagelist.api

import com.example.zedgeimagelist.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private fun provideOkHttpClient(): OkHttpClient {
        val connectTimeout = 10
        val readTimeout = 10
        val key = BuildConfig.API_KEY
        val builder = OkHttpClient().newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val originalHttpUrl = chain.request().url
                val url = originalHttpUrl.newBuilder().addQueryParameter("key", key).build()
                request.url(url)
                return@addInterceptor chain.proceed(request.build())
            }
            .connectTimeout(connectTimeout.toLong(), TimeUnit.SECONDS)
            .readTimeout(readTimeout.toLong(), TimeUnit.SECONDS)

        return builder.build()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val imageService: ImageService = getRetrofit().create(ImageService::class.java)
}