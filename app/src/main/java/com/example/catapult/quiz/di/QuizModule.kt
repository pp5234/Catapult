package com.example.catapult.quiz.di

import com.example.catapult.db.AppDatabase
import com.example.catapult.quiz.QuizRepository
import com.example.catapult.quiz.db.ResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuizModule {

    @Provides
    @Singleton
    fun provideResultDao(database: AppDatabase): ResultDao = database.ResultDao()

    @Provides
    @Singleton
    fun provideQuizRepository(
        breedsRepository: com.example.catapult.breeds.BreedsRepository,
        resultDao: ResultDao
    ): QuizRepository = QuizRepository(breedsRepository, resultDao)
}
