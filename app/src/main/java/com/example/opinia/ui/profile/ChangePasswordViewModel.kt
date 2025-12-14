package com.example.opinia.ui.profile

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

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false
)

sealed class ChangePasswordUiEvent {
    object PasswordChanged: ChangePasswordUiEvent()
    data class ChangePasswordError(val message: String): ChangePasswordUiEvent()
}

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkManager: NetworkManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ChangePasswordUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onOldPasswordChange(password: String) {
        _uiState.update { it.copy(oldPassword = password) }
    }

    fun onNewPasswordChange(password: String) {
        _uiState.update { it.copy(newPassword = password) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password) }
    }

    fun validateNewPassword(newPassword: String): Boolean {
        val hasMinLength = newPassword.length >= 8
        val hasNumber = newPassword.contains(Regex("[0-9]"))
        val hasUpperCase = newPassword.contains(Regex("[A-Z]"))
        val hasLowerCase = newPassword.contains(Regex("[a-z]"))
        val noSpaces = !newPassword.contains(" ")
        return hasMinLength && hasNumber && hasUpperCase && hasLowerCase && noSpaces
    }

    fun onChangeClicked() {
        viewModelScope.launch {
            if (!networkManager.isInternetAvailable()) {
                _uiEvent.send(ChangePasswordUiEvent.ChangePasswordError("No internet connection"))
                return@launch
            }
            if (!validateNewPassword(uiState.value.newPassword)) {
                _uiEvent.send(ChangePasswordUiEvent.ChangePasswordError("Password must be 8+ chars, with at least 1 number, 1 uppercase and 1 lowercase letter, and no spaces"))
                return@launch
            }
            if (uiState.value.newPassword != uiState.value.confirmPassword) {
                _uiEvent.send(ChangePasswordUiEvent.ChangePasswordError("Passwords do not match"))
                return@launch
            }
            if (uiState.value.newPassword == uiState.value.oldPassword) {
                _uiEvent.send(ChangePasswordUiEvent.ChangePasswordError("New password cannot be the same as the old password"))
                return@launch
            }
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = authRepository.updatePassword(
                    uiState.value.oldPassword,
                    uiState.value.newPassword
                )
                if (result.isSuccess) {
                    _uiEvent.send(ChangePasswordUiEvent.PasswordChanged)
                    _uiState.update { it.copy(oldPassword = "", newPassword = "", confirmPassword = "") }
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                    _uiEvent.send(ChangePasswordUiEvent.ChangePasswordError(errorMessage))
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}