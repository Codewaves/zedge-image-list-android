package com.example.zedgeimagelist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zedgeimagelist.data.ImageRepository
import com.example.zedgeimagelist.model.Image
import com.example.zedgeimagelist.presentation.DetailsActivity.Companion.PARAM_IMAGE_ID
import kotlinx.coroutines.launch


class DetailsViewModel(
    private val state: SavedStateHandle,
    private val imageRepository: ImageRepository
) : ViewModel() {
    var image: LiveData<Image> = imageRepository.getImage(state[PARAM_IMAGE_ID] ?: -1)

    fun toggleFavorite(isFavored: Boolean) {
        viewModelScope.launch {
            imageRepository.toggleFavorite(state[PARAM_IMAGE_ID] ?: -1, isFavored)
        }
    }
}