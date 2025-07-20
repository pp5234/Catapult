package com.example.catapult.quiz

import com.example.catapult.breeds.BreedsRepository
import com.example.catapult.breeds.db.Breed
import com.example.catapult.quiz.db.ResultDao
import com.example.catapult.quiz.model.QuizQuestionUiModel
import com.example.catapult.quiz.model.QuizUiModel
import com.example.catapult.quiz.start.QuizType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val breedsRepository: BreedsRepository,
    private val resultDao: ResultDao
) {
    suspend fun generateQuiz(
        quizType: QuizType,
        questionCount: Int = 20
    ): QuizUiModel = withContext(Dispatchers.IO) {
        val allBreeds = breedsRepository.getAllBreeds()
        val questions = mutableListOf<QuizQuestionUiModel>()
        val usedImages = mutableSetOf<String>()

        if (allBreeds.size < 4) {
            throw IllegalStateException("Not enough breeds available to generate quiz questions")
        }

        repeat(questionCount) { index ->
            val breeds = allBreeds.shuffled()
            val correctBreed = breeds.first()

            val images = breedsRepository.getImagesForBreed(correctBreed.id)
                .map { it.url }
                .filter { url -> url !in usedImages }

            if (images.isEmpty()) {
                val allImages = breedsRepository.getImagesForBreed(correctBreed.id).map { it.url }
                if (allImages.isNotEmpty()) {
                    val imageUrl = allImages.random()
                    usedImages.add(imageUrl)
                    questions += createQuestion(quizType, correctBreed, breeds, imageUrl)
                }
                return@repeat
            }

            val imageUrl = images.random().also { usedImages.add(it) }
            questions += createQuestion(quizType, correctBreed, breeds, imageUrl)
        }

        QuizUiModel(type = quizType, questions = questions)
    }

    private fun createQuestion(
        quizType: QuizType,
        correctBreed: Breed,
        allBreeds: List<Breed>,
        imageUrl: String
    ): QuizQuestionUiModel {
        return when (quizType) {
            QuizType.BREED -> {
                val incorrectNames = allBreeds
                    .filter { it.id != correctBreed.id }
                    .shuffled()
                    .take(3)
                    .map { it.name }
                val options = (incorrectNames + correctBreed.name).shuffled()

                QuizQuestionUiModel(
                    imageUrl = imageUrl,
                    options = options,
                    correctAnswer = correctBreed.name
                )
            }
            QuizType.TEMPERAMENT -> {
                val correctTemperaments = correctBreed.temperament
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                if (correctTemperaments.size < 3) {
                    return createQuestion(QuizType.BREED, correctBreed, allBreeds, imageUrl)
                }

                val otherBreeds = allBreeds.filter { it.id != correctBreed.id }
                val intruderTemperaments = otherBreeds
                    .flatMap { breed ->
                        breed.temperament.split(",").map { it.trim() }
                    }
                    .filter { it.isNotBlank() && it !in correctTemperaments }
                    .distinct()

                if (intruderTemperaments.isEmpty()) {
                    return createQuestion(QuizType.BREED, correctBreed, allBreeds, imageUrl)
                }

                val intruder = intruderTemperaments.random()
                val correctOptions = correctTemperaments.shuffled().take(3)
                val allOptions = (correctOptions + intruder).shuffled()

                QuizQuestionUiModel(
                    imageUrl = imageUrl,
                    options = allOptions,
                    correctAnswer = intruder
                )
            }
        }
    }

    suspend fun saveResult(
        username: String,
        score: Double,
        correctAnswers: Int,
        timeSpent: Int,
        isPublished: Boolean,
        globalRanking: Int? = null
    ) {
        resultDao.insertResult(
            com.example.catapult.quiz.db.Result(
                username = username,
                score = score,
                correctAnswers = correctAnswers,
                timeSpent = timeSpent,
                isPublished = isPublished,
                globalRanking = globalRanking
            )
        )
    }
}