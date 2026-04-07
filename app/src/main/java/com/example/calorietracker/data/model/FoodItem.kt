package com.example.calorietracker.data.model

import com.example.calorietracker.data.local.FoodEntity

data class FoodItem(
    val source: FoodSource,
    val externalId: String? = null,
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)

enum class FoodSource {
    OPEN_FOOD_FACTS,
    USDA,
    EDAMAM,
    MANUAL
}

fun FoodItem.toEntity(date: String): FoodEntity = FoodEntity(
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
    date = date,
    isSynced = false
)
