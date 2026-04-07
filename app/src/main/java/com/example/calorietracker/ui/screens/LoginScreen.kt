package com.example.calorietracker.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calorietracker.ui.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun LoginScreen(authViewModel: AuthViewModel, onSuccessLogin: () -> Unit) {
    val state by authViewModel.uiState.collectAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        runCatching { task.result.idToken }
            .onSuccess { token ->
                if (!token.isNullOrBlank()) authViewModel.signInWithGoogleToken(token)
            }
    }

    if (state.isLoggedIn) {
        onSuccessLogin()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CalorieTracker",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Inicia sesión con Google para guardar tu progreso",
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Button(
            onClick = {
                launcher.launch(authViewModel.googleSignInClient().signInIntent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar con Google")
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
