package com.example.opinia.ui.profile

import android.app.Activity
import android.util.Log
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCoursesContent(
    avatarResId: Int,
    onAvatarClick: () -> Unit,
    controller: NavController,
    departmentName: String,
    temporarilyUnsavedIds: Set<String>,
    savedCourses: List<Course>,
    onCourseClick: (String) -> Unit,
    onUnsaveClick: (String) -> Unit,
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
                    text = "Saved Courses",
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

            if (savedCourses.isEmpty()) {
                Text("No saved courses found", color = black.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 8.dp))
            }
            else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) { items(
                    items = savedCourses,
                    key = { it.courseId }
                ) { course ->
                    val isSaved = !temporarilyUnsavedIds.contains(course.courseId)
                    CustomCourseCard(
                        course = course,
                        isActive = isSaved,
                        onRowClick = { onCourseClick(course.courseId) },
                        onIconClick = { onUnsaveClick(course.courseId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 10.dp)
                            .height(52.dp),
                        backgroundColor = Color(0xFFB4B4ED),
                        innerPadding = PaddingValues(0.dp),
                        activeIcon = Icons.Filled.Bookmark,
                        inactiveIcon = Icons.Filled.BookmarkBorder,
                        iconSize = 24.dp,
                        iconStartPadding = 12.dp,
                        codeStyle = SpanStyle(fontFamily = WorkSansFontFamily, fontWeight = FontWeight.Normal, fontSize = 15.sp),
                        nameStyle = SpanStyle(fontFamily = WorkSansFontFamily, fontWeight = FontWeight.Normal, fontSize = 15.sp)
                    )
                }
                }
            }
        }
    }

}

@Composable
fun SavedCoursesScreen(
    navController: NavController,
    savedCoursesViewModel: SavedCoursesViewModel,
    searchViewModel: SearchViewModel
) {
    val uiState by savedCoursesViewModel.uiState.collectAsState()
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
        savedCoursesViewModel.uiEvent.collect { event ->
            when (event) {
                is SavedCoursesUiEvent.ErrorLoadingCourses -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is SavedCoursesUiEvent.CoursesLoaded -> {
                    Log.d("SavedCoursesScreen", "Courses loaded: ${event.courses}")
                }
                is SavedCoursesUiEvent.CourseSavedOrUnsavedSuccessfully -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    SavedCoursesContent(
        avatarResId = uiState.avatarResId ?: R.drawable.turuncu,
        onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) },
        controller = navController,
        departmentName = uiState.departmentName,
        temporarilyUnsavedIds = uiState.temporarilyUnsavedIds,
        savedCourses = uiState.savedCourses,
        onCourseClick = { courseId ->
            navController.navigate(Destination.COURSE_DETAIL.route.replace("{courseId}", courseId))
        },
        onUnsaveClick = { courseId ->
            savedCoursesViewModel.onToggleSaveCourse(courseId)
        },
        searchViewModel = searchViewModel
    )
}

@Preview(showBackground = true)
@Composable
fun SavedCoursesScreenPreview() {

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

    SavedCoursesContent(
        avatarResId = R.drawable.turuncu,
        onAvatarClick = {},
        controller = NavController(LocalContext.current),
        departmentName = "Visual Comminication and Design",
        temporarilyUnsavedIds = emptySet(),
        savedCourses = courses,
        onCourseClick = {},
        onUnsaveClick = {}
    )
}