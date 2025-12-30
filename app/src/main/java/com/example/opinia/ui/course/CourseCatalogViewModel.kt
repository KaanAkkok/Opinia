package com.example.opinia.ui.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.model.Course
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
import com.example.opinia.data.repository.AvatarProvider
import com.example.opinia.data.repository.CourseRepository
import com.example.opinia.data.repository.FacultyDepartmentRepository
import com.example.opinia.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseCatalogUiState(
    val step: Int = 1,
    val avatarResId: Int? = null,
    val availableFaculties: List<Faculty> = emptyList(),
    val selectedFaculty: Faculty? = null,
    val availableDepartments: List<Department> = emptyList(),
    val selectedDepartment: Department? = null,
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)

sealed class CourseCatalogUiEvent {
    data class Error(val message: String): CourseCatalogUiEvent()
}

@HiltViewModel
class CourseCatalogViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
    private val facultyDepartmentRepository: FacultyDepartmentRepository,
    private val avatarProvider: AvatarProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseCatalogUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<CourseCatalogUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var originalDepartmentCourses: List<Course> = emptyList()
    private var originalFaculties: List<Faculty> = emptyList()

    init {
        fetchFaculties()
        fetchStudentAvatarId()
    }

    private fun fetchFaculties() {
        viewModelScope.launch {
            val result = facultyDepartmentRepository.getAllFaculties()
            if (result.isSuccess) {
                val faculties = result.getOrDefault(emptyList())
                originalFaculties = faculties
                _uiState.update { it.copy(availableFaculties = faculties) }
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
                        val convertedId = avatarProvider.getAvatarResId(student.studentProfileAvatar)
                        _uiState.update { it.copy(avatarResId = convertedId) }
                    }
                }
            }
        }
    }

    fun onFacultySelected(faculty: Faculty) {
        _uiState.update {
            it.copy(
                selectedFaculty = faculty,
                selectedDepartment = null,
                availableDepartments = emptyList(),
                searchQuery = "",
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

    fun onDepartmentSelected(department: Department) {
        _uiState.update {
            it.copy(
                selectedDepartment = department,
                courses = emptyList(),
                searchQuery = "",
                step = 2
            )
        }
        fetchCourses(department.departmentId)
    }

    fun onBackToSelection() {
        _uiState.update {
            it.copy(
                step = 1,
                searchQuery = "",
                courses = emptyList(),
                availableFaculties = originalFaculties
            )
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
                        courses = courses
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /*
    EĞER ESKİ SEARCHBAR KULLANILACAKSA BUNU KULLAN
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        val currentStep = _uiState.value.step
        if (currentStep == 1) {
            // STEP 1: Fakülte Filtreleme
            if (query.isBlank()) {
                _uiState.update { it.copy(availableFaculties = originalFaculties) }
            } else {
                val filteredFaculties = originalFaculties.filter {
                    it.facultyName.contains(query, ignoreCase = true)
                }
                _uiState.update { it.copy(availableFaculties = filteredFaculties) }
            }
        } else {
            // STEP 2: Ders Filtreleme
            if (query.isBlank()) {
                _uiState.update { it.copy(courses = originalDepartmentCourses) }
            } else {
                val filteredCourses = originalDepartmentCourses.filter { course ->
                    course.courseName.contains(query, ignoreCase = true) ||
                            course.courseCode.contains(query, ignoreCase = true)
                }
                _uiState.update { it.copy(courses = filteredCourses) }
            }
        }
    }
    */

}