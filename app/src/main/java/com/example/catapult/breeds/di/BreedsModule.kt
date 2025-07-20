package com.example.catapult.breeds.di

import com.example.catapult.breeds.api.BreedsApi
import com.example.catapult.networking.di.CatApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object BreedsModule {

    @Provides @Singleton
    fun provideCatApi(@CatApi retrofit: Retrofit): BreedsApi =
        retrofit.create(BreedsApi::class.java)
}