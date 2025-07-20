package com.example.catapult.breeds.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Image)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllImages(images: List<Image>)

    @Query("SELECT * FROM image WHERE id = :imageId")
    suspend fun getImageById(imageId: String): Image?

    @Query("SELECT * FROM image WHERE breedId = :breedId")
    suspend fun getImagesForBreed(breedId: String): List<Image>

    @Query("SELECT * FROM image WHERE breedId = :breedId ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomImagesForBreed(breedId: String, count: Int): List<Image>
}