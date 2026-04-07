package com.example.calorietracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.calorietracker.ui.viewmodel.FoodViewModel
import kotlin.math.min

@Composable
fun DashboardScreen(
    foodViewModel: FoodViewModel,
    userName: String,
    onAddFood: () -> Unit,
    onOpenDiary: () -> Unit,
    onOpenProgress: () -> Unit,
    onLogout: () -> Unit
) {
    val state by foodViewModel.uiState.collectAsState()
    val progress = (state.consumedCalories.toFloat() / state.goalCalories.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Hola, $userName", style = MaterialTheme.typography.headlineSmall)
        Text("Fecha: ${state.date}")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Calorías restantes")
                CalorieCircle(progress = progress)
                Text("${state.remainingCalories} kcal")
                Text("Consumidas: ${state.consumedCalories} / ${state.goalCalories}")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Resumen del día", style = MaterialTheme.typography.titleMedium)
                Text("Comidas registradas: ${state.foodList.size}")
                Text("Última comida: ${state.foodList.firstOrNull()?.name ?: "Sin registros"}")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAddFood, modifier = Modifier.weight(1f)) { Text("Agregar") }
            Button(onClick = onOpenDiary, modifier = Modifier.weight(1f)) { Text("Diario") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onOpenProgress, modifier = Modifier.weight(1f)) { Text("Progreso") }
            Button(onClick = onLogout, modifier = Modifier.weight(1f)) { Text("Salir") }
        }
    }
}

@Composable
private fun CalorieCircle(progress: Float) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = Modifier
        .padding(16.dp)
        .height(180.dp)
        .fillMaxWidth()) {
        val stroke = 24f
        val diameter = min(size.width, size.height)
        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        drawArc(
            color = androidx.compose.ui.graphics.Color.LightGray,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}
