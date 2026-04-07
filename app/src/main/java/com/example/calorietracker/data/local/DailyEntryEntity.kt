package com.example.calorietracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_entries")
data class DailyEntryEntity(
    @PrimaryKey val date: String,
    val calorieGoal: Int = 2200,
    val updatedAt: Long = System.currentTimeMillis()
)
