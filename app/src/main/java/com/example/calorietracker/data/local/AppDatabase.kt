package com.example.calorietracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FoodEntity::class, DailyEntryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun dailyEntryDao(): DailyEntryDao
}
