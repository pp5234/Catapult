package com.example.catapult.networking.di

import com.example.catapult.breeds.api.BreedsApi
import com.example.catapult.networking.serialization.NetworkingJson
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    @Singleton
    @Provides
    @AuthenticatedClient
    fun AuthenticatedOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor {
            val updatedRequest = it.request().newBuilder()
                .addHeader("x-api-key", "0d6cb9d4-ea87-4d69-98bb-0642b8f4612f")
                .build()

            it.proceed(updatedRequest)
        }
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        )
        .build()

    @Singleton
    @Provides
    @UnauthenticatedClient
    fun UnauthenticatedOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor {
            val updatedRequest = it.request().newBuilder()
                .build()

            it.proceed(updatedRequest)
        }
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        )
        .build()

    @Singleton
    @Provides
    @CatApi
    fun provideCatApiRetrofitClient(
        @AuthenticatedClient okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(NetworkingJson.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Singleton
    @Provides
    @LeaderboardApi
    fun provideLeaderboardApiRetrofitClient(
        @UnauthenticatedClient okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://rma.finlab.rs/")
            .client(okHttpClient)
            .addConverterFactory(NetworkingJson.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
