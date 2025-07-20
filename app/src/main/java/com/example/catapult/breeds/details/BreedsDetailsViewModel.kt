package com.example.catapult.breeds.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.breeds.BreedsRepository
import com.example.catapult.breeds.db.Breed
import com.example.catapult.breeds.db.Image
import com.example.catapult.breeds.mapper.asBreedsDetailsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class BreedsDetailsViewModel @Inject constructor(
    savedState : SavedStateHandle,
    private val breedsRepository : BreedsRepository
) : ViewModel() {
    val breedId = savedState.get<String>("breedId") ?: error("BreedId not found")

    private val _state = MutableStateFlow(BreedsDetailsContract.UIState())
    val state = _state.asStateFlow()
    private fun setState(reducer: BreedsDetailsContract.UIState.() -> BreedsDetailsContract.UIState) = _state.getAndUpdate(reducer)

    init {
        loadBreedDetails()
    }

    private fun loadBreedDetails() = viewModelScope.launch {
        setState { copy(loading = true)}
        try {
            var breedRes = breedsRepository.getBreedById(breedId)
            var imageRes = loadImage(breedRes);
            val breed = asBreedsDetailsUiModel(breedRes, imageRes.url)
            setState { copy(loading = false, data = breed) }
        } catch (e : IOException) {
            setState { copy(loading = false, error = e)}
        }
    }

    private suspend fun loadImage(breed : Breed) : Image {
        return try {
            breedsRepository.getImageById(breed.referenceImageId ?: "", breed.id)
        } catch (e : Exception) {
            Image("","","");
        }


    }

}