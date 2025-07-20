package com.example.catapult.quiz.model

import com.example.catapult.quiz.start.QuizType

data class QuizUiModel(
    val type: QuizType,
    val questions: List<QuizQuestionUiModel>
)
