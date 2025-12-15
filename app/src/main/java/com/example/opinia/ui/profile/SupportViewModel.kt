package com.example.opinia.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.model.Student
import com.example.opinia.data.repository.StudentRepository
import com.example.opinia.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SupportUiState(
    val isLoading: Boolean = false
)

sealed class SupportUiEvent {
    data class OpenEmailClient(val email: String, val subject: String, val body: String) : SupportUiEvent()
    data object SupportSuccess: SupportUiEvent()
    data class SupportError(val message: String): SupportUiEvent()
}


@HiltViewModel
class SupportViewModel @Inject constructor(
    private val networkManager: NetworkManager,
    private val studentRepository: StudentRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<SupportUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val supportEmail = "support@opinia.com"

    fun onSupportClicked() {
        viewModelScope.launch {
            if (!networkManager.isInternetAvailable()) {
                _uiEvent.send(SupportUiEvent.SupportError("No internet connection"))
                return@launch
            }
            val uid = studentRepository.getCurrentUserId()
            if(uid == null) {
                _uiEvent.send(SupportUiEvent.SupportError("User authentication failed"))
                return@launch
            }
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = studentRepository.getStudentById(uid)
            if (result.isSuccess) {
                val currentUser = result.getOrNull()
                if (currentUser != null) {
                    val mailBody = """
                        
                        
                        
                        --------------------------------------
                        Please write your problem up here.
                        
                        User Information:
                        
                        User Mail: ${currentUser.studentEmail}
                        User Name: ${currentUser.studentName}
                        User Surname: ${currentUser.studentSurname}
                    """.trimIndent()

                    _uiEvent.send(SupportUiEvent.OpenEmailClient(
                        email = supportEmail,
                        subject = "Opinia Support Request",
                        body = mailBody
                    ))
                } else {
                    _uiEvent.send(SupportUiEvent.SupportError("Student profile not found"))
                }
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                _uiEvent.send(SupportUiEvent.SupportError("Data fetch error: $errorMessage"))
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

}