package com.example.catapult.quiz.api

import com.example.catapult.leaderboard.api.model.LeaderboardApiModel
import kotlinx.serialization.Serializable

@Serializable
data class PostResultApiModel(
    val nickname: String,
    val result: Double,
    val category: Int
)

@Serializable
data class PostResultResponse(
    val result: LeaderboardApiModel,
    val ranking: Int
)