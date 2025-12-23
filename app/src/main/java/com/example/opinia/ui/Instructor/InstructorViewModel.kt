package com.example.opinia.ui.instructor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.R
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
import com.example.opinia.data.model.Instructor
import com.example.opinia.data.repository.AvatarProvider
import com.example.opinia.data.repository.FacultyDepartmentRepository
import com.example.opinia.data.repository.InstructorRepository
import com.example.opinia.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InstructorUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Katalog Ekranı Verileri
    val facultyList: List<Faculty> = emptyList(),
    val departmentList: List<Department> = emptyList(),
    val selectedFaculty: Faculty? = null, // Seçili fakülte

    val isFacultyDropdownExpanded: Boolean = false, // Fakülte listesi açık mı?

    // Global Arama
    val globalSearchResults: List<Instructor> = emptyList(),
    val isGlobalSearching: Boolean = false,

    // Liste Ekranı Verileri
    val instructorList: List<Instructor> = emptyList(),
    val filteredInstructorList: List<Instructor> = emptyList(),
    val currentDepartmentName: String = "",
    val userAvatarResId: Int = R.drawable.ic_launcher_foreground
)

@HiltViewModel
class InstructorViewModel @Inject constructor(
    private val facultyRepository: FacultyDepartmentRepository,
    private val instructorRepository: InstructorRepository,
    private val studentRepository: StudentRepository,
    private val avatarProvider: AvatarProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(InstructorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Avatar yükleme
            val studentResult = studentRepository.getStudentProfile()
            val avatarId = if (studentResult.isSuccess) {
                val avatarKey = studentResult.getOrNull()?.studentProfileAvatar ?: "turuncu"
                avatarProvider.getAvatarResId(avatarKey)
            } else {
                R.drawable.ic_launcher_foreground
            }

            // Fakülteleri çek ama SEÇME (selectedFaculty = null kalsın)
            val facultyResult = facultyRepository.getAllFaculties()
            if (facultyResult.isSuccess) {
                val faculties = facultyResult.getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        facultyList = faculties,
                        selectedFaculty = null, // OTOMATİK SEÇİM YOK
                        userAvatarResId = avatarId
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Fakülteler yüklenemedi") }
            }
        }
    }

    // Fakülte Seçim Dropdown'ını aç/kapa
    fun toggleFacultyDropdown() {
        _uiState.update { it.copy(isFacultyDropdownExpanded = !it.isFacultyDropdownExpanded) }
    }

    // Fakülte seçildiğinde çalışacak fonksiyon
    fun onFacultySelected(faculty: Faculty) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    selectedFaculty = faculty,
                    isFacultyDropdownExpanded = false // Seçince listeyi kapat
                )
            }

            // Seçilen fakültenin departmanlarını getir
            val result = facultyRepository.getDepartmentsByFaculty(faculty.facultyId)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        departmentList = result.getOrNull() ?: emptyList()
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // --- İŞTE EKSİK OLAN FONKSİYON BU ---
    // Seçimi sıfırlayan (başa döndüren) fonksiyon
    fun clearFacultySelection() {
        _uiState.update {
            it.copy(
                selectedFaculty = null,       // Seçili fakülteyi kaldır
                departmentList = emptyList(), // Departmanları temizle
                isFacultyDropdownExpanded = false // Listeyi kapat
            )
        }
    }
    // -------------------------------------

    fun onCatalogSearch(query: String) {
        if (query.isEmpty()) {
            _uiState.update { it.copy(isGlobalSearching = false, globalSearchResults = emptyList()) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isGlobalSearching = true, isLoading = true) }
            val result = instructorRepository.searchInstructors(query)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, globalSearchResults = result.getOrNull() ?: emptyList())
                }
            } else {
                _uiState.update { it.copy(isLoading = false, globalSearchResults = emptyList()) }
            }
        }
    }

    fun loadInstructorsForDepartment(departmentId: String, targetInstructorId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val deptResult = facultyRepository.getDepartmentById(departmentId)
            val departmentName = deptResult.getOrNull()?.departmentName ?: ""
            val facultyId = deptResult.getOrNull()?.facultyId ?: ""
            val instResult = instructorRepository.getInstructorsByFacultyId(facultyId)

            if (instResult.isSuccess) {
                val allInstructors = instResult.getOrNull() ?: emptyList()
                var filtered = allInstructors.filter { it.departmentIds.contains(departmentId) }
                if (targetInstructorId != null) {
                    filtered = filtered.filter { it.instructorId == targetInstructorId }
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        instructorList = filtered,
                        filteredInstructorList = filtered,
                        currentDepartmentName = departmentName
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Hocalar yüklenemedi") }
            }
        }
    }

    fun searchInstructorsLocally(query: String) {
        val currentList = _uiState.value.instructorList
        if (query.isEmpty()) {
            _uiState.update { it.copy(filteredInstructorList = currentList) }
        } else {
            val filtered = currentList.filter {
                it.instructorName.contains(query, ignoreCase = true)
            }
            _uiState.update { it.copy(filteredInstructorList = filtered) }
        }
    }
}