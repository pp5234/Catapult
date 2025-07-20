package com.example.catapult.quiz.model

data class QuizQuestionUiModel(
    val imageUrl: String,
    val options: List<String>,
    val correctAnswer: String,
    val breedTemperament: List<String> = emptyList()
)