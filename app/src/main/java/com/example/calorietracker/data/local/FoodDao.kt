package com.example.calorietracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(foodEntity: FoodEntity)

    @Query("SELECT * FROM foods WHERE date = :date ORDER BY createdAt DESC")
    fun getFoodsByDate(date: String): Flow<List<FoodEntity>>

    @Query("SELECT COALESCE(SUM(calories), 0) FROM foods WHERE date = :date")
    fun getTotalCaloriesByDate(date: String): Flow<Int>

    @Query("SELECT * FROM foods WHERE isSynced = 0 ORDER BY createdAt ASC")
    suspend fun getPendingSyncFoods(): List<FoodEntity>

    @Query("UPDATE foods SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)
}
