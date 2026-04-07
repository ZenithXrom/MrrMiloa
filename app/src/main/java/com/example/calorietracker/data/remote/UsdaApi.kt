package com.example.calorietracker.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class UsdaSearchResponse(
    val foods: List<UsdaFood>?
)

data class UsdaFood(
    val fdcId: Long?,
    val description: String?,
    val foodNutrients: List<UsdaNutrient>?
)

data class UsdaNutrient(
    val nutrientName: String?,
    val value: Double?
)

interface UsdaApi {
    @GET("fdc/v1/foods/search")
    suspend fun searchFoods(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("pageSize") pageSize: Int = 10
    ): UsdaSearchResponse
}
