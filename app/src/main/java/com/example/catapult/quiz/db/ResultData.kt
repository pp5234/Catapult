package com.example.catapult.quiz.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "result")
data class Result(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val score: Double,
    val correctAnswers: Int,
    val timeSpent: Int,
    val isPublished: Boolean,
    val globalRanking: Int?,
    val createdAt: Long = System.currentTimeMillis(),
    val publishedAt: Long? = null
)