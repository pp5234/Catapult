package com.example.catapult.leaderboard.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardApiModel(
    val nickname: String,
    val result: Double,
    @SerialName("createdAt") val createdAt: Long,
    val category: Int
)