package com.example.opinia.ui.comment_review

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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentReviewUiState(
    val courseId: String = "",
    val avatarResId: Int? = null,
    val courseCode: String = "",
    val courseName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val isCourseSaved: Boolean = false,
    val isLoading: Boolean = false
)

sealed class CommentReviewUiEvent {
    data object CommentSuccessfullyCreated : CommentReviewUiEvent()
    data class ErrorCreatingComment(val message: String) : CommentReviewUiEvent()
}

@HiltViewModel
class CommentReviewViewModel @Inject constructor(
    private val commentReviewRepository: CommentReviewRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
    private val avatarProvider: AvatarProvider,
    private val networkManager: NetworkManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    //navigasyondan gelen argümanlar
    private val courseId: String = checkNotNull(savedStateHandle["courseId"])

    //navdan gelen argümaları set et
    private val _uiState = MutableStateFlow(CommentReviewUiState(courseId = courseId))
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<CommentReviewUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        fetchCourseDetails()
        checkIfCourseSaved()
        fetchStudentAvatarId()
    }

    private fun fetchCourseDetails() {
        viewModelScope.launch {
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
                _uiEvent.send(CommentReviewUiEvent.ErrorCreatingComment("Error fetching course details"))
            }
        }
    }

    private fun checkIfCourseSaved() {
        viewModelScope.launch {
            val result = studentRepository.getSavedCourseIds()
            if (result.isSuccess) {
                val savedCourseIds = result.getOrNull() ?: emptyList()
                val isSaved = savedCourseIds.contains(courseId)
                _uiState.update { it.copy(isCourseSaved = isSaved) }
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

    fun onRatingChanged(rating: Int) {
        _uiState.update { it.copy(rating = rating) }
    }

    fun onCommentChanged(comment: String) {
        if (comment.length <= 500) {
            _uiState.update { it.copy(comment = comment) }
        }
        else {
            val trimmedComment = comment.substring(0, 500)
            _uiState.update { it.copy(comment = trimmedComment) }
        }
    }

    fun submitCommentReview() {
        val state = _uiState.value
        viewModelScope.launch {
            if (!networkManager.isInternetAvailable()) {
                _uiEvent.send(CommentReviewUiEvent.ErrorCreatingComment("No internet connection"))
                return@launch
            }
            if (state.comment.isBlank()) {
                _uiEvent.send(CommentReviewUiEvent.ErrorCreatingComment("Please enter a comment"))
                return@launch
            }
            if (state.comment.length > 500) {
                _uiEvent.send(CommentReviewUiEvent.ErrorCreatingComment("Comment cannot exceed 500 characters"))
                return@launch
            }
            if (state.rating == 0) {
                _uiEvent.send(CommentReviewUiEvent.ErrorCreatingComment("Please rate the course"))
                return@launch
            }
            _uiState.update { it.copy(isLoading = true) }
            val newCommentReview = CommentReview(
                courseId = courseId,
                rating = state.rating,
                comment = state.comment
            )
            val result = commentReviewRepository.createCommentReview(newCommentReview)
            _uiState.update { it.copy(isLoading = false) }
            if (result.isSuccess) {
                _uiEvent.send(CommentReviewUiEvent.CommentSuccessfullyCreated)
                resetSubmissionState()
            }
            else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Comment creation failed"
                _uiEvent.send(CommentReviewUiEvent.ErrorCreatingComment(errorMsg))
            }
        }
    }

    fun onToggleSaveCourse() {
        viewModelScope.launch {
            val isCurrentlySaved = uiState.value.isCourseSaved
            val result = if (isCurrentlySaved) {
                studentRepository.unsaveCourse(courseId)
            }
            else {
                studentRepository.saveCourse(courseId)
            }
            if (result.isSuccess) {
                _uiState.update { it.copy(isCourseSaved = !isCurrentlySaved) }
            }
            else {
                _uiEvent.send(CommentReviewUiEvent.ErrorCreatingComment("Could not save course"))
            }
        }
    }

    fun resetSubmissionState() {
        _uiState.update {
            it.copy(
                rating = 0,
                comment = "",
                isLoading = false,
            )
        }
    }
}