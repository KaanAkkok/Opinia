package com.example.opinia.ui.dashboard

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.opinia.R
import com.example.opinia.data.model.Course
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomPopularCourseCard
import com.example.opinia.ui.components.CustomTopAppBar
import com.example.opinia.ui.search.GeneralSearchBar
import com.example.opinia.ui.search.SearchViewModel
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.black
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    avatarResId: Int,
    onAvatarClick: () -> Unit,
    studentName: String = "",
    studentSurname: String = "",
    controller: NavController,
    popularCourses: List<PopularCourseAndComment> = emptyList(),
    currentStudentCourses: List<Course> = emptyList(),
    onRefresh: suspend () -> Unit = {},
    searchViewModel: SearchViewModel? = null
) {
    val isPreview = LocalInspectionMode.current
    val scrollState = rememberScrollState()
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CustomTopAppBar(
                    avatarResId = avatarResId,
                    onAvatarClick = onAvatarClick,
                    text = "Welcome, $studentName $studentSurname",
                    textSize = 17
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
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    onRefresh()
                    isRefreshing = false
                }
            },
            state = pullToRefreshState,
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = isRefreshing,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    state = pullToRefreshState
                )
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(OpiniaGreyWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OpiniaGreyWhite)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                if(popularCourses.isEmpty()) {
                    Text("No popular courses found", color = black.copy(alpha = 0.6f))
                }
                else {
                    CustomPopularCourseCard(
                        Courses = popularCourses,
                        onCourseClick = { courseId ->
                            controller.navigate(Destination.COURSE_DETAIL.route.replace("{courseId}", courseId))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(OpiniaPurple)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.Start
                ){
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "My Courses",
                        color = black,
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (currentStudentCourses.isEmpty()) {
                        Text(
                            text = "No courses found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = black.copy(alpha = 0.6f)
                        )
                    } else {
                        currentStudentCourses.forEach { course ->
                            TextButton(
                                onClick = {
                                    controller.navigate(Destination.COURSE_DETAIL.route.replace("{courseId}", course.courseId))
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontFamily = WorkSansFontFamily, fontWeight = FontWeight.Normal, fontSize = 15.sp)) {
                                            append(course.courseCode)
                                        }
                                        append(" - ")
                                        withStyle(style = SpanStyle(fontFamily = WorkSansFontFamily, fontWeight = FontWeight.Normal, fontSize = 15.sp)) {
                                            append(course.courseName)
                                        }
                                    },
                                    color = black,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun DashboardScreen(navController: NavController, viewModel: DashboardViewModel, searchViewModel: SearchViewModel) {

    val uiState by viewModel.uiState.collectAsState()
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
        viewModel.uiEvent.collect { event ->
            when (event) {
                is DashboardUiEvent.DashboardUiEventSuccess -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is DashboardUiEvent.DashboardUiEventError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DashboardContent(
        avatarResId = uiState.avatarResId ?: R.drawable.ic_launcher_foreground,
        onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) },
        studentName = uiState.studentName,
        studentSurname = uiState.studentSurname,
        controller = navController,
        popularCourses = uiState.courses,
        currentStudentCourses = uiState.CurrentStudentCourses,
        onRefresh = viewModel::refreshComments,
        searchViewModel = searchViewModel
    )

}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {

    val dummyCourses = listOf(
        Course("1", "CS101", "Intro to CS", "CS", 5, 3, "1", 4.5, 10, emptyList(), emptyList()),
        Course("2", "MATH101", "Calculus I", "MATH", 6, 4, "2", 3.8, 5, emptyList(), emptyList())
    )

    DashboardContent(
        avatarResId = com.example.opinia.R.drawable.mor,
        onAvatarClick = {},
        studentName = "Ali",
        studentSurname = "Veli",
        controller = rememberNavController(),
        popularCourses = emptyList(),
        onRefresh = {},
        currentStudentCourses = dummyCourses
    )

}