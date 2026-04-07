package com.example.calorietracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntry(entry: DailyEntryEntity)

    @Query("SELECT * FROM daily_entries WHERE date = :date LIMIT 1")
    fun getEntryByDate(date: String): Flow<DailyEntryEntity?>
}
