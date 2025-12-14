package com.example.opinia.ui.profile

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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddCourseUiState(
    val step: Int = 1,
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

    private var originalDepartmentCourses: List<Course> = emptyList()

    init {
        fetchFaculties()
        fetchStudentEnrolledCourses()
        fetchStudentAvatarId()
    }

    private fun fetchFaculties() {
        viewModelScope.launch {
            val result = facultyDepartmentRepository.getAllFaculties()
            if (result.isSuccess) {
                _uiState.update { it.copy(availableFaculties = result.getOrDefault(emptyList())) }
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
                availableDepartments = emptyList()
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
                availableCourses = emptyList(),
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
                availableCourses = emptyList()
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
                        availableCourses = courses
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            _uiState.update { it.copy(availableCourses = originalDepartmentCourses) }
        } else {
            val filteredList = originalDepartmentCourses.filter { course ->
                course.courseName.contains(query, ignoreCase = true) ||
                        course.courseCode.contains(query, ignoreCase = true)
            }
            _uiState.update { it.copy(availableCourses = filteredList) }
        }
    }

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

    fun onRemoveCourseClicked(course: Course) {
        viewModelScope.launch {
            if (!networkManager.isInternetAvailable()) {
                _uiEvent.send(AddCourseUiEvent.Error("No internet connection"))
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            val result = studentRepository.dropStudentFromCourse(course.courseId)
            val isSuccess = result.isSuccess
            if (isSuccess) {
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
    }

}