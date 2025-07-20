package com.example.catapult.leaderboard.mapper

import com.example.catapult.leaderboard.api.model.LeaderboardApiModel
import com.example.catapult.leaderboard.model.LeaderboardUiModel

fun mapToLeaderboardUiModel(
    apiModels: List<LeaderboardApiModel>,
    currentUserNickname: String?
): List<LeaderboardUiModel> {
    val quizCounts = apiModels.groupingBy { it.nickname }.eachCount()
    return apiModels.mapIndexed { index, apiModel ->
        LeaderboardUiModel(
            rank = index + 1,
            nickname = apiModel.nickname,
            score = apiModel.result,
            totalQuizzes = quizCounts[apiModel.nickname] ?: 0,
            isCurrentUser = apiModel.nickname == currentUserNickname
        )
    }
}