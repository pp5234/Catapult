package com.example.catapult.users.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.users.UserRepository
import com.example.catapult.users.profile.UsersProfileContract.SideEffect
import com.example.catapult.users.profile.UsersProfileContract.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UsersProfileContract.UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: UsersProfileContract.UiState.() -> UsersProfileContract.UiState) = _state.getAndUpdate(reducer)

    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(event: UiEvent) = viewModelScope.launch { events.emit(event) }

    private val _effect: Channel<SideEffect> = Channel()
    val effect = _effect.receiveAsFlow()
    private fun setEffect(effect: SideEffect) = viewModelScope.launch { _effect.send(effect) }

    init {
        observeEvents()
        loadUserProfile()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
                    UiEvent.Logout -> logout()
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            setEffect(SideEffect.NavigateToRegistration)
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val userAccount = userRepository.getUserData()
                if (userAccount.firstName.isNotEmpty() &&
                    userAccount.lastName.isNotEmpty() &&
                    userAccount.username.isNotEmpty() &&
                    userAccount.email.isNotEmpty()
                    ) {
                    val quizResults = userRepository.getAllResults(userAccount.username)
                    val bestScore = quizResults.maxByOrNull { it.score }
                    val bestRank = userRepository.getBestRank(userAccount.username)

                    setState {
                        copy(
                            isLoading = false,
                            userAccount = userAccount,
                            quizResults = quizResults,
                            bestScore = bestScore,
                            bestRank = bestRank
                        )
                    }
                } else {
                    setState { copy(isLoading = false, error = IllegalStateException("User not found")) }
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e) }
            }
        }
    }
}