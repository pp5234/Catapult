package com.example.catapult.breeds.gallery

import com.example.catapult.breeds.model.ImageUiModel

interface BreedsGalleryContract {
        data class UIState(
            val loading: Boolean = false,
            val images: List<ImageUiModel> = emptyList(),
            val breedName: String? = null,
            val error: String? = null
        )
}