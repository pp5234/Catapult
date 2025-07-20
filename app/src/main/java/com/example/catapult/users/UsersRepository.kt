package com.example.catapult.users

import com.example.catapult.db.AppDatabase
import com.example.catapult.users.account.UserAccount
import com.example.catapult.users.account.UserAccountStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val database: AppDatabase,
    private val store: UserAccountStore
){

    suspend fun getUserData() = withContext(Dispatchers.IO) {
        store.getUserAccount()
    }

    suspend fun getAllResults(username: String) = withContext(Dispatchers.IO) {
        database.ResultDao().getResultsForUser(username)
    }

    suspend fun getBestRank(username: String) = withContext(Dispatchers.IO) {
        database.ResultDao().getBestGlobalRankingForUser(username)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        store.removeUserAccount()
    }

    suspend fun registerUser(userData: UserAccount) = withContext(Dispatchers.IO){
        store.setUserAccount(userData)
    }
}