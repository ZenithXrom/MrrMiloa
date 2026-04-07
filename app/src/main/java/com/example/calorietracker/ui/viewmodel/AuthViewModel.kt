package com.example.calorietracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calorietracker.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = ""
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            isLoggedIn = authRepository.isLoggedIn,
            userName = authRepository.currentUserName
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun googleSignInClient() = authRepository.getGoogleSignInClient()

    fun signInWithGoogleToken(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.signInWithGoogleIdToken(idToken)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userName = authRepository.currentUserName
                    )
                }
                .onFailure { ex ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ex.message ?: "Error de autenticación"
                    )
                }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }
}

class AuthViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
