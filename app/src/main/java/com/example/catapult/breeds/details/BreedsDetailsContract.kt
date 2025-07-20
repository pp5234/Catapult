package com.example.catapult.breeds.details

import com.example.catapult.breeds.model.BreedsDetailsUiModel

interface BreedsDetailsContract {
    data class UIState (
        val loading: Boolean = true,
        val data: BreedsDetailsUiModel? = null,
        val error: Throwable? = null
    )
}