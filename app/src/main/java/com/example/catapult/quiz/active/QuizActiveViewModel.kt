package com.example.catapult.quiz.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catapult.leaderboard.api.LeaderboardApi
import com.example.catapult.quiz.QuizRepository
import com.example.catapult.quiz.model.QuizUiModel
import com.example.catapult.quiz.active.QuizQuestionContract.UiEvent
import com.example.catapult.quiz.active.QuizQuestionContract.UiState
import com.example.catapult.quiz.api.PostResultApiModel
import com.example.catapult.quiz.start.QuizType
import com.example.catapult.users.account.UserAccountStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizActiveViewModel @Inject constructor(
    private val repository: QuizRepository,
    private val leaderboardApi: LeaderboardApi,
    private val userAccountStore: UserAccountStore
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private lateinit var quiz: QuizUiModel

    fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.Initialize -> initializeQuiz(event.quizType)
            is UiEvent.SelectAnswer -> selectAnswer(event.answer)
            UiEvent.NextQuestion -> nextQuestion()
            UiEvent.ShowCancelDialog -> showCancelDialog()
            UiEvent.HideCancelDialog -> hideCancelDialog()
            UiEvent.CancelAndSaveQuiz -> cancelAndSaveQuiz()
            UiEvent.SaveResultLocal -> saveResultLocal()
            UiEvent.SaveResultGlobal -> saveResultGlobal()
            UiEvent.NavigationCompleted -> _state.value = _state.value.copy(navigateBack = false)
        }
    }

    private fun initializeQuiz(quizType: QuizType) {
        if (_state.value.questions.isNotEmpty()) return
        viewModelScope.launch {
            _state.value = UiState(isLoading = true)
            try {
                quiz = repository.generateQuiz(quizType)
                if (quiz.questions.isEmpty()) {
                    _state.value = UiState(
                        isLoading = false,
                        isQuizCompleted = true,
                        error = "No questions available for the quiz."
                    )
                    return@launch
                }
                _state.value = UiState(
                    questions = quiz.questions,
                    timeLeft = 300
                )
                startTimer()
            } catch (e: Exception) {
                _state.value = UiState(
                    isLoading = false,
                    error = "Error loading the quiz: ${e.message}"
                )
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.timeLeft > 0 && !_state.value.isQuizCompleted) {
                delay(1000)
                val current = _state.value
                if (!current.isQuizCompleted) {
                    _state.value = current.copy(timeLeft = current.timeLeft - 1)
                }
            }
            if (_state.value.timeLeft <= 0 && !_state.value.isQuizCompleted) finishQuiz()
        }
    }


    private fun selectAnswer(answer: String) {
        val current = _state.value
        if (current.isQuizCompleted || current.currentQuestionIndex >= current.questions.size) return
        val updatedAnswers = current.userAnswers.toMutableMap().apply {
            put(current.currentQuestionIndex, answer)
        }
        _state.value = current.copy(userAnswers = updatedAnswers)
        viewModelScope.launch {
            delay(500)  // Delay postoji zbog tranzicije izmedju pitanja
            if (!_state.value.isQuizCompleted) nextQuestion()
        }
    }

    private fun nextQuestion() {
        val current = _state.value
        val nextIndex = current.currentQuestionIndex + 1
        if (nextIndex >= current.questions.size) {
            finishQuiz()
        } else {
            _state.value = current.copy(currentQuestionIndex = nextIndex)
        }
    }

    private fun finishQuiz() {
        if (_state.value.isQuizCompleted) return
        timerJob?.cancel()
        viewModelScope.launch {
            val current = _state.value
            val correctCount = calculateCorrectAnswers(current)
            val score = calculateScore(correctCount, current.timeLeft)
            _state.value = current.copy(
                isQuizCompleted = true,
                finalScore = score,
                currentScore = score
            )
        }
    }

    private fun saveResultLocal() {
        viewModelScope.launch {
            repository.saveResult(
                username = userAccountStore.userAccount.first().username,
                score = _state.value.finalScore.toDouble(),
                correctAnswers = calculateCorrectAnswers(_state.value),
                timeSpent = 300 - _state.value.timeLeft,
                isPublished = false,
                globalRanking = null
            )
            _state.value = _state.value.copy(navigateBack = true)
        }
    }

    private fun saveResultGlobal() {
        viewModelScope.launch {
            val account = userAccountStore.userAccount.first()
            val scoreValue = _state.value.finalScore.toDouble()
            try {
                val response = leaderboardApi.postResult(
                    PostResultApiModel(
                        nickname = account.username,
                        result = scoreValue,
                        category = 1
                    )
                )
                repository.saveResult(
                    username = account.username,
                    score = scoreValue,
                    correctAnswers = calculateCorrectAnswers(_state.value),
                    timeSpent = 300 - _state.value.timeLeft,
                    isPublished = true,
                    globalRanking = response.ranking
                )
            } catch (e: Exception) {
                repository.saveResult(
                    username = account.username,
                    score = scoreValue,
                    correctAnswers = calculateCorrectAnswers(_state.value),
                    timeSpent = 300 - _state.value.timeLeft,
                    isPublished = false,
                    globalRanking = null
                )
            } finally {
                _state.value = _state.value.copy(navigateBack = true)
            }
        }
    }

    private fun cancelAndSaveQuiz() {
        timerJob?.cancel()
        viewModelScope.launch {
            val current = _state.value
            val correctCount = calculateCorrectAnswers(current)
            val score = calculateScore(correctCount, current.timeLeft)

            repository.saveResult(
                username = userAccountStore.userAccount.first().username,
                score = score.toDouble(),
                correctAnswers = correctCount,
                timeSpent = 300 - current.timeLeft,
                isPublished = false,
                globalRanking = null
            )

            _state.value = current.copy(
                showCancelDialog = false,
                navigateBack = true
            )
        }
    }

    private fun calculateCorrectAnswers(state: UiState): Int {
        return state.userAnswers.count { (index, answer) ->
            state.questions.getOrNull(index)?.correctAnswer == answer
        }
    }

    private fun calculateScore(correctAnswers: Int, timeLeft: Int): Float {
        val bto = correctAnswers.toFloat()
        val pvt = timeLeft.toFloat()
        val mvt = 300f
        val ubp = bto * 2.5f * (1 + (pvt + 120) / mvt)
        return ubp.coerceAtMost(100f)
    }

    private fun showCancelDialog() {
        _state.value = _state.value.copy(showCancelDialog = true)
    }

    private fun hideCancelDialog() {
        _state.value = _state.value.copy(showCancelDialog = false)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}