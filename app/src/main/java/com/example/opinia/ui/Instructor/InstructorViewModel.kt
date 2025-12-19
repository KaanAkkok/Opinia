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
    val selectedFaculty: Faculty? = null,
    val isDepartmentsVisible: Boolean = false,

    // Global Arama (Katalog Ekranı için)
    val globalSearchResults: List<Instructor> = emptyList(), // EKLENDİ: Arama sonuçları
    val isGlobalSearching: Boolean = false, // EKLENDİ: Arama modunda mıyız?

    // Liste Ekranı Verileri (Departman içi)
    val instructorList: List<Instructor> = emptyList(),
    val filteredInstructorList: List<Instructor> = emptyList(),
    val currentDepartmentName: String = "",
    val userAvatarResId: Int = R.drawable.ic_launcher_foreground
)

@HiltViewModel
class InstructorViewModel @Inject constructor(
    private val facultyRepository: FacultyDepartmentRepository,
    private val instructorRepository: InstructorRepository, // Repo'yu kullanacağız
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
            val studentResult = studentRepository.getStudentProfile()
            val avatarId = if (studentResult.isSuccess) {
                val avatarKey = studentResult.getOrNull()?.studentProfileAvatar ?: "turuncu"
                avatarProvider.getAvatarResId(avatarKey)
            } else {
                R.drawable.ic_launcher_foreground
            }

            val facultyResult = facultyRepository.getAllFaculties()
            if (facultyResult.isSuccess) {
                val faculties = facultyResult.getOrNull() ?: emptyList()
                val targetFaculty = faculties.find { it.facultyId == "fac_communication" } ?: faculties.firstOrNull()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        facultyList = faculties,
                        selectedFaculty = targetFaculty,
                        userAvatarResId = avatarId
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Fakülteler yüklenemedi") }
            }
        }
    }

    // --- EKLENEN KISIM: GLOBAL ARAMA FONKSİYONU ---
    fun onCatalogSearch(query: String) {
        if (query.isEmpty()) {
            // Arama boşsa normal görünüme dön
            _uiState.update { it.copy(isGlobalSearching = false, globalSearchResults = emptyList()) }
            return
        }

        viewModelScope.launch {
            // Arama başladığı an durumu güncelle
            _uiState.update { it.copy(isGlobalSearching = true, isLoading = true) }

            // Repository'den arama yap
            // Not: Firestore 'startAt' kullandığı için isimlerin baş harfiyle arama yapar (örn: "Ned" -> "Neda")
            val result = instructorRepository.searchInstructors(query)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        globalSearchResults = result.getOrNull() ?: emptyList()
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, globalSearchResults = emptyList()) }
            }
        }
    }
    // ------------------------------------------------

    fun toggleDepartments() {
        val currentState = _uiState.value
        val targetFaculty = currentState.selectedFaculty ?: return

        if (currentState.isDepartmentsVisible) {
            _uiState.update { it.copy(isDepartmentsVisible = false) }
        } else {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                val result = facultyRepository.getDepartmentsByFaculty(targetFaculty.facultyId)
                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            departmentList = result.getOrNull() ?: emptyList(),
                            isDepartmentsVisible = true
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
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

                // 1. Önce departmana göre filtrele
                var filtered = allInstructors.filter { it.departmentIds.contains(departmentId) }

                // 2. Eğer özel davetiye (targetInstructorId) varsa sadece o hocayı bırak
                if (targetInstructorId != null) {
                    filtered = filtered.filter { it.instructorId == targetInstructorId }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        instructorList = filtered,        // Tüm liste (departmandaki)
                        filteredInstructorList = filtered, // Ekranda görünen liste
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