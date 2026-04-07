package com.example.calorietracker.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class EdamamResponse(
    val parsed: List<EdamamParsedItem>?
)

data class EdamamParsedItem(
    val food: EdamamFood?,
    val quantity: Double?,
    val measure: EdamamMeasure?
)

data class EdamamFood(
    val foodId: String?,
    val label: String?,
    val nutrients: EdamamNutrients?
)

data class EdamamMeasure(
    val weight: Double?
)

data class EdamamNutrients(
    val ENERC_KCAL: Double?,
    val PROCNT: Double?,
    val CHOCDF: Double?,
    val FAT: Double?
)

interface EdamamApi {
    @GET("api/food-database/v2/parser")
    suspend fun parseText(
        @Query("ingr") ingredient: String,
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String
    ): EdamamResponse
}
