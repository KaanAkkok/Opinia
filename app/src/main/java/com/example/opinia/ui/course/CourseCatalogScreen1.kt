package com.example.opinia.ui.course

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomDropdown
import com.example.opinia.ui.components.CustomTopAppBar
import com.example.opinia.ui.search.GeneralSearchBar
import com.example.opinia.ui.search.SearchViewModel
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.black
import com.example.opinia.ui.theme.gray

@Composable
fun CourseCatalogContent1(
    avatarResId: Int,
    onAvatarClick: () -> Unit,
    query: String,
    controller: NavController,
    availableFaculties: List<Faculty>,
    selectedFaculty: Faculty?,
    onFacultySelected: (Faculty) -> Unit,
    availableDepartments: List<Department>,
    onDepartmentSelected: (Department) -> Unit,
    searchViewModel: SearchViewModel? = null
) {
    val isPreview = LocalInspectionMode.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CustomTopAppBar(
                    avatarResId = avatarResId,
                    onAvatarClick = onAvatarClick,
                    text = "Course Catalog",
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!isPreview && searchViewModel != null) {
                    Box(modifier = Modifier.fillMaxWidth().zIndex(10f)) {
                        GeneralSearchBar(
                            searchViewModel = searchViewModel,
                            onNavigateToCourse = { courseId ->
                                controller.navigate(Destination.COURSE_DETAIL.route.replace("{courseId}", courseId))
                            },
                            onNavigateToInstructor = { instructor ->
                                val deptId = instructor.departmentIds.firstOrNull() ?: "unknown"
                                val route = Destination.INSTRUCTOR_LIST.route
                                    .replace("{departmentName}", deptId)
                                    .replace("{targetInstructorId}", instructor.instructorId)
                                controller.navigate(route)
                            }
                        )
                    }
                } else if (isPreview) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(48.dp)
                            .background(OpinialightBlue, MaterialTheme.shapes.extraLarge),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("  Search Preview...", color = black.copy(0.5f), modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        bottomBar = {
            BottomNavBar(navController = controller)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(OpiniaGreyWhite)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Faculties",
                color = black,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (query.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableFaculties) { faculty ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape = MaterialTheme.shapes.extraLarge)
                                .background(OpiniaPurple)
                                .clickable {
                                    onFacultySelected(faculty)
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = faculty.facultyName,
                                color = black,
                                fontFamily = WorkSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        }
                    }
                    if (availableFaculties.isEmpty()) {
                        item {
                            Text(
                                text = "No faculty found.",
                                modifier = Modifier.padding(8.dp),
                                color = gray
                            )
                        }
                    }
                }
            } else {
                CustomDropdown(
                    availableFaculties,
                    selectedFaculty,
                    onFacultySelected,
                    { it.facultyName },
                    "Select Faculty",
                    Color(0xFF9E9EE8)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedFaculty != null && query.isEmpty()) {
                Text(
                    text = "Departments",
                    color = black,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(availableDepartments) { department ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape = MaterialTheme.shapes.extraLarge)
                                .background(Color(0xFFB4B4ED))
                                .clickable { onDepartmentSelected(department) }
                                .height(52.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = department.departmentName,
                                color = black,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                                fontFamily = WorkSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        }
                    }
                    if (availableDepartments.isEmpty()) {
                        item {
                            Text(
                                text = "No departments found.",
                                modifier = Modifier.padding(16.dp),
                                color = gray
                            )
                        }
                    }
                }
            }
        }

    }

}

@Composable
fun CourseCatalogScreen1(navController: NavController, courseCatalogViewModel: CourseCatalogViewModel, searchViewModel: SearchViewModel) {
    
    val uiState = courseCatalogViewModel.uiState.collectAsState().value
    val context = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(key1 = true) {
        courseCatalogViewModel.uiEvent.collect { event ->
            when(event) {
                is CourseCatalogUiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    BackHandler(enabled = uiState.step == 2) {
        courseCatalogViewModel.onBackToSelection()
    }

    if (uiState.step == 1) {
        CourseCatalogContent1(
            avatarResId = uiState.avatarResId ?: R.drawable.turuncu,
            onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) },
            query = uiState.searchQuery,
            controller = navController,
            availableFaculties = uiState.availableFaculties,
            selectedFaculty = uiState.selectedFaculty,
            onFacultySelected = courseCatalogViewModel::onFacultySelected,
            availableDepartments = uiState.availableDepartments,
            onDepartmentSelected = courseCatalogViewModel::onDepartmentSelected,
            searchViewModel = searchViewModel
        )
    } else {
        CourseCatalogContent2(
            avatarResId = uiState.avatarResId ?: R.drawable.turuncu,
            onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) },
            controller = navController,
            departmentName = uiState.selectedDepartment?.departmentName ?: "",
            courses = uiState.courses,
            onBackClick = { courseCatalogViewModel.onBackToSelection() },
            onCourseClicked = { course ->
                navController.navigate("course_detail/${course.courseId}")
            },
            searchViewModel = searchViewModel
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddCourse1ScreenPreview() {
    CourseCatalogContent1(
        avatarResId = R.drawable.turuncu,
        onAvatarClick = {},
        query = "",
        controller = NavController(LocalContext.current),
        availableFaculties = emptyList(),
        selectedFaculty = null,
        onFacultySelected = {},
        availableDepartments = emptyList(),
        onDepartmentSelected = {}
    )
}