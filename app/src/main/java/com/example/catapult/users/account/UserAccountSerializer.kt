package com.example.catapult.users.account

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object UserAccountSerializer : Serializer<UserAccount> {

    override val defaultValue: UserAccount = UserAccount()

    override suspend fun readFrom(input: InputStream): UserAccount {
        try {
            val jsonString = input.readBytes().decodeToString()
            if (jsonString.isEmpty()) {
                return defaultValue
            }
            return Json.decodeFromString(UserAccount.serializer(), jsonString)
        } catch (exception: SerializationException) {
            throw CorruptionException("Cannot read UserAccount from JSON.", exception)
        }
    }

    override suspend fun writeTo(t: UserAccount, output: OutputStream) {
        if (t.email.isEmpty() && t.firstName.isEmpty() && t.lastName.isEmpty() && t.username.isEmpty()) {
            output.write(byteArrayOf())
        } else {
            output.write(
                Json.encodeToString(UserAccount.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}