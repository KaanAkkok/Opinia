package com.example.opinia.ui.onboarding_authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.repository.AuthRepository
import com.example.opinia.data.repository.StudentRepository
import com.example.opinia.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class SplashUiState {
    data object Loading : SplashUiState()
    data object NoInternet : SplashUiState()
    data object GoToChooseLoginOrSignup : SplashUiState()
    data object GoToDashboard : SplashUiState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val networkManager: NetworkManager,
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init{
        checkAppStartConditions()
    }

    fun checkAppStartConditions() {
        viewModelScope.launch {
            _uiState.value = SplashUiState.Loading
            delay(1500)
            if (!networkManager.isInternetAvailable()) {
                _uiState.value = SplashUiState.NoInternet
                return@launch
            }

            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _uiState.value = SplashUiState.GoToChooseLoginOrSignup
                return@launch
            }
            try {
                currentUser.reload().await()
                if (!currentUser.isEmailVerified && currentUser.email != "admin@std.yeditepe.edu.tr") { //admin için arka kapı
                    Log.d("SplashVM", "Email not verified. Cleaning up.")
                    currentUser.delete().await()
                    _uiState.value = SplashUiState.GoToChooseLoginOrSignup
                }
                else {
                    val hasProfile = studentRepository.checkIfStudentExists(currentUser.uid).getOrDefault(false)
                    if (hasProfile) {
                        _uiState.value = SplashUiState.GoToDashboard
                    } else {
                        Log.e("SplashVM", "Profile missing. Cleaning up.")
                        currentUser.delete().await()
                        _uiState.value = SplashUiState.GoToChooseLoginOrSignup
                    }
                }
            } catch (e: Exception) {
                Log.e("SplashVM", "Session check failed", e)
                authRepository.logout()
                _uiState.value = SplashUiState.GoToChooseLoginOrSignup
            }
        }
    }

}