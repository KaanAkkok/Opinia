package com.example.opinia.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.model.CommentReview
import com.example.opinia.data.model.Course
import com.example.opinia.data.repository.AvatarProvider
import com.example.opinia.data.repository.CommentReviewRepository
import com.example.opinia.data.repository.CourseRepository
import com.example.opinia.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PopularCourseAndComment(
    val Course: Course,
    val LatestComment: CommentReview,
    val CommenterName: String = "",
    val CommenterSurname: String = "",
    val CommenterAvatarResId: Int? = null,
    val CommenterYear: String = "",
    val CommenterRating: Int = 0,
    val CourseAverageRating: Double = 0.0,
)

data class DashboardUistate(
    val courses: List<PopularCourseAndComment> = emptyList(), //popüler derslerin listesi
    val studentName: String = "",
    val studentSurname: String = "",
    val avatarResId: Int? = null,
    val CurrentStudentCourses: List<Course> = emptyList(), //öğrencinin aldığı dersler
    val isLoading: Boolean = false
)

sealed class DashboardUiEvent {
    data class DashboardUiEventSuccess(val message: String) : DashboardUiEvent()
    data class DashboardUiEventError(val message: String) : DashboardUiEvent()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val avatarProvider: AvatarProvider,
    private val courseRepository: CourseRepository,
    private val commentReviewRepository: CommentReviewRepository,
    private val studentRepository: StudentRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUistate())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<DashboardUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        fetchStudentDetail()
        fetchPopularCourses()
    }

    private fun fetchStudentDetail() {
        viewModelScope.launch {
            val uid = studentRepository.getCurrentUserId()
            if (uid != null) {
                val result = studentRepository.getStudentProfile()
                if (result.isSuccess) {
                    val student = result.getOrNull()
                    if (student != null) {
                        val convertedId = avatarProvider.getAvatarResId(student.studentProfileAvatar)
                        val enrolledCoursesResult = courseRepository.getCoursesByIds(student.enrolledCourseIds)
                        val enrolledCourses = enrolledCoursesResult.getOrNull() ?: emptyList()
                        _uiState.update {
                            it.copy(
                                studentName = student.studentName,
                                studentSurname = student.studentSurname,
                                avatarResId = convertedId,
                                CurrentStudentCourses = enrolledCourses
                            )
                        }
                    }
                    else {
                        _uiEvent.send(DashboardUiEvent.DashboardUiEventError("Failed to fetch student profile"))
                    }
                }
                else {
                    _uiEvent.send(DashboardUiEvent.DashboardUiEventError("Failed to fetch student profile"))
                }
            }
            else {
                _uiEvent.send(DashboardUiEvent.DashboardUiEventError("User not logged in"))
            }
        }
    }

    private fun fetchPopularCourses(courseLimit: Int = 5, commentLimit: Int = 1) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val popularCoursesResult = courseRepository.getPopularCourses(courseLimit)
            if (popularCoursesResult.isSuccess) {
                val courses = popularCoursesResult.getOrNull() ?: emptyList()
                val filteredCourses = courses.filter { it.averageRating > 0.0 }
                val dashboardCoursesList = filteredCourses.map { course ->
                    // 1. Kurs için son yorumu çek
                    val latestCommentsResult = commentReviewRepository.getLatestCommentsByCourseId(course.courseId, commentLimit)
                    val latestComment = latestCommentsResult.getOrNull()?.firstOrNull()
                    // 2. yorumu yapan öğrenciyi çek
                    var commenterName = ""
                    var commenterSurname = ""
                    var commenterAvatarResId: Int? = null
                    var commenterYear = ""
                    var commenterRating = 0
                    if (latestComment != null) {
                        val studentResult = studentRepository.getStudentById(latestComment.studentId)
                        val student = studentResult.getOrNull()
                        if (student != null) {
                            commenterName = student.studentName
                            commenterSurname = student.studentSurname
                            commenterAvatarResId = avatarProvider.getAvatarResId(student.studentProfileAvatar)
                            commenterYear = student.studentYear
                            commenterRating = latestComment.rating
                        }
                    }
                    // 3. Tekil PopularCourseAndComment nesnesini oluştur ve listeye ekle
                    PopularCourseAndComment(
                        Course = course,
                        LatestComment = latestComment ?: CommentReview("", "",  "", 0, ""),
                        CommenterName = commenterName,
                        CommenterSurname = commenterSurname,
                        CommenterAvatarResId = commenterAvatarResId,
                        CommenterYear = commenterYear,
                        CommenterRating = commenterRating,
                        CourseAverageRating = course.averageRating,
                    )
                }
                // 4. Hazırladığımız listeyi UI State'e gönder
                _uiState.update {
                    it.copy(
                        courses = dashboardCoursesList,
                        isLoading = false
                    )
                }
            }
            else {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(DashboardUiEvent.DashboardUiEventError("Failed to fetch popular courses"))
            }
        }
    }
}