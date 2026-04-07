package com.example.calorietracker.data.repository

import com.example.calorietracker.BuildConfig
import com.example.calorietracker.data.model.FoodItem
import com.example.calorietracker.data.model.FoodSource
import com.example.calorietracker.data.remote.EdamamApi
import com.example.calorietracker.data.remote.OpenFoodFactsApi
import com.example.calorietracker.data.remote.RemoteClients
import com.example.calorietracker.data.remote.UsdaApi

class NutritionService(
    private val openFoodFactsApi: OpenFoodFactsApi = RemoteClients.openFoodFactsApi,
    private val usdaApi: UsdaApi = RemoteClients.usdaApi,
    private val edamamApi: EdamamApi = RemoteClients.edamamApi,
    private val usdaApiKey: String = BuildConfig.USDA_API_KEY,
    private val edamamAppId: String = BuildConfig.EDAMAM_APP_ID,
    private val edamamAppKey: String = BuildConfig.EDAMAM_APP_KEY
) {

    suspend fun fetchByBarcode(barcode: String): FoodItem? {
        if (barcode.isBlank()) return null
        val response = openFoodFactsApi.getByBarcode(barcode)
        val product = response.product ?: return null
        val n = product.nutriments

        return FoodItem(
            source = FoodSource.OPEN_FOOD_FACTS,
            externalId = product.code,
            name = product.product_name ?: "Producto sin nombre",
            calories = (n?.energy_kcal_100g ?: 0.0).toInt(),
            protein = (n?.proteins_100g ?: 0.0).toFloat(),
            carbs = (n?.carbohydrates_100g ?: 0.0).toFloat(),
            fat = (n?.fat_100g ?: 0.0).toFloat()
        )
    }

    suspend fun searchGenericFood(query: String): List<FoodItem> {
        if (query.isBlank() || usdaApiKey.isBlank()) return emptyList()
        val response = usdaApi.searchFoods(query = query, apiKey = usdaApiKey)

        return response.foods.orEmpty().map { food ->
            val calories = nutrientValue(food.foodNutrients, "Energy")
            val protein = nutrientValue(food.foodNutrients, "Protein")
            val carbs = nutrientValue(food.foodNutrients, "Carbohydrate, by difference")
            val fat = nutrientValue(food.foodNutrients, "Total lipid (fat)")

            FoodItem(
                source = FoodSource.USDA,
                externalId = food.fdcId?.toString(),
                name = food.description ?: "Alimento USDA",
                calories = calories.toInt(),
                protein = protein.toFloat(),
                carbs = carbs.toFloat(),
                fat = fat.toFloat()
            )
        }
    }

    suspend fun parseNaturalLanguage(text: String): List<FoodItem> {
        if (text.isBlank() || edamamAppId.isBlank() || edamamAppKey.isBlank()) return emptyList()

        val response = edamamApi.parseText(
            ingredient = text,
            appId = edamamAppId,
            appKey = edamamAppKey
        )

        return response.parsed.orEmpty().mapNotNull { item ->
            val food = item.food ?: return@mapNotNull null
            val nutrients = food.nutrients
            FoodItem(
                source = FoodSource.EDAMAM,
                externalId = food.foodId,
                name = food.label ?: "Alimento Edamam",
                calories = (nutrients?.ENERC_KCAL ?: 0.0).toInt(),
                protein = (nutrients?.PROCNT ?: 0.0).toFloat(),
                carbs = (nutrients?.CHOCDF ?: 0.0).toFloat(),
                fat = (nutrients?.FAT ?: 0.0).toFloat()
            )
        }
    }

    private fun nutrientValue(nutrients: List<com.example.calorietracker.data.remote.UsdaNutrient>?, name: String): Double {
        return nutrients.orEmpty().firstOrNull { it.nutrientName == name }?.value ?: 0.0
    }
}
