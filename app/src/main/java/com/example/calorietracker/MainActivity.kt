package com.example.calorietracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.calorietracker.ui.navigation.AppNavHost
import com.example.calorietracker.ui.theme.CalorieTrackerTheme
import com.example.calorietracker.ui.viewmodel.AuthViewModel
import com.example.calorietracker.ui.viewmodel.AuthViewModelFactory
import com.example.calorietracker.ui.viewmodel.FoodViewModel
import com.example.calorietracker.ui.viewmodel.FoodViewModelFactory

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((application as CalorieTrackerApp).authRepository)
    }

    private val foodViewModel: FoodViewModel by viewModels {
        FoodViewModelFactory((application as CalorieTrackerApp).foodRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalorieTrackerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(authViewModel = authViewModel, foodViewModel = foodViewModel)
                }
            }
        }
    }
}
