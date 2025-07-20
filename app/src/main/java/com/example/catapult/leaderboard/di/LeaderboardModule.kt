package com.example.catapult.leaderboard.di


import com.example.catapult.leaderboard.api.LeaderboardApi
import com.example.catapult.networking.di.LeaderboardApi as LeaderboardApiAnnotation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LeaderboardModule {

    @Provides
    @Singleton
    fun provideLeaderboardApi(@LeaderboardApiAnnotation retrofit: Retrofit): LeaderboardApi =
        retrofit.create(LeaderboardApi::class.java)
}