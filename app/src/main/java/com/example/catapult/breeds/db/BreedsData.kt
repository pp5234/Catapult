package com.example.catapult.breeds.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breed")
data class Breed(
    @PrimaryKey
    val id: String,
    val name: String,
    val alt: String,
    val origin: String,
    val description: String,
    val temperament: String,
    val lifeSpan: String,
    val weight: String,
    val adaptability: Int,
    val affectionLevel: Int,
    val childFriendly: Int,
    val dogFriendly: Int,
    val energyLevel: Int,
    val grooming: Int,
    val healthIssues: Int,
    val intelligence: Int,
    val sheddingLevel: Int,
    val socialNeeds: Int,
    val strangerFriendly: Int,
    val vocalisation: Int,
    val rare: Int,
    val wikipediaUrl: String?,
    val referenceImageId: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)