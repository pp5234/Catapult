package com.example.catapult.breeds.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.breeds.BreedsRepository
import com.example.catapult.breeds.mapper.asImageUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BreedsGalleryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val breedsRepository: BreedsRepository,
    ) : ViewModel() {

        private val _state = MutableStateFlow(BreedsGalleryContract.UIState())
        val state: StateFlow<BreedsGalleryContract.UIState> = _state.asStateFlow()

        private val breedId: String = savedStateHandle.get<String>("breedId") ?: ""
        private fun setState(reducer: BreedsGalleryContract.UIState.() -> BreedsGalleryContract.UIState) = _state.getAndUpdate(reducer)


    init {
        loadBreedName()
        loadBreedGallery()
    }
    
    private fun loadBreedGallery() = viewModelScope.launch {
        setState { copy(loading = true)}
        try {
            var breedImagesRes = breedsRepository.getImagesForBreed(breedId).map { asImageUiModel(it) }
            setState { copy(loading = false, images = breedImagesRes) }
        } catch (e : IOException) {
            setState { copy(loading = false, images = emptyList()) }
        }  catch (e : Exception) {
            setState { copy(loading = false, error = e.message) }
        }
    }

    private fun loadBreedName() = viewModelScope.launch {
        try {
            var breedRes = breedsRepository.getBreedById(breedId)
            setState { copy(breedName = breedRes.name) }
        } catch (e : Exception) {
            setState { copy(breedName = "")}
        }
    }

}