package com.example.catapult.quiz.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResultDao {
    @Insert
    suspend fun insertResult(result: Result): Long

    @Insert
    suspend fun insertAllResults(results: List<Result>)

    @Query("SELECT * FROM result WHERE username = :username ORDER BY createdAt DESC")
    suspend fun getResultsForUser(username: String): List<Result>

    @Query("SELECT MIN(globalRanking) FROM result WHERE username = :username AND globalRanking IS NOT NULL")
    suspend fun getBestGlobalRankingForUser(username: String): Int?

}