package com.example.catapult.breeds.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageApiModel (
    val id : String? = "",
    val url : String? = "",
)