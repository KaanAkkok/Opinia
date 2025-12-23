package com.example.opinia.ui.course

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.model.CommentReview
import com.example.opinia.data.repository.AvatarProvider
import com.example.opinia.data.repository.CommentReviewRepository
import com.example.opinia.data.repository.CourseRepository
import com.example.opinia.data.repository.StudentRepository
import com.example.opinia.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentAndStudent(
    val comment: CommentReview,
    val studentName: String = "",
    val studentSurname: String = "",
    val studentAvatarResId: Int? = null,
    val studentYear: String = ""
)

data class CourseDetailUiState(
    val courseId: String = "",
    val avatarResId: Int? = null,
    val courseCode: String = "",
    val courseName: String = "",
    val myReview: CommentAndStudent? = null, //öğrencinin yorumu ve ratingi
    val isCourseSaved: Boolean = false,
    val isStudentAlreadyCommented: Boolean = false,
    val isStudentTakingCourse: Boolean = false,
    val otherCommentsList: List<CommentAndStudent> = emptyList(), //diğer öğrencilerin yorumları
    val averageRating: Float = 0.0f,
    val totalReviewCount: Int = 0,
    val ratingDistribution: Map<Int, Int> = emptyMap(),
    val isLoading: Boolean = false,
)

sealed class CourseDetailUiEvent {
    data class Success(val message: String) : CourseDetailUiEvent()
    data class Error(val message: String) : CourseDetailUiEvent()
}

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository,
    private val avatarProvider: AvatarProvider,
    private val commentReviewRepository: CommentReviewRepository,
    private val networkManager: NetworkManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(CourseDetailUiState(courseId = courseId))
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<CourseDetailUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val jobs = listOf(
                launch { fetchStudentAvatarId() },
                launch { fetchCourseDetails() },
                launch { checkIfCourseSaved() },
                launch { checkIfCourseTaken() },
                launch { isStudentAlreadyCommented() },
                launch { fetchCommentsWithStudents() }
            )
            jobs.joinAll()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun fetchCourseDetails() {
        val result = courseRepository.getCourseById(courseId)
        if (result.isSuccess) {
            val course = result.getOrNull()
            if (course != null) {
                _uiState.update {
                    it.copy(
                        courseCode = course.courseCode,
                        courseName = course.courseName
                    )
                }
            }
        } else {
            _uiEvent.send(CourseDetailUiEvent.Error("Error fetching course details"))
        }
    }

    private suspend fun checkIfCourseSaved() {
        val result = studentRepository.getSavedCourseIds()
        if (result.isSuccess) {
            val savedCourseIds = result.getOrNull() ?: emptyList()
            val isSaved = savedCourseIds.contains(courseId)
            _uiState.update { it.copy(isCourseSaved = isSaved) }
        }
        else {
            _uiEvent.send(CourseDetailUiEvent.Error("Could not fetch saved courses"))
        }
    }

    private suspend fun fetchStudentAvatarId() {
        val uid = studentRepository.getCurrentUserId()
        if (uid != null) {
            val result = studentRepository.getStudentProfile()
            if (result.isSuccess) {
                val student = result.getOrNull()
                if (student != null) {
                    val convertedId = avatarProvider.getAvatarResId(student.studentProfileAvatar)
                    _uiState.update { it.copy(avatarResId = convertedId) }
                }
                else {
                    _uiEvent.send(CourseDetailUiEvent.Error("Failed to fetch student profile"))
                }
            }
            else {
                _uiEvent.send(CourseDetailUiEvent.Error("Failed to fetch student profile"))
            }
        }
        else {
            _uiEvent.send(CourseDetailUiEvent.Error("User not logged in"))
        }
    }

    private suspend fun checkIfCourseTaken() {
        val uid = studentRepository.getCurrentUserId()
        if (uid != null) {
            val result = studentRepository.getEnrolledCoursesIds(uid)
            if (result.isSuccess) {
                val enrolledCourseIds = result.getOrNull() ?: emptyList()
                if (!enrolledCourseIds.isEmpty()) {
                    if (enrolledCourseIds.contains(courseId)) {
                        _uiState.update { it.copy(isStudentTakingCourse = true) }
                    }
                    else {
                        _uiState.update { it.copy(isStudentTakingCourse = false) }
                    }
                }
                else {
                    _uiEvent.send(CourseDetailUiEvent.Error("Student not enrolled in any courses"))
                }
            }
            else {
                _uiEvent.send(CourseDetailUiEvent.Error("Failed to fetch enrolled courses"))
            }
        }
        else {
            _uiEvent.send(CourseDetailUiEvent.Error("User not logged in"))
        }
    }

    private suspend fun isStudentAlreadyCommented() {
        val uid = studentRepository.getCurrentUserId()
        if (uid != null) {
            val commentByStudent = commentReviewRepository.getCommentsByStudentId(uid) // her öğrenci her ders için sadece 1 yorum yapabilir
            val isThisCourse = commentByStudent.getOrNull()?.any { it.courseId == courseId }
            if (isThisCourse == true) {
                _uiState.update {
                    it.copy(isStudentAlreadyCommented = true)
                }
            }
            else {
                _uiState.update {
                    it.copy(isStudentAlreadyCommented = false)
                }
            }
        }
        else {
            _uiEvent.send(CourseDetailUiEvent.Error("User not logged in"))
        }
    }

    private suspend fun fetchCommentsWithStudents() = coroutineScope {
        val currentUserId = studentRepository.getCurrentUserId()
        val commentsResult = commentReviewRepository.getCommentsByCourseId(courseId)
        if (commentsResult.isSuccess) {
            val allComments = commentsResult.getOrNull() ?: emptyList()
            calculateCourseStats(allComments)
            val combinedList = allComments.map { comment ->
                async {
                    val studentResult = studentRepository.getStudentById(comment.studentId)
                    val student = studentResult.getOrNull()
                    val avatarId = if (student != null) {
                        avatarProvider.getAvatarResId(student.studentProfileAvatar)
                    } else {
                        com.example.opinia.R.drawable.ic_launcher_background
                    }
                    CommentAndStudent(
                        comment = comment,
                        studentName = (student?.studentName ?: "Anonymous"),
                        studentSurname = student?.studentSurname ?: "",
                        studentYear = student?.studentYear ?: "",
                        studentAvatarResId = avatarId
                    )
                }
            }.awaitAll()
            val myReviewItem = combinedList.find { it.comment.studentId == currentUserId }
            val othersList = combinedList.filter { it.comment.studentId != currentUserId }
            _uiState.update {
                it.copy(
                    myReview = myReviewItem,
                    otherCommentsList = othersList,
                    isStudentAlreadyCommented = myReviewItem != null
                )
            }
        }
        else {
            _uiEvent.send(CourseDetailUiEvent.Error("Failed to fetch comments"))
        }
    }

    suspend fun refreshComments() {
        if (!networkManager.isInternetAvailable()) {
            _uiEvent.send(CourseDetailUiEvent.Error("No internet connection"))
            return
        }
        _uiState.update {
            it.copy(otherCommentsList = emptyList(), myReview = null)
        }
        delay(500)
        fetchCommentsWithStudents()
    }

    fun deleteMyReview() {
        val myCurrentReview = uiState.value.myReview?.comment
        if (myCurrentReview == null) {
            return
        }
        viewModelScope.launch {
            val result = commentReviewRepository.deleteComment(myCurrentReview.commentId)
            if (result.isSuccess) {
                _uiEvent.send(CourseDetailUiEvent.Success("Comment deleted successfully"))
                _uiState.update {
                    it.copy(
                        myReview = null,
                        isStudentAlreadyCommented = false
                    )
                }
                fetchCommentsWithStudents()
            }
            else {
                _uiEvent.send(CourseDetailUiEvent.Error("Failed to delete comment"))
            }
        }
    }

    private fun calculateCourseStats(comments: List<CommentReview>) {
        if(comments.isEmpty()) {
            _uiState.update {
                it.copy(
                    averageRating = 0.0f,
                    totalReviewCount = 0,
                    ratingDistribution = emptyMap()
                )
            }
            return
        }
        val totalCounts = comments.size
        val avg = comments.map { it.rating }.average().toFloat()
        val distribution = mutableMapOf<Int, Int>()
        for (i in 1..3) {
            val count = comments.count { it.rating == i }
            distribution[i] = count
        }
        _uiState.update {
            it.copy(
                averageRating = avg,
                totalReviewCount = totalCounts,
                ratingDistribution = distribution
            )
        }
    }

    fun onToggleSaveCourse() {
        viewModelScope.launch {
            val isCurrentlySaved = uiState.value.isCourseSaved
            val result = if (isCurrentlySaved) {
                studentRepository.unsaveCourse(courseId)
            } else {
                studentRepository.saveCourse(courseId)
            }
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isCourseSaved = !isCurrentlySaved)
                }
                val message = if (isCurrentlySaved) {
                    "${uiState.value.courseCode} unsaved successfully"
                } else {
                    "${uiState.value.courseCode} saved successfully"
                }
                _uiEvent.send(CourseDetailUiEvent.Success(message))
            } else {
                _uiEvent.send(CourseDetailUiEvent.Error("Could not save course"))
            }
        }
    }

}