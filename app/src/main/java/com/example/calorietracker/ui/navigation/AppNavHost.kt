package com.example.calorietracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.calorietracker.ui.screens.AddFoodScreen
import com.example.calorietracker.ui.screens.DashboardScreen
import com.example.calorietracker.ui.screens.DiaryScreen
import com.example.calorietracker.ui.screens.LoginScreen
import com.example.calorietracker.ui.screens.ProgressScreen
import com.example.calorietracker.ui.viewmodel.AuthViewModel
import com.example.calorietracker.ui.viewmodel.FoodViewModel

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val ADD_FOOD = "add_food"
    const val DIARY = "diary"
    const val PROGRESS = "progress"
}

@Composable
fun AppNavHost(authViewModel: AuthViewModel, foodViewModel: FoodViewModel) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) Routes.DASHBOARD else Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(authViewModel = authViewModel) {
                navController.navigate(Routes.DASHBOARD) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                foodViewModel = foodViewModel,
                userName = authState.userName,
                onAddFood = { navController.navigate(Routes.ADD_FOOD) },
                onOpenDiary = { navController.navigate(Routes.DIARY) },
                onOpenProgress = { navController.navigate(Routes.PROGRESS) },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(Routes.ADD_FOOD) {
            AddFoodScreen(foodViewModel = foodViewModel) {
                navController.popBackStack()
            }
        }
        composable(Routes.DIARY) {
            DiaryScreen(foodViewModel = foodViewModel) {
                navController.popBackStack()
            }
        }
        composable(Routes.PROGRESS) {
            ProgressScreen {
                navController.popBackStack()
            }
        }
    }
}
