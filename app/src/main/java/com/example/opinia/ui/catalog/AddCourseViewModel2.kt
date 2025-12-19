package com.example.opinia.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.model.Course
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
import com.example.opinia.data.repository.AvatarProvider
import com.example.opinia.data.repository.CourseRepository
import com.example.opinia.data.repository.FacultyDepartmentRepository
import com.example.opinia.data.repository.StudentRepository
import com.example.opinia.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


enum class AddCourseStep {
    FACULTY_SELECTION, // Fakülte ve Bölüm Seçimi
    COURSE_SELECTION   // Ders Seçimi
}

data class AddCourseUiState(
    val currentStep: AddCourseStep = AddCourseStep.FACULTY_SELECTION, // Enum kullanıyoruz
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
    private val networkManager: NetworkManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCourseUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<AddCourseUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Orijinal listeleri hafızada tutuyoruz (Arama performansı için)
    private var originalFaculties: List<Faculty> = emptyList()
    private var originalDepartmentCourses: List<Course> = emptyList()

    init {
        fetchFaculties()
        fetchStudentEnrolledCourses()
        fetchStudentAvatarId()
    }

    // --- VERİ ÇEKME ---

    private fun fetchFaculties() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = facultyDepartmentRepository.getAllFaculties()

            if (result.isSuccess) {
                val faculties = result.getOrDefault(emptyList())
                originalFaculties = faculties
                _uiState.update {
                    it.copy(availableFaculties = faculties, isLoading = false)
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                sendError("Fakülteler yüklenemedi.")
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
                    it.copy(isLoading = false, availableCourses = courses)
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                sendError("Dersler yüklenemedi.")
            }
        }
    }

    // --- KULLANICI ETKİLEŞİMLERİ ---

    fun onFacultySelected(faculty: Faculty) {
        _uiState.update {
            it.copy(
                selectedFaculty = faculty,
                selectedDepartment = null,
                availableDepartments = emptyList(),
                searchQuery = ""
            )
        }
        fetchDepartments(faculty.facultyId)
    }

    fun onDepartmentSelected(department: Department) {
        _uiState.update {
            it.copy(
                selectedDepartment = department,
                availableCourses = emptyList(),
                searchQuery = "",
                currentStep = AddCourseStep.COURSE_SELECTION // Enum ile geçiş
            )
        }
        fetchCourses(department.departmentId)
    }

    fun onBackToSelection() {
        _uiState.update {
            it.copy(
                currentStep = AddCourseStep.FACULTY_SELECTION,
                searchQuery = "",
                availableCourses = emptyList(),
                availableFaculties = originalFaculties // Listeyi sıfırla
            )
        }
    }

    // 2. ADIM: Arama işlemini optimize ettik (Dispatchers.Default)
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        val currentState = _uiState.value

        // Arama işlemi CPU yoğun olabilir, UI thread'i kilitlememek için Default dispatcher kullanıyoruz
        viewModelScope.launch(Dispatchers.Default) {
            if (currentState.currentStep == AddCourseStep.FACULTY_SELECTION) {
                val filtered = if (query.isBlank()) originalFaculties else {
                    originalFaculties.filter { it.facultyName.contains(query, ignoreCase = true) }
                }
                _uiState.update { it.copy(availableFaculties = filtered) }
            } else {
                val filtered = if (query.isBlank()) originalDepartmentCourses else {
                    originalDepartmentCourses.filter {
                        it.courseName.contains(query, ignoreCase = true) ||
                                it.courseCode.contains(query, ignoreCase = true)
                    }
                }
                _uiState.update { it.copy(availableCourses = filtered) }
            }
        }
    }

    // --- DERS EKLEME / ÇIKARMA ---

    fun onAddCourseClicked(course: Course) {
        if (!checkInternet()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = studentRepository.enrollStudentToCourse(course.courseId)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, enrolledCourseIds = it.enrolledCourseIds + course.courseId)
                }
                _uiEvent.send(AddCourseUiEvent.CourseAddedSuccess("${course.courseCode} eklendi"))
            } else {
                _uiState.update { it.copy(isLoading = false) }
                sendError("Ders eklenemedi")
            }
        }
    }

    fun onRemoveCourseClicked(course: Course) {
        if (!checkInternet()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = studentRepository.dropStudentFromCourse(course.courseId)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, enrolledCourseIds = it.enrolledCourseIds - course.courseId)
                }
                _uiEvent.send(AddCourseUiEvent.CourseAddedSuccess("${course.courseCode} çıkarıldı"))
            } else {
                _uiState.update { it.copy(isLoading = false) }
                sendError("Ders çıkarılamadı")
            }
        }
    }

    // --- YARDIMCI FONKSİYONLAR ---

    private fun fetchStudentEnrolledCourses() {
        viewModelScope.launch {
            studentRepository.getCurrentUserId()?.let { uid ->
                studentRepository.getEnrolledCoursesIds(uid).onSuccess { ids ->
                    _uiState.update { it.copy(enrolledCourseIds = ids) }
                }
            }
        }
    }

    private fun fetchStudentAvatarId() {
        viewModelScope.launch {
            studentRepository.getStudentProfile().onSuccess { student ->
                student?.let {
                    val resId = avatarProvider.getAvatarResId(it.studentProfileAvatar)
                    _uiState.update { state -> state.copy(avatarResId = resId) }
                }


        }
            }
        }


    private fun checkInternet(): Boolean {
        if (!networkManager.isInternetAvailable()) {
            sendError("İnternet bağlantısı yok")
            return false
        }
        return true
    }

    private fun sendError(msg: String) {
        viewModelScope.launch {
            _uiEvent.send(AddCourseUiEvent.Error(msg))
        }
    }
}