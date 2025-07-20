// in: com/example/catapult/breeds/BreedsRepository.kt
package com.example.catapult.breeds

import com.example.catapult.breeds.api.BreedsApi
import com.example.catapult.breeds.db.Breed
import com.example.catapult.breeds.db.Image
import com.example.catapult.breeds.mapper.asBreedsListDataModel
import com.example.catapult.breeds.mapper.asImageDataModel
import com.example.catapult.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BreedsRepository @Inject constructor(
    private val database: AppDatabase,
    private val breedsApi: BreedsApi
){

    suspend fun fetchAllBreeds() {
        val data: List<Breed>
        withContext(Dispatchers.IO) {
            data = breedsApi.getAllBreeds().map { asBreedsListDataModel(it) }
            database.BreedsDao().insertAllBreeds(data)
        }
    }

    suspend fun getAllBreeds() : List<Breed> = withContext(Dispatchers.IO) {
        database.BreedsDao().getAllBreeds()
    }

    suspend fun getBreedsByQuery(query: String): List<Breed> = withContext(Dispatchers.IO) {
        database.BreedsDao().getBreedsByQuery(query)
    }

    suspend fun getBreedById(breedId: String): Breed = withContext(Dispatchers.IO) {
        database.BreedsDao().getBreedById(breedId)
            ?: run {
                val fetched = asBreedsListDataModel(breedsApi.getBreedById(breedId))
                database.BreedsDao().insertBreed(fetched)
                fetched
            }
    }

    suspend fun getImageById(imageId: String, breedId: String): Image = withContext(Dispatchers.IO) {
        database.ImageDao().getImageById(imageId)
            ?: run {
                val fetched = asImageDataModel(breedsApi.getImageById(imageId), breedId)
                database.ImageDao().insertImage(fetched)
                fetched
            }
    }

    suspend fun getImagesForBreed(breedId: String, imgLimit : Int = 2): List<Image> = withContext(Dispatchers.IO) {
        database.ImageDao().getImagesForBreed(breedId)
            .takeIf { it.size >= imgLimit }
            ?: run {
                val fetched = breedsApi.getImagesForBreed(breedId, 20)
                    .map { asImageDataModel(it, breedId) }

                database.ImageDao().insertAllImages(fetched)
                fetched
            }
    }
}