package com.example.catapult.users.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.users.UserRepository
import com.example.catapult.users.account.UserAccount
import com.example.catapult.users.registration.UserRegistrationContract.SideEffect
import com.example.catapult.users.registration.UserRegistrationContract.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserRegistrationViewModel @Inject constructor(
    private val usersRepository: UserRepository,
) : ViewModel() {

    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(event: UiEvent) = viewModelScope.launch { events.emit(event) }

    private val _effect: Channel<SideEffect> = Channel()
    val effect = _effect.receiveAsFlow()
    private fun setEffect(effect: SideEffect) = viewModelScope.launch { _effect.send(effect) }

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is UiEvent.Submit -> {
                        val accountData = UserAccount(
                            event.firstName,
                            event.lastName,
                            event.username,
                            event.email
                        )
                        submit(accountData)
                    }
                }
            }
        }
    }

    private fun submit(data : UserAccount) = viewModelScope.launch {
        usersRepository.registerUser(data)
        setEffect(SideEffect.NavigateToMainScreen)
    }

}