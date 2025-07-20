package com.example.catapult.breeds.list

import com.example.catapult.breeds.model.BreedsListUiModel

interface BreedsListContract {
    data class UiState (
        val loading : Boolean = true,
        val isSearched : Boolean = false,
        val data : List<BreedsListUiModel> = emptyList(),
        val error: Throwable? = null,
    )
}