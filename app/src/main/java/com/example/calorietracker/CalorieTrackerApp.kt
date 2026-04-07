package com.example.calorietracker

import android.app.Application
import androidx.room.Room
import com.example.calorietracker.data.local.AppDatabase
import com.example.calorietracker.data.repository.AuthRepository
import com.example.calorietracker.data.repository.FoodRepository
import com.example.calorietracker.data.repository.SyncRepository

class CalorieTrackerApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var foodRepository: FoodRepository
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var syncRepository: SyncRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "calorie_tracker.db"
        ).fallbackToDestructiveMigration().build()

        foodRepository = FoodRepository(database.foodDao(), database.dailyEntryDao())
        authRepository = AuthRepository(this)
        syncRepository = SyncRepository(database.foodDao())
    }
}
