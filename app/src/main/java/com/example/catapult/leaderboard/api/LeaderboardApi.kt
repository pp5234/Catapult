package com.example.catapult.leaderboard.api

import com.example.catapult.leaderboard.api.model.LeaderboardApiModel
import com.example.catapult.quiz.api.PostResultApiModel
import com.example.catapult.quiz.api.PostResultResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LeaderboardApi {
    @GET("leaderboard")
    suspend fun getLeaderboard(
        @Query("category") category: Int = 1
    ): List<LeaderboardApiModel>

    @POST("leaderboard")
    suspend fun postResult(
        @Body result: PostResultApiModel
    ): PostResultResponse
}