package com.example.catapult.users.registration

interface UserRegistrationContract {
    sealed class UiEvent {
        data class Submit(
            val firstName: String,
            val lastName: String,
            val username: String,
            val email: String
        ) : UiEvent()
    }

    sealed class SideEffect {
        data object NavigateToMainScreen : SideEffect()
    }


}