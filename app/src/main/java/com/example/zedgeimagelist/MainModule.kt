package com.example.zedgeimagelist

import com.example.zedgeimagelist.data.ImageRepository
import com.example.zedgeimagelist.api.RetrofitBuilder
import com.example.zedgeimagelist.data.CachedImagePageProvider
import com.example.zedgeimagelist.data.ImagePageProvider
import com.example.zedgeimagelist.db.ImageDatabase
import com.example.zedgeimagelist.presentation.DetailsViewModel
import com.example.zedgeimagelist.presentation.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { DetailsViewModel(get(), get()) }
}

val apiModule = module {
    single { ImageDatabase.buildDatabase(androidContext()) }
    single { CachedImagePageProvider(RetrofitBuilder.imageService, get()) as ImagePageProvider }
    single { ImageRepository(androidContext(), get(), get()) }
}