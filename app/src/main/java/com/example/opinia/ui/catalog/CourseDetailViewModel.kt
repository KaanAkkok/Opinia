
package com.example.opinia.ui.catalog


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.R
import com.example.opinia.data.repository.AvatarProvider
import com.example.opinia.data.repository.CourseRepository
import com.example.opinia.data.repository.FacultyDepartmentRepository
import com.example.opinia.data.repository.SeederRepository
import com.example.opinia.data.repository.StudentRepository
import com.example.opinia.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseDetailUiState(
    val isLoading: Boolean = false,
    val courseCode: String = "",
    val courseName: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val isBookmarked: Boolean = false,
    val avatarResId: Int? = null,
    val comments: List<CommentUiModel> = emptyList()
)

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
    private val facultyDepartmentRepository: FacultyDepartmentRepository,
    private val avatarProvider: AvatarProvider,
    private val networkManager: NetworkManager,
    private val seederRepository: SeederRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseDetailUiState())
    val uiState = _uiState.asStateFlow()

    // Navigasyondan gelen courseId'yi al
    private val courseId: String = checkNotNull(savedStateHandle["courseId"])

    init {
        loadCourseDetails(courseId)
    }

    private fun loadCourseDetails(id: String) {
        // Simüle edilmiş veri yükleme. Gerçek uygulamada Repository'den çekilecek.
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Örnek Veriler (Görseldeki verilerle eşleşmesi için)
            val mockComments = listOf(
                CommentUiModel("1", "Aylin K***", R.drawable.turuncu, "08/11/23", 3, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh...", "2024 Fall"),
                CommentUiModel("2", "Sıla K***", R.drawable.turuncu, "08/11/23", 3, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh...", "2024 Fall", isHighlighted = true), // Mavi çerçeveli olan
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    courseCode = "VCD 111",
                    courseName = "Basic Drawing",
                    rating = 2.8,
                    reviewCount = 4,
                    comments = mockComments,
                    avatarResId = R.drawable.turuncu
                )
            }
        }
    }

    fun toggleBookmark() {
        _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
    }
}