package com.example.opinia.ui.catalog

import androidx.lifecycle.ViewModel




import androidx.lifecycle.viewModelScope
import com.example.opinia.data.model.Course
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
import com.example.opinia.data.repository.AvatarProvider
import com.example.opinia.data.repository.CourseRepository
import com.example.opinia.data.repository.FacultyDepartmentRepository
import com.example.opinia.data.repository.SeederRepository
import com.example.opinia.data.repository.StudentRepository
import com.example.opinia.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI Durumunu tutan Data Class
data class AddCourseUiState(
    val step: Int = 1, // 1: Fakülte/Bölüm Seçimi, 2: Ders Seçimi
    val avatarResId: Int? = null,
    val availableFaculties: List<Faculty> = emptyList(),
    val selectedFaculty: Faculty? = null,
    val availableDepartments: List<Department> = emptyList(),
    val selectedDepartment: Department? = null,
    val availableCourses: List<Course> = emptyList(),
    val enrolledCourseIds: List<String> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

// UI Olaylarını (Toast mesajları vb.) yöneten Sealed Class
sealed class AddCourseUiEvent {
    data class CourseAddedSuccess(val message: String): AddCourseUiEvent()
    data class Error(val message: String): AddCourseUiEvent()
}

@HiltViewModel
class AddCourseViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
    private val facultyDepartmentRepository: FacultyDepartmentRepository,
    private val avatarProvider: AvatarProvider,
    private val networkManager: NetworkManager,
    private val seederRepository: SeederRepository
) : ViewModel() {

    // StateFlow ile UI durumunu yönetiyoruz
    private val _uiState = MutableStateFlow(AddCourseUiState())
    val uiState = _uiState.asStateFlow()

    // Channel ile tek seferlik olayları (Toast gibi) yönetiyoruz
    private val _uiEvent = Channel<AddCourseUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Arama yaparken orijinal listeleri kaybetmemek için yedek listeler
    private var originalFaculties: List<Faculty> = emptyList()
    private var originalDepartmentCourses: List<Course> = emptyList()

    init {
        // ViewModel başladığında gerekli başlangıç verilerini çek
        fetchFaculties()
        fetchStudentEnrolledCourses()
        fetchStudentAvatarId()
    }

    // --- VERİ ÇEKME İŞLEMLERİ ---

    private fun fetchFaculties() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = facultyDepartmentRepository.getAllFaculties()

            if (result.isSuccess) {
                val faculties = result.getOrDefault(emptyList())
                originalFaculties = faculties
                _uiState.update {
                    it.copy(
                        availableFaculties = faculties,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AddCourseUiEvent.Error("Failed to load faculties"))
            }
        }
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

    private fun fetchCourses(departmentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = courseRepository.getCoursesByDepartmentId(departmentId)

            if (result.isSuccess) {
                val courses = result.getOrDefault(emptyList())
                originalDepartmentCourses = courses
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        availableCourses = courses
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AddCourseUiEvent.Error("Failed to load courses"))
            }
        }
    }

    private fun fetchStudentEnrolledCourses() {
        viewModelScope.launch {
            val uid = studentRepository.getCurrentUserId()
            if (uid != null) {
                val result = studentRepository.getEnrolledCoursesIds(uid)
                if (result.isSuccess) {
                    _uiState.update { it.copy(enrolledCourseIds = result.getOrDefault(emptyList())) }
                }
            }
        }
    }

    private fun fetchStudentAvatarId() {
        viewModelScope.launch {
            val uid = studentRepository.getCurrentUserId()
            if (uid != null) {
                val result = studentRepository.getStudentProfile()
                if (result.isSuccess) {
                    val student = result.getOrNull()
                    if (student != null) {
                        val convertedId =
                            avatarProvider.getAvatarResId(student.studentProfileAvatar)
                        _uiState.update { it.copy(avatarResId = convertedId) }
                    }
                }
            }
        }
    }

    // --- KULLANICI ETKİLEŞİMLERİ ---

    // 1. Adım: Fakülte Seçildiğinde
    fun onFacultySelected(faculty: Faculty) {
        _uiState.update {
            it.copy(
                selectedFaculty = faculty,
                selectedDepartment = null, // Bölüm seçimini sıfırla
                availableDepartments = emptyList(),
                searchQuery = "" // Aramayı temizle
            )
        }
        fetchDepartments(faculty.facultyId)
    }

    // 1. Adım: Bölüm Seçildiğinde -> 2. Adıma Geçiş
    fun onDepartmentSelected(department: Department) {
        _uiState.update {
            it.copy(
                selectedDepartment = department,
                availableCourses = emptyList(),
                searchQuery = "", // Aramayı temizle
                step = 2 // 2. Sayfaya geç
            )
        }
        fetchCourses(department.departmentId)
    }

    // Geri Tuşu: 2. Adımdan 1. Adıma Dönüş
    fun onBackToSelection() {
        _uiState.update {
            it.copy(
                step = 1,
                searchQuery = "",
                availableCourses = emptyList(),
                // Fakülteleri orijinal haline getir (eğer arama yapıldıysa)
                availableFaculties = originalFaculties
            )
        }
    }

    // Arama Çubuğu Mantığı
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        val currentStep = _uiState.value.step

        if (currentStep == 1) {
            // STEP 1: Fakülte Listesinde Arama
            if (query.isBlank()) {
                _uiState.update { it.copy(availableFaculties = originalFaculties) }
            } else {
                val filteredFaculties = originalFaculties.filter {
                    it.facultyName.contains(query, ignoreCase = true)
                }
                _uiState.update { it.copy(availableFaculties = filteredFaculties) }
            }
        } else {
            // STEP 2: Ders Listesinde Arama
            if (query.isBlank()) {
                _uiState.update { it.copy(availableCourses = originalDepartmentCourses) }
            } else {
                val filteredCourses = originalDepartmentCourses.filter { course ->
                    course.courseName.contains(query, ignoreCase = true) ||
                            course.courseCode.contains(query, ignoreCase = true)
                }
                _uiState.update { it.copy(availableCourses = filteredCourses) }
            }
        }
    }

    // Ders Ekleme
    fun onAddCourseClicked(course: Course) {
        viewModelScope.launch {
            if (!networkManager.isInternetAvailable()) {
                _uiEvent.send(AddCourseUiEvent.Error("No internet connection"))
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            val result = studentRepository.enrollStudentToCourse(course.courseId)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        enrolledCourseIds = it.enrolledCourseIds + course.courseId
                    )
                }
                _uiEvent.send(AddCourseUiEvent.CourseAddedSuccess("${course.courseCode} added"))
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AddCourseUiEvent.Error("Course enrollment failed"))
            }
        }
    }

    // Ders Çıkarma
    fun onRemoveCourseClicked(course: Course) {
        viewModelScope.launch {
            if (!networkManager.isInternetAvailable()) {
                _uiEvent.send(AddCourseUiEvent.Error("No internet connection"))
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            val result = studentRepository.dropStudentFromCourse(course.courseId)

            if (result.isSuccess) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        enrolledCourseIds = state.enrolledCourseIds - course.courseId
                    )
                }
                _uiEvent.send(AddCourseUiEvent.CourseAddedSuccess("${course.courseCode} removed"))
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AddCourseUiEvent.Error("Failed to remove course"))
            }
        }
    } //
}