package com.example.opinia.ui.profile

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.data.model.Course
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomCourseCard
import com.example.opinia.ui.components.CustomTopAppBar
import com.example.opinia.ui.search.GeneralSearchBar
import com.example.opinia.ui.search.SearchViewModel
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.black
import com.example.opinia.ui.theme.gray

@Composable
fun AddCourse2Content(
    avatarResId: Int,
    onAvatarClick: () -> Unit,
    controller: NavController,
    departmentName: String,
    courses: List<Course>,
    enrolledCourseIds: List<String>,
    onCourseToggle: (Course, Boolean) -> Unit,
    onBackClick: () -> Unit,
    searchViewModel: SearchViewModel? = null
) {
    val listState = rememberLazyListState()
    val isPreview = LocalInspectionMode.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CustomTopAppBar(
                    avatarResId = avatarResId,
                    onAvatarClick = onAvatarClick,
                    text = "Add Course",
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
                .padding(horizontal = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = departmentName,
                color = black,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(
                    items = courses,
                    key = { course -> course.courseId }
                ) { course ->
                    val isAdded = enrolledCourseIds.contains(course.courseId)
                    CustomCourseCard(
                        course = course,
                        isActive = isAdded,
                        onRowClick = { onCourseToggle(course, isAdded) },
                        onIconClick = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 10.dp)
                            .height(52.dp),
                        backgroundColor = Color(0xFFB4B4ED),
                        innerPadding = PaddingValues(0.dp),
                        activeIcon = Icons.Filled.CheckBox,
                        inactiveIcon = Icons.Filled.CheckBoxOutlineBlank,
                        iconSize = 24.dp,
                        iconStartPadding = 12.dp,
                        codeStyle = SpanStyle(fontFamily = WorkSansFontFamily, fontWeight = FontWeight.Normal, fontSize = 15.sp),
                        nameStyle = SpanStyle(fontFamily = WorkSansFontFamily, fontWeight = FontWeight.Normal, fontSize = 15.sp)
                    )
                }
                if (courses.isEmpty()) {
                    item {
                        Text(
                            text = "No courses found.",
                            modifier = Modifier.padding(16.dp),
                            color = gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddCourse2Screen(
    navController: NavController,
    addCourseViewModel: AddCourseViewModel,
    searchViewModel: SearchViewModel
) {

    val uiState = addCourseViewModel.uiState.collectAsState().value
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
        addCourseViewModel.uiEvent.collect { event ->
            when(event) {
                is AddCourseUiEvent.CourseAddedSuccess -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is AddCourseUiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    AddCourse2Content(
        avatarResId = uiState.avatarResId ?: R.drawable.turuncu,
        onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) },
        controller = navController,
        departmentName = uiState.selectedDepartment?.departmentName ?: "Unknown Dept",
        courses = uiState.availableCourses,
        enrolledCourseIds = uiState.enrolledCourseIds,
        onBackClick = addCourseViewModel::onBackToSelection,
        onCourseToggle = { course, isCurrentlyAdded ->
            if (isCurrentlyAdded) {
                addCourseViewModel.onRemoveCourseClicked(course)
            } else {
                addCourseViewModel.onAddCourseClicked(course)
            }
        },
        searchViewModel = searchViewModel
    )
}

@Preview(showBackground = true)
@Composable
fun AddCourse2ScreenPreview() {

    val vcd111 = Course(
        "vcd111",
        "VCD111",
        "Basic Drawing",
        "fac_communication",
        6,
        3,
        "vcd111",
        0.0,
        0,
        listOf(
            "dept_visual_communication_design"
        ),
        emptyList()
    )

    val vcd171 = Course(
        "vcd171",
        "VCD171",
        "Design Fundamentals",
        "fac_communication",
        5,
        3,
        "vcd171",
        0.0,
        0,
        listOf(
            "dept_visual_communication_design"
        ),
        emptyList()
    )

    val vcd172 = Course(
        "vcd172",
        "VCD172",
        "Digital Design",
        "fac_communication",
        7,
        3,
        "vcd172",
        0.0,
        0,
        listOf(
            "dept_visual_communication_design"
        ),
        emptyList()
    )

    val courses = listOf(vcd111, vcd171, vcd172)

    AddCourse2Content(
        avatarResId = R.drawable.turuncu,
        onAvatarClick = {},
        controller = NavController(LocalContext.current),
        departmentName = "Visual Comminication and Design",
        courses = courses,
        enrolledCourseIds = emptyList(),
        onBackClick = {},
        onCourseToggle = { _, _ -> }
    )
}