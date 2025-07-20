package com.example.catapult.users.account

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserAccountModule {

    private val Context.userAccountDataStore: DataStore<UserAccount> by dataStore(
        fileName = "user_account.json",
        serializer = UserAccountSerializer
    )

    @Provides
    @Singleton
    fun provideUserAccountDataStore(
        @ApplicationContext context: Context
    ): DataStore<UserAccount> {
        return context.userAccountDataStore
    }
}