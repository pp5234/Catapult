package com.example.catapult.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.catapult.breeds.db.BreedsDao
import com.example.catapult.breeds.db.Breed
import com.example.catapult.breeds.db.Image
import com.example.catapult.breeds.db.ImageDao
import com.example.catapult.quiz.db.ResultDao
import com.example.catapult.quiz.db.Result

@Database(
    entities = [Breed::class, Image::class, Result::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(JsonTypeConvertor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun BreedsDao(): BreedsDao
    abstract fun ImageDao(): ImageDao
    abstract fun ResultDao(): ResultDao

}