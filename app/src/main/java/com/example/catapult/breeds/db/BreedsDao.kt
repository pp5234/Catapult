package com.example.catapult.breeds.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreed(breed: Breed)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllBreeds(breeds: List<Breed>)

    @Query("SELECT * FROM breed ORDER BY name ASC")
    suspend fun getAllBreeds(): List<Breed>

    @Query("SELECT * FROM breed WHERE id = :breedId")
    suspend fun getBreedById(breedId: String): Breed?

    @Query("SELECT * FROM breed WHERE name LIKE '%' || :query || '%' OR alt LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun getBreedsByQuery(query: String): List<Breed>

    @Query("SELECT COUNT(*) FROM breed")
    suspend fun getBreedsCount(): Int

    @Query("SELECT * FROM breed WHERE (:excludeId IS NULL OR id != :excludeId)  ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomBreeds(excludeId: String?, count: Int): List<Breed>

    @Query("SELECT temperament FROM breed")
    suspend fun getAllTemperamentStrings(): List<String> // New method
}