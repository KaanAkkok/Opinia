package com.example.opinia.ui.profile

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomButton
import com.example.opinia.ui.components.DropdownWithAnimation
import com.example.opinia.ui.components.TextFieldInput
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpinialightBlue
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeStudentInfoContent(
    controller: NavController,
    faculities: List<Faculty>,
    studentName: String,
    onNameChange: (String) -> Unit,
    studentSurname: String,
    onSurnameChange: (String) -> Unit,
    selectedFaculty: Faculty?,
    onFacultySelected: (Faculty) -> Unit,
    departments: List<Department>,
    selectedDepartment: Department?,
    onDepartmentSelected: (Department) -> Unit,
    selectedStdYear: String,
    onYearSelected: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.yeni_lacivert_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .width(210.dp)
                            .height(63.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OpiniaGreyWhite,
                    scrolledContainerColor = OpiniaGreyWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
            )
        },
        bottomBar = {
            BottomNavBar(navController = controller)
        }
    ) { innerPadding ->

        val scrollState = rememberScrollState()

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
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .fillMaxSize()
                .background(OpiniaGreyWhite)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Student Name",
                color = OpiniaDeepBlue,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextFieldInput(value = studentName, onValueChange = onNameChange)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Student Surname",
                color = OpiniaDeepBlue,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextFieldInput(value = studentSurname, onValueChange = onSurnameChange)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Faculty",
                color = OpiniaDeepBlue,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            DropdownWithAnimation(faculities, selectedFaculty, { it.facultyName }, onFacultySelected)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Department",
                color = OpiniaDeepBlue,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            DropdownWithAnimation(departments, selectedDepartment, { it.departmentName }, onDepartmentSelected)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Year",
                color = OpiniaDeepBlue,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            DropdownWithAnimation(yearList, selectedStdYear, { it }, onYearSelected)

            Spacer(modifier = Modifier.height(72.dp))

            CustomButton(
                onClick = onSaveClick,
                text = "Save",
                shape = MaterialTheme.shapes.medium,
                textStyle = MaterialTheme.typography.titleMedium,
                containerColor = OpinialightBlue,
                contentColor = OpiniaDeepBlue,
                modifier = Modifier
                    .height(42.dp)
                    .width(180.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

        }

    }
}

@Composable
fun ChangeStudentInfoScreen(
    navController: NavController,
    changeStudentInfoViewModel: ChangeStudentInfoViewModel
) {

    val uiState by changeStudentInfoViewModel.uiState.collectAsState()
    val context  = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(key1 = true) {
        changeStudentInfoViewModel.uiEvent.collectLatest { event ->
            when(event) {
                is ChangeStudentInfoUiEvent.SaveSuccess -> {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                is ChangeStudentInfoUiEvent.SaveError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ChangeStudentInfoContent(
        controller = navController,
        studentName = uiState.studentName,
        onNameChange = { changeStudentInfoViewModel.onNameChange(it) },
        studentSurname = uiState.studentSurname,
        onSurnameChange = { changeStudentInfoViewModel.onSurnameChange(it) },
        faculities = uiState.faculties,
        selectedFaculty = uiState.selectedFaculty,
        onFacultySelected = { changeStudentInfoViewModel.onFacultySelected(it) },
        departments = uiState.departments,
        selectedDepartment = uiState.selectedDepartment,
        onDepartmentSelected = { changeStudentInfoViewModel.onDepartmentSelected(it) },
        selectedStdYear = uiState.selectedStdYear,
        onYearSelected = { changeStudentInfoViewModel.onYearSelected(it) },
        onSaveClick = { changeStudentInfoViewModel.onSaveClick() }
    )

}

@Preview(showBackground = true)
@Composable
fun ChangeStudentInfoScreenPreview() {
    ChangeStudentInfoContent(
        controller = NavController(LocalContext.current),
        studentName = "Kaan",
        onNameChange = {},
        studentSurname = "Akkök",
        onSurnameChange = {},
        faculities = emptyList(),
        selectedFaculty = null,
        onFacultySelected = {},
        departments = emptyList(),
        selectedDepartment = null,
        onDepartmentSelected = {},
        selectedStdYear = "",
        onYearSelected = {},
        onSaveClick = {}
    )
}