package com.example.catapult.networking.di

import javax.inject.Qualifier

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthenticatedClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class UnauthenticatedClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CatApi

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LeaderboardApi
