package com.example.catapult.breeds.api

import com.example.catapult.breeds.api.model.BreedsApiModel
import com.example.catapult.breeds.api.model.ImageApiModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BreedsApi {
    @GET("breeds")
    suspend fun getAllBreeds(): List<BreedsApiModel>

    @GET("breeds/{id}")
    suspend fun getBreedById(
        @Path("id") breedId: String,
    ): BreedsApiModel

    @GET("images/{id}")
    suspend fun getImageById(
        @Path("id") imageId : String
    ) : ImageApiModel

    @GET("images/search")
    suspend fun getImagesForBreed(
        @Query("breed_ids") breedId: String,
        @Query("limit") limit: Int = 20
    ): List<ImageApiModel>
}