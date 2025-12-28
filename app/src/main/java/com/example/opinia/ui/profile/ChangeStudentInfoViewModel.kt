package com.example.opinia.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
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

data class ChangeStudentInfoUiState(
    val studentName: String = "",
    val studentSurname: String = "",
    val departments: List<Department> = emptyList(),
    val faculties: List<Faculty> = emptyList(),
    val selectedDepartment: Department ?= null,
    val selectedFaculty: Faculty ?= null,
    val selectedStdYear: String = "",
    val isLoading: Boolean = false,
)

sealed class ChangeStudentInfoUiEvent {
    data object SaveSuccess : ChangeStudentInfoUiEvent()
    data class SaveError(val message: String) : ChangeStudentInfoUiEvent()
}

@HiltViewModel
class ChangeStudentInfoViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val facultyDepartmentRepository: FacultyDepartmentRepository,
    private val networkManager: NetworkManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangeStudentInfoUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ChangeStudentInfoUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadInitialData()
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(studentName = name) }
    }

    fun onSurnameChange(surname: String) {
        _uiState.update { it.copy(studentSurname = surname) }
    }

    fun onYearSelected(year: String) {
        _uiState.update { it.copy(selectedStdYear = year) }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val facultiesResult = facultyDepartmentRepository.getAllFaculties()
            val allFaculties = facultiesResult.getOrDefault(emptyList())
            val studentResult = studentRepository.getStudentProfile()
            if (studentResult.isSuccess) {
                val student = studentResult.getOrThrow()

                val currentFaculty = allFaculties.find { it.facultyId == student?.facultyID }
                var currentDepartments: List<Department> = emptyList()
                if (currentFaculty != null) {
                    val deptResult = facultyDepartmentRepository.getDepartmentsByFaculty(currentFaculty.facultyId)
                    currentDepartments = deptResult.getOrDefault(emptyList())
                }
                val currentDepartment = currentDepartments.find { it.departmentId == student?.departmentID }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        studentName = student?.studentName ?: "",
                        studentSurname = student?.studentSurname ?: "",
                        faculties = allFaculties,
                        departments = currentDepartments,
                        selectedFaculty = currentFaculty,
                        selectedDepartment = currentDepartment,
                        selectedStdYear = student?.studentYear ?: ""
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        faculties = allFaculties
                    )
                }
            }
        }
    }

    fun onFacultySelected(faculty: Faculty) {
        _uiState.update {
            it.copy(
                selectedFaculty = faculty,
                selectedDepartment = null,
                departments = emptyList()
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
                    departments = result.getOrDefault(emptyList())
                )
            }
        }
    }

    fun onDepartmentSelected(department: Department) {
        _uiState.update { it.copy(selectedDepartment = department) }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val state = uiState.value
            _uiState.update {
                it.copy(isLoading = true)
            }
            if (!networkManager.isInternetAvailable()) {
                _uiEvent.send(ChangeStudentInfoUiEvent.SaveError("No internet connection"))
                return@launch
            }
            if (state.selectedFaculty == null) {
                _uiEvent.send(ChangeStudentInfoUiEvent.SaveError("Please select a faculty"))
                return@launch
            }
            if (state.selectedDepartment == null) {
                _uiEvent.send(ChangeStudentInfoUiEvent.SaveError("Please select a department"))
                return@launch
            }
            if (state.selectedStdYear.isEmpty()) {
                _uiEvent.send(ChangeStudentInfoUiEvent.SaveError("Please select a year"))
                return@launch
            }
            val res1 = studentRepository.updateStudentName(state.studentName)
            val res2 = studentRepository.updateStudentSurname(state.studentSurname)
            val res3 = studentRepository.updateStudentFaculty(state.selectedFaculty.facultyId)
            val res4 = studentRepository.updateStudentDepartment(state.selectedDepartment.departmentId)
            val res5 = studentRepository.updateStudentYear(state.selectedStdYear)
            if (res1.isSuccess && res2.isSuccess && res3.isSuccess && res4.isSuccess && res5.isSuccess) {
                _uiEvent.send(ChangeStudentInfoUiEvent.SaveSuccess)
            }
            else {
                _uiEvent.send(ChangeStudentInfoUiEvent.SaveError("Failed to save changes"))
            }
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

}