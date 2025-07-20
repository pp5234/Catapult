package com.example.catapult.leaderboard

import com.example.catapult.leaderboard.model.LeaderboardUiModel

interface LeaderboardContract {
    data class LeaderboardState(
        val isLoading: Boolean = true,
        val leaderboard: List<LeaderboardUiModel> = emptyList(),
        val currentUserIndex: Int = -1,
        val error: Throwable? = null
    )
    sealed class UiEvent {
        data object ScrollToUser : UiEvent()
    }

    sealed class SideEffect {
        data class ScrollToPosition(val index: Int) : SideEffect()
    }
}