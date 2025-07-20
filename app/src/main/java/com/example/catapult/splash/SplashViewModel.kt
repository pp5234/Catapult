package com.example.catapult.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.breeds.BreedsRepository
import com.example.catapult.users.account.UserAccountStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userAccountStore: UserAccountStore,
    private val breedsRepository: BreedsRepository
) : ViewModel() {

    private val _hasUserAccount = MutableStateFlow(false)
    val hasUserAccount = _hasUserAccount

    private val _bootCompleted = MutableStateFlow(false)
    val bootCompleted = _bootCompleted

    init {
        viewModelScope.launch {
            // 1) Da li imamo user account?
            _hasUserAccount.emit(userAccountStore.hasUserAccount())
            try {
                breedsRepository.fetchAllBreeds()
            }catch (e: Exception) {
                Log.d("CatapultSplashScreen", "failed to fetch breeds")
            }


            // Opciono
            // 2) Da li je verzija out of date?
            // 3) Da li treba migriramo neke podatke.
            // 4) Itd....

            _bootCompleted.emit(true)
        }
    }
}