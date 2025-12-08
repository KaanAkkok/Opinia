package com.example.opinia.ui.onboarding_authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.repository.AuthRepository
import com.example.opinia.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState (
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)

sealed class LoginUiEvent {
    data object LoginSuccess: LoginUiEvent()
    data class LoginError(val message: String): LoginUiEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository, private val networkManager: NetworkManager) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<LoginUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(password = password)
        }
    }

    fun validateStudentEmail(email: String): Boolean {
        if (email.isBlank()) {
            return false
        }
        else if (!email.endsWith("@std.yeditepe.edu.tr")) {
            return false
        }
        return true
    }

    suspend fun loginUserUseCases(email: String, password: String): Result<Unit> {
        if (!networkManager.isInternetAvailable()) {
            return Result.failure(Exception("No internet connection"))
        }
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Please enter email or password"))
        }
        if (!validateStudentEmail(email)) {
            return Result.failure(Exception("Please enter a valid student email"))
        }
        return authRepository.login(email, password)
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val result = loginUserUseCases(uiState.value.email, uiState.value.password)

            if (result.isSuccess) {
                _uiEvent.send(LoginUiEvent.LoginSuccess)
            } else {
                _uiEvent.send(LoginUiEvent.LoginError(result.exceptionOrNull()?.message ?: "Unknown error"))
            }

            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

}

