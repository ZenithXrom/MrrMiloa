package com.example.calorietracker.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RemoteClients {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val openFoodFactsApi: OpenFoodFactsApi by lazy {
        retrofit("https://world.openfoodfacts.org/")
            .create(OpenFoodFactsApi::class.java)
    }

    val usdaApi: UsdaApi by lazy {
        retrofit("https://api.nal.usda.gov/")
            .create(UsdaApi::class.java)
    }

    val edamamApi: EdamamApi by lazy {
        retrofit("https://api.edamam.com/")
            .create(EdamamApi::class.java)
    }
}
