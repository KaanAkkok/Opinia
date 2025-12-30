package com.example.opinia.ui.onboarding_authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.model.Course
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
import com.example.opinia.data.model.Student
import com.example.opinia.data.repository.AuthRepository
import com.example.opinia.data.repository.AvatarProvider
import com.example.opinia.data.repository.CourseRepository
import com.example.opinia.data.repository.FacultyDepartmentRepository
import com.example.opinia.data.repository.StudentRepository
import com.example.opinia.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    // sayfa 1 temel bilgiler
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val password: String = "",
    val passwordDummy: String = "",
    val selectedAvatarId: String = "",
    val isWaitingForEmailVerification: Boolean = false,
    val isResendButtonEnabled: Boolean = true,
    val resendCooldown: Int = 0,
    // sayfa 3 akademik bilgiler
    val availableFaculties: List<Faculty> = emptyList(), // backendden gelecek liste
    val selectedFaculty: Faculty? = null,
    val availableDepartments: List<Department> = emptyList(), // backendden gelecek liste
    val selectedDepartment: Department? = null,
    val selectedStdYear: String = "",
    // sayfa 4 ders seçimi
    val availableCourses: List<Course> = emptyList(), // backendden gelecek liste
    val selectedCourses: List<Course> = emptyList(), // kullanıcının seçtikleri
    // Genel Durumlar
    val isLoading: Boolean = false
)

