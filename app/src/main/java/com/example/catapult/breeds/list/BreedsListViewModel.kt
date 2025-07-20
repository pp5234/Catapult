// in: com/example/catapult/breeds/list/BreedsListViewModel.kt
package com.example.catapult.breeds.list

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.breeds.BreedsRepository
import com.example.catapult.breeds.mapper.asBreedsListUiModel
import com.example.catapult.breeds.model.BreedsListUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BreedsListViewModel @Inject constructor(
    private val breedsRepository: BreedsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(BreedsListContract.UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: BreedsListContract.UiState.() -> BreedsListContract.UiState) =
        _state.getAndUpdate(reducer)

    init {
        val query: String? = savedStateHandle["q"]
        loadBreedsList(query)
    }

    private fun loadBreedsList(query: String?) = viewModelScope.launch {
        setState { copy(loading = true) }
        try {
            val data: List<BreedsListUiModel>
            withContext(Dispatchers.IO) {
                val breeds = if (!query.isNullOrBlank()) {
                    breedsRepository.getBreedsByQuery(query)
                } else {
                    breedsRepository.getAllBreeds()
                }
                data = breeds.map { asBreedsListUiModel(it) }
            }
            setState { copy(loading = false, data = data) }
        } catch (e: Exception) {
            Log.d("BreedsListViewModel: ", e.message.toString())
            setState { copy(loading = false, error = e) }
        }
    }
}