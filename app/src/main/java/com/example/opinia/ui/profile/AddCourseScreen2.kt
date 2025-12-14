package com.example.opinia.ui.profile

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.data.model.Course
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomCourseCard
import com.example.opinia.ui.components.CustomTopAppBar
import com.example.opinia.ui.components.SearchBar
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.black
import com.example.opinia.ui.theme.gray

@Composable
fun AddCourse2Content(
    avatarResId: Int,
    onAvatarClick: () -> Unit,
    controller: NavController,
    query: String,
    onQueryChange: (String) -> Unit,
    departmentName: String,
    courses: List<Course>,
    enrolledCourseIds: List<String>,
    onCourseToggle: (Course, Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            CustomTopAppBar(
                avatarResId = avatarResId,
                onAvatarClick = onAvatarClick,
                text = "Add Course"
            )
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
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            SearchBar(query, onQueryChange)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = departmentName,
                style = MaterialTheme.typography.titleMedium,
                color = black,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
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
                            .height(45.dp),
                        backgroundColor = OpiniaPurple,
                        innerPadding = PaddingValues(0.dp),
                        activeIcon = Icons.Filled.CheckBox,
                        inactiveIcon = Icons.Filled.CheckBoxOutlineBlank,
                        iconSize = 24.dp,
                        iconStartPadding = 12.dp,
                        codeStyle = SpanStyle(fontWeight = FontWeight.Bold),
                        nameStyle = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp)
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
fun AddCourse2Screen(navController: NavController, addCourseViewModel: AddCourseViewModel) {

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
        query = uiState.searchQuery,
        onQueryChange = addCourseViewModel::onSearchQueryChanged,
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
        }
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
        query = "",
        onQueryChange = {},
        departmentName = "Visual Comminication and Design",
        courses = courses,
        enrolledCourseIds = emptyList(),
        onBackClick = {},
        onCourseToggle = { _, _ -> }
    )
}