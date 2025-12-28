package com.example.opinia.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.model.Course
import com.example.opinia.data.repository.AvatarProvider
import com.example.opinia.data.repository.CourseRepository
import com.example.opinia.data.repository.FacultyDepartmentRepository
import com.example.opinia.data.repository.StudentRepository
import com.example.opinia.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedCoursesUiState(
    val isLoading: Boolean = false,
    val savedCourses: List<Course> = emptyList(),
    val searchQuery: String = "",
    val avatarResId: Int? = null,
    val departmentName: String = "",
    val temporarilyUnsavedIds: Set<String> = emptySet()
)

sealed class SavedCoursesUiEvent {
    data class CourseSavedOrUnsavedSuccessfully(val message: String) : SavedCoursesUiEvent()
    data class CoursesLoaded(val courses: List<Course>) : SavedCoursesUiEvent()
    data class ErrorLoadingCourses(val message: String) : SavedCoursesUiEvent()
}

@HiltViewModel
class SavedCoursesViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
    private val avatarProvider: AvatarProvider,
    private val facultyDepartmentRepository: FacultyDepartmentRepository,
    private val networkManager: NetworkManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(SavedCoursesUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<SavedCoursesUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var originalCourseList: List<Course> = emptyList()

    init {
        loadSavedCourses()
    }

    private fun loadSavedCourses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            if (!networkManager.isInternetAvailable()) {
                handleError("No internet connection")
                return@launch
            }
            val studentResult = studentRepository.getStudentProfile()
            if (studentResult.isSuccess) {
                val student = studentResult.getOrNull()
                if (student != null) {
                    val convertedId = avatarProvider.getAvatarResId(student.studentProfileAvatar)
                    val deptName = try {
                        val deptResult = facultyDepartmentRepository.getDepartmentById(student.departmentID)
                        deptResult.getOrNull()?.departmentName ?: ""
                    } catch (e: Exception) {
                        ""
                    }
                    _uiState.update {
                        it.copy(
                            avatarResId = convertedId,
                            departmentName = deptName
                        )
                    }
                    val ids = student.savedCourseIds
                    if (ids.isEmpty()) {
                        _uiState.update {
                            it.copy(isLoading = false, savedCourses = emptyList())
                        }
                    } else {
                        val coursesResult = courseRepository.getCoursesByIds(ids)
                        if (coursesResult.isSuccess) {
                            val courses = coursesResult.getOrNull() ?: emptyList()
                            originalCourseList = courses
                            _uiState.update {
                                it.copy(isLoading = false, savedCourses = courses)
                            }
                            _uiEvent.send(SavedCoursesUiEvent.CoursesLoaded(courses))
                        } else {
                            val errorMsg = coursesResult.exceptionOrNull()?.message ?: "Course details loading failed"
                            handleError(errorMsg)
                        }
                    }
                } else {
                    handleError("Student not found")
                }
            } else {
                val errorMsg = studentResult.exceptionOrNull()?.message ?: "Student details loading failed"
                handleError(errorMsg)
            }
        }
    }

    private suspend fun handleError(errorMsg: String) {
        _uiState.update { it.copy(isLoading = false) }
        _uiEvent.send(SavedCoursesUiEvent.ErrorLoadingCourses(errorMsg))
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(savedCourses = originalCourseList) }
        } else {
            val filteredList = originalCourseList.filter { course ->
                course.courseName.contains(query, ignoreCase = true) ||
                        course.courseCode.contains(query, ignoreCase = true)
            }
            _uiState.update { it.copy(savedCourses = filteredList) }
        }
    }

    fun onToggleSaveCourse(courseId: String) {
        viewModelScope.launch {
            val currentUnsavedIds = _uiState.value.temporarilyUnsavedIds
            val courseCode = originalCourseList.find { it.courseId == courseId }?.courseCode ?: ""
            if (currentUnsavedIds.contains(courseId)) {
                val result = studentRepository.saveCourse(courseId)
                if (result.isSuccess) {
                    _uiState.update { it.copy(
                        temporarilyUnsavedIds = it.temporarilyUnsavedIds - courseId
                        )
                    }
                    _uiEvent.send(SavedCoursesUiEvent.CourseSavedOrUnsavedSuccessfully("${courseCode} saved successfully"))
                    delay(5000L)
                    loadSavedCourses()
                } else {
                    _uiEvent.send(SavedCoursesUiEvent.ErrorLoadingCourses("Could not save course"))
                }
            } else {
                val result = studentRepository.unsaveCourse(courseId)
                if (result.isSuccess) {
                    _uiState.update { it.copy(
                        temporarilyUnsavedIds = it.temporarilyUnsavedIds + courseId
                        )
                    }
                    _uiEvent.send(SavedCoursesUiEvent.CourseSavedOrUnsavedSuccessfully("${courseCode} unsaved successfully"))
                    delay(5000L)
                    loadSavedCourses()
                } else {
                    _uiEvent.send(SavedCoursesUiEvent.ErrorLoadingCourses("Could not unsave course"))
                }
            }
        }
    }

}