package com.example.catapult.breeds.model

data class BreedsListUiModel (
    val id : String,
    val name: String,
    val alt: String,
    val temperament: String,
    val description: String
)

data class BreedsDetailsUiModel (
    val id: String,
    val name: String,
    val description: String,
    val origin: String,
    val temperament: String,
    val lifeSpan: String,
    val weightMetric: String,
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
    val isRare: Int,
    val wikipediaUrl: String,
    val imageUrl: String,
)
