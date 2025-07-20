package com.example.catapult.leaderboard

import com.example.catapult.leaderboard.api.LeaderboardApi
import com.example.catapult.users.account.UserAccountStore
import javax.inject.Inject

class LeaderboardRepository @Inject constructor(
    private val leaderboardApi: LeaderboardApi,
    private val userAccountStore: UserAccountStore
) {
    suspend fun getLeaderboard() = leaderboardApi.getLeaderboard()
    suspend fun getCurrentUserNickname(): String? {
        return userAccountStore.getUserAccount().username
    }
}