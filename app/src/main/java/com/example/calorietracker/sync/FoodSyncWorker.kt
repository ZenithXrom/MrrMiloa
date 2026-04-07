package com.example.calorietracker.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.calorietracker.CalorieTrackerApp

class FoodSyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val app = applicationContext as CalorieTrackerApp
        val userId = inputData.getString(KEY_USER_ID) ?: return Result.success()

        return runCatching {
            app.syncRepository.syncPendingFoods(userId)
        }.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }

    companion object {
        const val KEY_USER_ID = "user_id"
    }
}
