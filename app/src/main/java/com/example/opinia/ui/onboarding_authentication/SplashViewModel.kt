package com.example.opinia.ui.onboarding_authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.repository.AuthRepository
import com.example.opinia.data.repository.AuthState
import com.example.opinia.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashUiState {
    data object Loading : SplashUiState()
    data object NoInternet : SplashUiState()
    data object GoToChooseLoginOrSignup : SplashUiState()
    data object GoToDashboard : SplashUiState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(private val networkManager: NetworkManager, private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init{
        checkAppStartConditions()
    }

    fun checkAppStartConditions() {
        viewModelScope.launch {
            _uiState.value = SplashUiState.Loading
            if (!networkManager.isInternetAvailable()) {
                _uiState.value = SplashUiState.NoInternet
                delay(5000) //5 saniye bekle
                checkAppStartConditions()
                return@launch
            }
            authRepository.authState.collect { authState ->
                _uiState.value = when (authState) {
                    is AuthState.Authenticated -> SplashUiState.GoToDashboard
                    is AuthState.Unauthenticated -> SplashUiState.GoToChooseLoginOrSignup
                    is AuthState.Loading -> SplashUiState.Loading
                }
            }
        }
    }

}