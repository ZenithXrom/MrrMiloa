package com.example.calorietracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.calorietracker.data.model.FoodItem
import com.example.calorietracker.ui.viewmodel.FoodInputMode
import com.example.calorietracker.ui.viewmodel.FoodViewModel

@Composable
fun AddFoodScreen(foodViewModel: FoodViewModel, onSaved: () -> Unit) {
    val state by foodViewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var naturalText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Agregar alimento")
        FoodModeSelector(state.inputMode, foodViewModel::setInputMode)

        when (state.inputMode) {
            FoodInputMode.MANUAL -> {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Calorías") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Button(onClick = {
                    foodViewModel.addFood(name, calories.toIntOrNull() ?: 0)
                    onSaved()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Guardar manualmente")
                }
            }

            FoodInputMode.BARCODE -> {
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Código de barras") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Button(onClick = { foodViewModel.fetchByBarcode(barcode) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Buscar en Open Food Facts")
                }
            }

            FoodInputMode.GENERIC_SEARCH -> {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Buscar alimento genérico") }
                )
                Button(onClick = { foodViewModel.searchGenericFood(searchQuery) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Buscar en USDA")
                }
            }

            FoodInputMode.NATURAL_LANGUAGE -> {
                OutlinedTextField(
                    value = naturalText,
                    onValueChange = { naturalText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ej: 200g de pollo") }
                )
                Button(onClick = { foodViewModel.parseNaturalLanguage(naturalText) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Analizar con Edamam")
                }
            }
        }

        if (state.isLookupLoading) {
            CircularProgressIndicator()
        }

        state.lookupError?.let { Text(it) }

        if (state.lookupResults.isNotEmpty()) {
            Text("Resultados")
            SearchResults(state.lookupResults) { item ->
                foodViewModel.addFood(item)
                onSaved()
            }
        }
    }
}

@Composable
private fun FoodModeSelector(selected: FoodInputMode, onModeSelected: (FoodInputMode) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = selected == FoodInputMode.MANUAL, onClick = { onModeSelected(FoodInputMode.MANUAL) }, label = { Text("Manual") })
                FilterChip(selected = selected == FoodInputMode.BARCODE, onClick = { onModeSelected(FoodInputMode.BARCODE) }, label = { Text("Código") })
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = selected == FoodInputMode.GENERIC_SEARCH, onClick = { onModeSelected(FoodInputMode.GENERIC_SEARCH) }, label = { Text("USDA") })
                FilterChip(selected = selected == FoodInputMode.NATURAL_LANGUAGE, onClick = { onModeSelected(FoodInputMode.NATURAL_LANGUAGE) }, label = { Text("Texto") })
            }
        }
    }
}

@Composable
private fun SearchResults(items: List<FoodItem>, onUseResult: (FoodItem) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        items(items.take(10)) { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${item.name} (${item.calories} kcal)")
                Button(onClick = { onUseResult(item) }) {
                    Text("Usar")
                }
            }
        }
    }
}
