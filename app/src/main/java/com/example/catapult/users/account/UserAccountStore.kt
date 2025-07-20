package com.example.catapult.users.account

import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAccountStore @Inject constructor(
    private val persistence: DataStore<UserAccount>,
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    val userAccount = persistence.data
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = runBlocking { persistence.data.first() },
        )

    suspend fun getUserAccount(): UserAccount {
        return persistence.data.first()
    }

    suspend fun setUserAccount(data: UserAccount) {
        persistence.updateData { data }
    }

    suspend fun removeUserAccount() {
        persistence.updateData { UserAccount() }
    }

    suspend fun hasUserAccount(): Boolean {
        return try {
            val user = getUserAccount()
            user.firstName.isNotEmpty() && user.lastName.isNotEmpty() && user.username.isNotEmpty() && user.email.isNotEmpty()
        } catch (e : Exception) {
            false
        }
    }
}