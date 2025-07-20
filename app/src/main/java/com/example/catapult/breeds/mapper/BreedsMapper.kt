package com.example.catapult.breeds.mapper

import com.example.catapult.breeds.api.model.BreedsApiModel
import com.example.catapult.breeds.db.Breed
import com.example.catapult.breeds.model.BreedsDetailsUiModel
import com.example.catapult.breeds.model.BreedsListUiModel

fun asBreedsDetailsUiModel(data : Breed, imageUrl: String?) : BreedsDetailsUiModel {
    return BreedsDetailsUiModel(
        id = data.id,
        name = data.name,
        description = data.description,
        origin = data.origin,
        temperament = data.temperament,
        lifeSpan = data.lifeSpan,
        weightMetric = data.weight,
        adaptability = data.adaptability,
        affectionLevel = data.affectionLevel,
        childFriendly = data.childFriendly,
        dogFriendly = data.dogFriendly,
        energyLevel = data.energyLevel,
        grooming = data.grooming,
        healthIssues = data.healthIssues,
        intelligence = data.intelligence,
        sheddingLevel = data.sheddingLevel,
        socialNeeds = data.socialNeeds,
        strangerFriendly = data.strangerFriendly,
        vocalisation = data.vocalisation,
        isRare = data.rare,
        wikipediaUrl = data.wikipediaUrl ?: "",
        imageUrl = imageUrl ?: ""
    )
}

fun asBreedsListUiModel(data : Breed) : BreedsListUiModel {
    return BreedsListUiModel(
        id = data.id,
        name = data.name,
        alt = data.alt,
        description = data.description,
        temperament = data.temperament
    )
}

fun asBreedsListDataModel(data : BreedsApiModel) : Breed {
    return Breed(
        id = data.id,
        name = data.name,
        alt = data.alt,
        description = data.description,
        temperament = data.temperament,
        origin = data.origin,
        lifeSpan = data.lifeSpan,
        weight = data.weight.metric,
        adaptability = data.adaptability,
        affectionLevel = data.affectionLevel,
        childFriendly = data.childFriendly,
        dogFriendly = data.dogFriendly,
        energyLevel = data.energyLevel,
        grooming = data.grooming,
        healthIssues = data.healthIssues,
        intelligence = data.intelligence,
        sheddingLevel = data.sheddingLevel,
        socialNeeds = data.socialNeeds,
        strangerFriendly = data.strangerFriendly,
        vocalisation = data.vocalisation,
        rare = data.rare,
        wikipediaUrl = data.wikipediaUrl,
        referenceImageId = data.referenceImageId,
    )
}

