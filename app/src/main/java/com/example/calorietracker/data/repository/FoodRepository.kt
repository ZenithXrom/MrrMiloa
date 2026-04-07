package com.example.calorietracker.data.repository

import com.example.calorietracker.data.local.DailyEntryDao
import com.example.calorietracker.data.local.DailyEntryEntity
import com.example.calorietracker.data.local.FoodDao
import com.example.calorietracker.data.local.FoodEntity
import com.example.calorietracker.data.model.FoodItem
import com.example.calorietracker.data.model.toEntity
import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val foodDao: FoodDao,
    private val dailyEntryDao: DailyEntryDao
) {
    fun foodsByDate(date: String): Flow<List<FoodEntity>> = foodDao.getFoodsByDate(date)

    fun totalCaloriesByDate(date: String): Flow<Int> = foodDao.getTotalCaloriesByDate(date)

    fun dailyEntry(date: String): Flow<DailyEntryEntity?> = dailyEntryDao.getEntryByDate(date)

    suspend fun addFood(name: String, calories: Int, date: String) {
        foodDao.insertFood(FoodEntity(name = name, calories = calories, date = date, isSynced = false))
    }

    suspend fun addFood(foodItem: FoodItem, date: String) {
        foodDao.insertFood(foodItem.toEntity(date))
    }

    suspend fun ensureDailyGoal(date: String, goal: Int = 2200) {
        dailyEntryDao.upsertEntry(DailyEntryEntity(date = date, calorieGoal = goal))
    }
}
