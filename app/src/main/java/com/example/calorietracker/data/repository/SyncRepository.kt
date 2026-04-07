package com.example.calorietracker.data.repository

import com.example.calorietracker.data.local.FoodDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SyncRepository(
    private val foodDao: FoodDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun syncPendingFoods(userId: String) {
        val pending = foodDao.getPendingSyncFoods()
        pending.forEach { food ->
            val payload = mapOf(
                "name" to food.name,
                "calories" to food.calories,
                "protein" to food.protein,
                "carbs" to food.carbs,
                "fat" to food.fat,
                "date" to food.date,
                "createdAt" to food.createdAt,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(userId)
                .collection("foods")
                .document(food.id.toString())
                .set(payload)
                .await()

            foodDao.markAsSynced(food.id)
        }
    }
}
