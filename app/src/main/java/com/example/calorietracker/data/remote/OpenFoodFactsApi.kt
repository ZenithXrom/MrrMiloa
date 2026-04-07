package com.example.calorietracker.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

data class OpenFoodFactsResponse(
    val status: Int,
    val product: OffProduct?
)

data class OffProduct(
    val code: String?,
    val product_name: String?,
    val nutriments: OffNutriments?
)

data class OffNutriments(
    val energy_kcal_100g: Double?,
    val proteins_100g: Double?,
    val carbohydrates_100g: Double?,
    val fat_100g: Double?
)

interface OpenFoodFactsApi {
    @GET("api/v2/product/{barcode}")
    suspend fun getByBarcode(@Path("barcode") barcode: String): OpenFoodFactsResponse
}
