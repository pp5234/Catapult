package com.example.catapult.db

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class JsonTypeConvertor {

    private companion object {
        private val Json = Json {
            ignoreUnknownKeys = true
            prettyPrint = false
        }
    }

    @TypeConverter
    fun jsonObjectToString(jsonObject: JsonObject?): String? {
        return when(jsonObject) {
            null -> null
            else -> Json.encodeToString(jsonObject)
        }
    }

    @TypeConverter
    fun stringToJsonObject(value: String?): JsonObject? {
        return value?.let { Json.decodeFromString(it) }
    }

}