package com.example.catapult.breeds.mapper


import com.example.catapult.breeds.api.model.ImageApiModel
import com.example.catapult.breeds.db.Image
import com.example.catapult.breeds.model.ImageUiModel

fun asImageDataModel(data : ImageApiModel, breedId : String) : Image {
    return Image(
        id = data.id ?: "",
        breedId = breedId,
        url = data.url ?: ""
    )
}

fun asImageUiModel(data : Image) : ImageUiModel {
    return ImageUiModel(
        id = data.id,
        url = data.url
    )
}