sealed class RegisterUiEvent {
    data object SignupSuccess : RegisterUiEvent() // completeRegistration() tamamlandıktan sonra
    data class SignupError(val message: String): RegisterUiEvent()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository, //giriş işlemleri için
    private val facultyDepartmentRepository: FacultyDepartmentRepository, //fakülte ve departmanları getirmek için
    private val avatarProvider: AvatarProvider, //avatarları getirmek için
    private val courseRepository: CourseRepository, //dersleri getirmek için
    private val studentRepository: StudentRepository, //öğrenci bilgilerini kaydetmek için
    private val networkManager: NetworkManager //internet bağlantısını kontrol etmek için
    ) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<RegisterUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var verificationJob: Job? = null
    private var resendTimerJob: Job? = null

    val allAvatars = avatarProvider.avatars //buradan avatarlara erişebiliriz

    init {
        fetchFaculties()
    }

    // 1. sayfa update fonksiyonu
    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onSurnameChange(surname: String) {
        _uiState.update { it.copy(surname = surname) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onPasswordDummyChange(passwordDummy: String) {
        _uiState.update { it.copy(passwordDummy = passwordDummy) }
    }

    fun selectAvatar(avatarId: String) {
        _uiState.update { it.copy(selectedAvatarId = avatarId) }
    }

    // 2. sayfa update fonksiyonu
    // 1. Tüm Fakülteleri Çek
    private fun fetchFaculties() {
        viewModelScope.launch {
            val result = facultyDepartmentRepository.getAllFaculties()
            if (result.isSuccess) {
                _uiState.update { it.copy(availableFaculties = result.getOrDefault(emptyList())) }
            }
        }
    }

    // 2. Fakülte Seçilince -> Departmanları Getir
    fun onFacultySelected(faculty: Faculty) {
        _uiState.update {
            it.copy(
                selectedFaculty = faculty,
                selectedDepartment = null, // Fakülte değişirse departman sıfırlanmalı
                availableDepartments = emptyList(),
                availableCourses = emptyList() // Dersler de sıfırlanmalı
            )
        }
        fetchDepartments(faculty.facultyId)
    }

    private fun fetchDepartments(facultyId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = facultyDepartmentRepository.getDepartmentsByFaculty(facultyId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    availableDepartments = result.getOrDefault(emptyList())
                )
            }
        }
    }

    // 3. Departman Seçilince -> Dersleri Getir
    fun onDepartmentSelected(department: Department) {
        _uiState.update {
            it.copy(
                selectedDepartment = department,
                availableCourses = emptyList(), // Yeni departman seçilince eski dersler silinmeli
                selectedCourses = emptyList()   // Seçili dersler de sıfırlanmalı
            )
        }
        fetchCourses(department.departmentId)
    }

    private fun fetchCourses(departmentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = courseRepository.getCoursesByDepartmentId(departmentId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    availableCourses = result.getOrDefault(emptyList())
                )
            }
        }
    }

    fun onYearSelected(year: String) {
        _uiState.update { it.copy(selectedStdYear = year) }
    }

    // 3. sayfa update fonksiyonu
    fun onCourseSelected(course: Course) {
        _uiState.update { currentState ->
            val updatedList = if (currentState.selectedCourses.contains(course)) {
                currentState.selectedCourses - course
            } else {
                currentState.selectedCourses + course
            }
            currentState.copy(selectedCourses = updatedList)
        }
    }

    // Kayıt İşlemleri tamamlanması

    fun validateStudentEmail(email: String): Boolean {
        if (email.isBlank()) {
            return false
        }
        else if (!email.endsWith("@std.yeditepe.edu.tr")) {
            return false
        }
        return true
    }

    fun validatePassword(password: String): Boolean {
        val hasMinLength = password.length >= 8
        val hasNumber = password.contains(Regex("[0-9]"))
        val hasUpperCase = password.contains(Regex("[A-Z]"))
        val hasLowerCase = password.contains(Regex("[a-z]"))
        val noSpaces = !password.contains(" ")
        return hasMinLength && hasNumber && hasUpperCase && hasLowerCase && noSpaces
    }

    private fun sendErrorEvent(msg: String) {
        viewModelScope.launch {
            _uiEvent.send(RegisterUiEvent.SignupError(msg))
        }
    }

    fun validateStep1(): Boolean {
        val state = uiState.value
        if (!networkManager.isInternetAvailable()) {
            sendErrorEvent("No internet connection")
            return false
        }
        if (state.name.isBlank() || state.surname.isBlank() || state.email.isBlank() || state.password.isBlank() || state.passwordDummy.isBlank()) {
            sendErrorEvent("Please fill in all fields")
            return false
        }
        if (!validateStudentEmail(state.email)) {
            sendErrorEvent("Please enter a valid student email")
            return false
        }
        if (!validatePassword(state.password)) {
            val errorMsg = "Password must be 8+ chars, 1 number, 1 uppercase, 1 lowercase & no spaces."
            sendErrorEvent(errorMsg)
            return false
        }
        if (state.password != state.passwordDummy) {
            sendErrorEvent("Passwords do not match")
            return false
        }
        if (state.selectedAvatarId.isEmpty()) {
            sendErrorEvent("Please select an avatar by clicking the icon above")
            return false
        }
        return true
    }

    fun validateStep2(): Boolean {
        val state = uiState.value
        if (!networkManager.isInternetAvailable()) {
            sendErrorEvent("No internet connection")
            return false
        }
        if (state.selectedFaculty == null) {
            sendErrorEvent("Please select a faculty")
            return false
        }
        if (state.selectedDepartment == null) {
            sendErrorEvent("Please select a department")
            return false
        }
        if (state.selectedStdYear.isEmpty()) {
            sendErrorEvent("Please select a year")
            return false
        }
        return true
    }

    fun completeRegistration() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            val state = uiState.value
            if (!networkManager.isInternetAvailable()) {
                _uiEvent.send(RegisterUiEvent.SignupError("No internet connection"))
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }
            val authResult = authRepository.signup(state.email, state.password)
            if (authResult.isSuccess) {
                if (state.email != "admin@std.yeditepe.edu.tr") { //admin için arka kapı
                    authRepository.sendVerificationEmail(state.email)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isWaitingForEmailVerification = true
                        )
                    }
                    startResendTimer()
                    startEmailVerificationCheck()
                }
                else {
                    saveStudentToFirestore()
                    _uiEvent.send(RegisterUiEvent.SignupSuccess)
                }
            }
            else {
                _uiState.update {
                    it.copy(isLoading = false)
                }
                _uiEvent.send(RegisterUiEvent.SignupError(authResult.exceptionOrNull()?.message ?: "Unknown error"))
            }

        }
    }

    private fun startEmailVerificationCheck() {
        verificationJob?.cancel()
        verificationJob = viewModelScope.launch {
            val timeOut = 5*60*1000L
            val startTime = System.currentTimeMillis()
            while(isActive) {
                if(System.currentTimeMillis() - startTime > timeOut) {
                    handleRegistrationFailure("Email verification timeout")
                    break
                }
                delay(3000)
                val isVerified = authRepository.checkEmailVerification().getOrDefault(false)
                if (isVerified) {
                    _uiState.update {
                        it.copy(
                            isWaitingForEmailVerification = false
                        )
                    }
                    saveStudentToFirestore()
                    break
                }
            }
        }
    }

    private suspend fun saveStudentToFirestore() {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }

        val state = uiState.value
        val uid = studentRepository.getCurrentUserId()

        if (uid != null) {
            val newStudent = Student(
                studentId = uid,
                studentEmail = state.email.trim(),
                studentName = state.name.trim(),
                studentSurname = state.surname.trim(),
                studentYear = state.selectedStdYear,
                facultyID = state.selectedFaculty?.facultyId ?: "",
                departmentID = state.selectedDepartment?.departmentId ?: "",
                studentProfileAvatar = state.selectedAvatarId,
                enrolledCourseIds = state.selectedCourses.map { it.courseId }, // Sadece ID'leri kaydediyoruz
                savedCourseIds = emptyList() // Başlangıçta boş bir liste
            )
            val dbResult = studentRepository.createStudent(newStudent)
            if (dbResult.isSuccess) {
                _uiEvent.send(RegisterUiEvent.SignupSuccess)
            } else {
                com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.delete()
                _uiEvent.send(RegisterUiEvent.SignupError("Student register failed: ${dbResult.exceptionOrNull()?.message}"))
            }
        }
        else {
            _uiEvent.send(RegisterUiEvent.SignupError("Student ID not found"))
        }

        _uiState.update { it.copy(isLoading = false) }
    }

    private suspend fun handleRegistrationFailure(msg: String) {
        verificationJob?.cancel()
        _uiState.update {
            it.copy(isLoading = true)
        }
        authRepository.deleteUnverifiedUser()
        _uiState.update {
            it.copy(
                isLoading = false,
                isWaitingForEmailVerification = false
            )
        }
        _uiEvent.send(RegisterUiEvent.SignupError(msg))
    }

    // email ekranına geri yolla
    fun onWrongEmailClicked() {
        viewModelScope.launch {
            verificationJob?.cancel()
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            try {
                authRepository.deleteUnverifiedUser()
            } catch (e: Exception) {
                _uiEvent.send(RegisterUiEvent.SignupError(e.message ?: "Failed to delete unverified user"))
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isWaitingForEmailVerification = false
                )
            }
        }
    }

    fun cancelRegistration() {
        viewModelScope.launch {
            verificationJob?.cancel()
            handleRegistrationFailure("Registration canceled")
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            if (!uiState.value.isResendButtonEnabled) return@launch
            val result = authRepository.sendVerificationEmail(email = uiState.value.email)
            if (result.isSuccess) {
                _uiEvent.send(RegisterUiEvent.SignupError("Verification email sent again!"))
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Failed to resend email"
                _uiEvent.send(RegisterUiEvent.SignupError(errorMsg))
            }
        }
    }

    private fun startResendTimer() {
        resendTimerJob?.cancel()
        resendTimerJob = viewModelScope.launch {
            _uiState.update {
                it.copy(isResendButtonEnabled = false, resendCooldown = 60)
            }
            for (i in 60 downTo 1) {
                delay(1000)
                _uiState.update { it.copy(resendCooldown = i - 1) }
            }
            _uiState.update {
                it.copy(isResendButtonEnabled = true, resendCooldown = 0)
            }
        }
    }

}