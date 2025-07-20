package com.example.catapult.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.leaderboard.mapper.mapToLeaderboardUiModel
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
class LeaderboardViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderboardContract.LeaderboardState())
    val state = _state.asStateFlow()

    private val events = MutableSharedFlow<LeaderboardContract.UiEvent>()
    fun setEvent(event: LeaderboardContract.UiEvent) = viewModelScope.launch { events.emit(event) }

    private val _effect: Channel<LeaderboardContract.SideEffect> = Channel()
    val effect = _effect.receiveAsFlow()
    private fun setEffect(effect: LeaderboardContract.SideEffect) = viewModelScope.launch { _effect.send(effect) }

    private fun setState(reducer: LeaderboardContract.LeaderboardState.() -> LeaderboardContract.LeaderboardState) {
        _state.getAndUpdate(reducer)
    }

    init {
        fetchLeaderboard()
        observeEvents()
    }

    private fun fetchLeaderboard() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val currentUserNickname = leaderboardRepository.getCurrentUserNickname()
                val leaderboardApiResult = leaderboardRepository.getLeaderboard()
                val leaderboardUiModels = mapToLeaderboardUiModel(leaderboardApiResult, currentUserNickname)
                val userIndex = leaderboardUiModels.indexOfFirst { it.isCurrentUser }

                setState {
                    copy(
                        isLoading = false,
                        leaderboard = leaderboardUiModels,
                        currentUserIndex = userIndex
                    )
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e) }
            }
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
                    is LeaderboardContract.UiEvent.ScrollToUser -> {
                        if (state.value.currentUserIndex != -1) {
                            setEffect(LeaderboardContract.SideEffect.ScrollToPosition(state.value.currentUserIndex))
                        }
                    }
                }
            }
        }
    }
}