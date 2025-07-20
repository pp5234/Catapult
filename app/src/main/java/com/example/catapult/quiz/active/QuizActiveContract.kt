package com.example.catapult.quiz.active

import com.example.catapult.quiz.model.QuizQuestionUiModel
import com.example.catapult.quiz.start.QuizType

interface QuizQuestionContract {
    data class UiState(
        val questions: List<QuizQuestionUiModel> = emptyList(),
        val currentQuestionIndex: Int = 0,
        val userAnswers: Map<Int, String> = emptyMap(),
        val currentScore: Float = 0f,
        val finalScore: Float = 0f,
        val timeLeft: Int = 300,
        val isLoading: Boolean = false,
        val isQuizCompleted: Boolean = false,
        val showCancelDialog: Boolean = false,
        val error: String? = null,
        val navigateBack: Boolean = false
    )

    sealed class UiEvent {
        data class Initialize(val quizType: QuizType) : UiEvent()
        data class SelectAnswer(val answer: String) : UiEvent()
        object NextQuestion : UiEvent()
        object ShowCancelDialog : UiEvent()
        object HideCancelDialog : UiEvent()
        object CancelAndSaveQuiz : UiEvent()
        object SaveResultLocal : UiEvent()
        object SaveResultGlobal : UiEvent()
        object NavigationCompleted : UiEvent()
    }
}