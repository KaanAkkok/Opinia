package com.example.opinia.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState (
    val isLoading: Boolean = false
)


sealed class ProfileUiEvent {
    data object LogoutSuccess: ProfileUiEvent()
    data class LogoutError(val message: String): ProfileUiEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ProfileUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onLogoutClicked() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            try {
                authRepository.logout()
                _uiEvent.send(ProfileUiEvent.LogoutSuccess)
            } catch (e: Exception) {
                _uiEvent.send(ProfileUiEvent.LogoutError(e.message ?: "Could not log out"))
            }
            finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }

        }
    }

}