package com.example.catapult.leaderboard.model

data class LeaderboardUiModel(
    val rank: Int,
    val nickname: String,
    val score: Double,
    val totalQuizzes: Int,
    val isCurrentUser: Boolean = false
)