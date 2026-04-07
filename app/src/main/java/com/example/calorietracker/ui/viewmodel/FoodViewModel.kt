package com.example.calorietracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calorietracker.data.local.FoodEntity
import com.example.calorietracker.data.model.FoodItem
import com.example.calorietracker.data.repository.FoodRepository
import com.example.calorietracker.data.repository.NutritionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class FoodInputMode {
    MANUAL,
    BARCODE,
    GENERIC_SEARCH,
    NATURAL_LANGUAGE
}

data class DashboardUiState(
    val date: String,
    val foodList: List<FoodEntity> = emptyList(),
    val consumedCalories: Int = 0,
    val goalCalories: Int = 2200,
    val lookupResults: List<FoodItem> = emptyList(),
    val lookupError: String? = null,
    val isLookupLoading: Boolean = false,
    val inputMode: FoodInputMode = FoodInputMode.MANUAL
) {
    val remainingCalories: Int
        get() = goalCalories - consumedCalories
}

class FoodViewModel(
    private val repository: FoodRepository,
    private val nutritionService: NutritionService
) : ViewModel() {

    private val currentDate = MutableStateFlow(LocalDate.now().toString())
    private val lookupResults = MutableStateFlow<List<FoodItem>>(emptyList())
    private val lookupError = MutableStateFlow<String?>(null)
    private val lookupLoading = MutableStateFlow(false)
    private val inputMode = MutableStateFlow(FoodInputMode.MANUAL)

    val uiState: StateFlow<DashboardUiState> = currentDate
        .flatMapLatest { date ->
            combine(
                repository.foodsByDate(date),
                repository.totalCaloriesByDate(date),
                repository.dailyEntry(date),
                lookupResults,
                lookupError,
                lookupLoading,
                inputMode
            ) { foods, total, dailyEntry, remoteResults, remoteError, remoteLoading, mode ->
                DashboardUiState(
                    date = date,
                    foodList = foods,
                    consumedCalories = total,
                    goalCalories = dailyEntry?.calorieGoal ?: 2200,
                    lookupResults = remoteResults,
                    lookupError = remoteError,
                    isLookupLoading = remoteLoading,
                    inputMode = mode
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState(date = LocalDate.now().toString())
        )

    init {
        ensureGoalForToday()
    }

    private fun ensureGoalForToday() {
        viewModelScope.launch {
            repository.ensureDailyGoal(currentDate.value)
        }
    }

    fun setInputMode(mode: FoodInputMode) {
        inputMode.value = mode
        lookupResults.value = emptyList()
        lookupError.value = null
    }

    fun addFood(name: String, calories: Int) {
        if (name.isBlank() || calories <= 0) return
        viewModelScope.launch {
            repository.addFood(name = name, calories = calories, date = currentDate.value)
        }
    }

    fun addFood(item: FoodItem) {
        viewModelScope.launch {
            repository.addFood(item, currentDate.value)
        }
    }

    fun fetchByBarcode(barcode: String) {
        viewModelScope.launch {
            lookupLoading.value = true
            lookupError.value = null
            runCatching {
                nutritionService.fetchByBarcode(barcode)
            }.onSuccess { result ->
                lookupResults.value = listOfNotNull(result)
            }.onFailure {
                lookupError.value = it.message ?: "Error consultando Open Food Facts"
            }
            lookupLoading.value = false
        }
    }

    fun searchGenericFood(query: String) {
        viewModelScope.launch {
            lookupLoading.value = true
            lookupError.value = null
            runCatching {
                nutritionService.searchGenericFood(query)
            }.onSuccess { result ->
                lookupResults.value = result
            }.onFailure {
                lookupError.value = it.message ?: "Error consultando USDA"
            }
            lookupLoading.value = false
        }
    }

    fun parseNaturalLanguage(text: String) {
        viewModelScope.launch {
            lookupLoading.value = true
            lookupError.value = null
            runCatching {
                nutritionService.parseNaturalLanguage(text)
            }.onSuccess { result ->
                lookupResults.value = result
            }.onFailure {
                lookupError.value = it.message ?: "Error consultando Edamam"
            }
            lookupLoading.value = false
        }
    }
}

class FoodViewModelFactory(
    private val repository: FoodRepository,
    private val nutritionService: NutritionService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(repository, nutritionService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
