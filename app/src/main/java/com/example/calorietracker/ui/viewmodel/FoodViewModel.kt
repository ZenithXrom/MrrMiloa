package com.example.calorietracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calorietracker.data.local.FoodEntity
import com.example.calorietracker.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DashboardUiState(
    val date: String,
    val foodList: List<FoodEntity> = emptyList(),
    val consumedCalories: Int = 0,
    val goalCalories: Int = 2200
) {
    val remainingCalories: Int
        get() = goalCalories - consumedCalories
}

class FoodViewModel(private val repository: FoodRepository) : ViewModel() {

    private val currentDate = MutableStateFlow(LocalDate.now().toString())

    val uiState: StateFlow<DashboardUiState> = currentDate
        .flatMapLatest { date ->
            combine(
                repository.foodsByDate(date),
                repository.totalCaloriesByDate(date),
                repository.dailyEntry(date)
            ) { foods, total, dailyEntry ->
                DashboardUiState(
                    date = date,
                    foodList = foods,
                    consumedCalories = total,
                    goalCalories = dailyEntry?.calorieGoal ?: 2200
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

    fun addFood(name: String, calories: Int) {
        if (name.isBlank() || calories <= 0) return
        viewModelScope.launch {
            repository.addFood(name = name, calories = calories, date = currentDate.value)
        }
    }

    fun changeDate(date: String) {
        currentDate.value = date
        viewModelScope.launch {
            repository.ensureDailyGoal(date)
        }
    }
}

class FoodViewModelFactory(private val repository: FoodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
