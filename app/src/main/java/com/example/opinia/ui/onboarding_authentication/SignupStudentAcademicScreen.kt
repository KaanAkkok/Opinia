package com.example.opinia.ui.onboarding_authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
import com.example.opinia.ui.Destination
import com.example.opinia.ui.components.DropdownInput
import com.example.opinia.ui.components.NextButton
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignupStudentAcademicContent(
    faculities: List<Faculty>,
    selectedFaculty: Faculty?,
    onFacultySelected: (Faculty) -> Unit,
    departments: List<Department>,
    selectedDepartment: Department?,
    onDepartmentSelected: (Department) -> Unit,
    selectedStdYear: String,
    onYearSelected: (String) -> Unit,
    onNextClick: () -> Unit
) {
    val yearList = listOf(
        "1st Semester",
        "2st Semester",
        "3st Semester",
        "4st Semester",
        "5st Semester",
        "6st Semester",
        "7st Semester",
        "8st Semester",
        "Beyond 8st Semester"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OpiniaDeepBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.yeni_acikmavi_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .width(210.dp)
                .height(63.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))

        Text("Faculty", style = MaterialTheme.typography.titleSmall, color = OpinialightBlue)

        Spacer(modifier = Modifier.height(12.dp))

        DropdownInput(faculities, selectedFaculty, { it.facultyName }, onFacultySelected)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Department", style = MaterialTheme.typography.titleSmall, color = OpinialightBlue)

        Spacer(modifier = Modifier.height(12.dp))

        DropdownInput(departments, selectedDepartment, { it.departmentName }, onDepartmentSelected)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Year", style = MaterialTheme.typography.titleSmall, color = OpinialightBlue)

        Spacer(modifier = Modifier.height(12.dp))

        DropdownInput(yearList, selectedStdYear, { it }, onYearSelected)

        Spacer(modifier = Modifier.height(160.dp))

        NextButton(onClick = onNextClick)
    }

}

@Composable
fun SignupStudentAcademicScreen(navController: NavController, registerViewModel: RegisterViewModel) {

    val uiState by registerViewModel.uiState.collectAsState()
    val context  = LocalContext.current

    LaunchedEffect(key1 = true) {
        registerViewModel.uiEvent.collectLatest { event ->
            when(event) {
                is RegisterUiEvent.SignupError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }

    SignupStudentAcademicContent(
        faculities = uiState.availableFaculties,
        selectedFaculty = uiState.selectedFaculty,
        onFacultySelected = { registerViewModel.onFacultySelected(it) },
        departments = uiState.availableDepartments,
        selectedDepartment = uiState.selectedDepartment,
        onDepartmentSelected = { registerViewModel.onDepartmentSelected(it) },
        selectedStdYear = uiState.selectedStdYear,
        onYearSelected = { registerViewModel.onYearSelected(it) },
        onNextClick = {
            if (registerViewModel.validateStep2()) {
                navController.navigate(Destination.SIGNUP_COURSE_INFO.route)
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun SignupStudentAcademicScreenPreview() {
    val dummyFaculties = listOf(Faculty("1", "Faculty of Communication"))
    val dummyDepartments = listOf(Department("1", "Visual Communication Design", "1"))

    SignupStudentAcademicContent(
        faculities = dummyFaculties,
        selectedFaculty = dummyFaculties[0],
        onFacultySelected = {},
        departments = dummyDepartments,
        selectedDepartment = dummyDepartments[0],
        onDepartmentSelected = {},
        selectedStdYear = "1st Semester",
        onYearSelected = {},
        onNextClick = {}
    )
}