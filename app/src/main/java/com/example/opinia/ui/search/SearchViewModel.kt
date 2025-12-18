package com.example.opinia.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.model.Course
import com.example.opinia.data.model.Instructor
import com.example.opinia.data.repository.CourseRepository
import com.example.opinia.data.repository.InstructorRepository
import com.example.opinia.utils.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val searchQuery: String = "",
    val searchResultsCourses: List<Course> = emptyList(),
    val searchResultsInstructors: List<Instructor> = emptyList(),
    val isSearching: Boolean = false,
    val isLoading: Boolean = true,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val instructorRepository: InstructorRepository,
    private val networkManager: NetworkManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, isSearching = query.isNotEmpty()) }
        searchJob?.cancel()
        if (query.length >= 3) {
            searchJob = viewModelScope.launch {
                delay(300L)
                performSearch(query)
            }
        } else {
            _uiState.update {
                it.copy(searchResultsCourses = emptyList(), searchResultsInstructors = emptyList())
            }
        }
    }

    fun performSearch(query: String) {
        if (!networkManager.isInternetAvailable()) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val coursesDeferred = async { courseRepository.searchCourses(query) }
            val instructorsDeferred = async { instructorRepository.searchInstructors(query) }
            _uiState.update {
                it.copy(
                    searchResultsCourses = coursesDeferred.await().getOrNull() ?: emptyList(),
                    searchResultsInstructors = instructorsDeferred.await().getOrNull() ?: emptyList()
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

}