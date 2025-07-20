package com.example.catapult.users.profile

import com.example.catapult.quiz.db.Result
import com.example.catapult.users.account.UserAccount

interface UsersProfileContract {
    data class UiState(
        val isLoading: Boolean = true,
        val userAccount: UserAccount? = null,
        val quizResults: List<Result> = emptyList(),
        val bestScore: Result? = null,
        val bestRank: Int? = null,
        val error: Throwable? = null
    )

    sealed class UiEvent {
        data object Logout : UiEvent()
    }

    sealed class SideEffect {
        data object NavigateToRegistration : SideEffect()
    }
}