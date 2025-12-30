package com.example.opinia.ui.course

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.opinia.R
import com.example.opinia.data.model.CommentReview
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomCommentCard
import com.example.opinia.ui.components.CustomTopAppBar
import com.example.opinia.ui.components.OnLifeCycleEvent
import com.example.opinia.ui.components.RatingSummarySection
import com.example.opinia.ui.search.GeneralSearchBar
import com.example.opinia.ui.search.SearchViewModel
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.black
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailContent(
    controller: NavController,
    uiState: CourseDetailUiState,
    onAvatarClick: () -> Unit,
    toggleSaveCourse: () -> Unit,
    onAddReviewClick: () -> Unit,
    onRefresh: suspend () -> Unit,
    onDeleteClick: () -> Unit = {},
    searchViewModel: SearchViewModel? = null
) {
    val isPreview = LocalInspectionMode.current
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CustomTopAppBar(
                    avatarResId = uiState.avatarResId ?: R.drawable.ic_launcher_background,
                    onAvatarClick = onAvatarClick,
                    text = "Course Comments",
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!isPreview && searchViewModel != null) {
                    Box(modifier = Modifier.fillMaxWidth().zIndex(10f)) {
                        GeneralSearchBar(
                            searchViewModel = searchViewModel,
                            onNavigateToCourse = { courseId ->
                                controller.navigate(
                                    Destination.COURSE_DETAIL.route.replace(
                                        "{courseId}",
                                        courseId
                                    )
                                )
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
                        Text(
                            "  Search Preview...",
                            color = black.copy(0.5f),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            BottomNavBar(navController = controller)
        },
        floatingActionButton = {
            if (uiState.isStudentTakingCourse && !uiState.isStudentAlreadyCommented) {
                FloatingActionButton(
                    onClick = onAddReviewClick,
                    containerColor = OpiniaPurple,
                    contentColor = OpiniaDeepBlue,
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.comment),
                        contentDescription = "comment",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)) {
                                    append(uiState.courseCode)
                                }
                                append(" - ")
                                withStyle(style = SpanStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)) {
                                    append(uiState.courseName)
                                }
                            },
                            color = black,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = toggleSaveCourse) {
                            Icon(
                                imageVector = if (uiState.isCourseSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "Save Course",
                                tint = OpiniaDeepBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. RATING SUMMARY
                item {
                    RatingSummarySection(
                        averageRating = uiState.averageRating,
                        totalCount = uiState.totalReviewCount,
                        distribution = uiState.ratingDistribution
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 3. YOUR REVIEW
                if (uiState.myReview != null) {
                    item {
                        Text(
                            "Your Review",
                            color = black,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomCommentCard(
                            item = uiState.myReview,
                            isMyReview = true,
                            onDeleteClick = onDeleteClick
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // 4. STUDENT REVIEWS
                if (uiState.otherCommentsList.isNotEmpty()) {
                    item {
                        Text(
                            "Student Reviews",
                            color = black,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(uiState.otherCommentsList) { reviewItem ->
                        CustomCommentCard(item = reviewItem)
                    }
                } else if (uiState.myReview == null) {
                    item {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(
                                "No reviews yet. Be the first!",
                                color = Color.Gray
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun CourseDetailScreen(navController: NavController, courseDetailViewModel: CourseDetailViewModel, searchViewModel: SearchViewModel) {

    val uiState by courseDetailViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    OnLifeCycleEvent { event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            courseDetailViewModel.loadAllData()
        }
    }

    LaunchedEffect(key1 = true) {
        courseDetailViewModel.uiEvent.collect { event ->
            when (event) {
                is CourseDetailUiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is CourseDetailUiEvent.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    CourseDetailContent(
        controller = navController,
        uiState = uiState,
        onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) },
        toggleSaveCourse = courseDetailViewModel::onToggleSaveCourse,
        onAddReviewClick = { navController.navigate(Destination.COMMENT_REVIEW.route.replace("{courseId}", uiState.courseId)) },
        onRefresh = courseDetailViewModel::refreshComments,
        onDeleteClick = courseDetailViewModel::deleteMyReview,
        searchViewModel = searchViewModel
    )

}

@Preview(showBackground = true)
@Composable
fun CourseDetailScreenPreview() {
    val mockComments = listOf(
        CommentAndStudent(
            comment = CommentReview(
                rating = 3,
                comment = "Ders çok verimliydi, hocanın anlatımı harika. Kesinlikle tavsiye ederim.",
                timestamp = System.currentTimeMillis()
            ),
            studentName = "Ahmet",
            studentSurname = "Yılmaz",
            studentYear = "3rd",
            studentAvatarResId = R.drawable.turuncu
        ),
        CommentAndStudent(
            comment = CommentReview(
                rating = 2,
                comment = "Sınavlar biraz zordu ama proje ödevleri öğreticiydi.",
                timestamp = System.currentTimeMillis()
            ),
            studentName = "Ayşe",
            studentSurname = "K.",
            studentYear = "2nd",
            studentAvatarResId = R.drawable.turuncu
        )
    )

    val mockState = CourseDetailUiState(
        courseCode = "CS-101",
        courseName = "Introduction to Computer Science",
        averageRating = 2.4f,
        totalReviewCount = 15,
        ratingDistribution = mapOf(1 to 2, 2 to 5, 3 to 8),
        isCourseSaved = true,
        isStudentTakingCourse = true,
        isStudentAlreadyCommented = false,
        otherCommentsList = mockComments,
        myReview = null
    )

    CourseDetailContent(
        controller = rememberNavController(),
        uiState = mockState,
        onAvatarClick = {},
        toggleSaveCourse = {},
        onRefresh = {},
        onDeleteClick = {},
        onAddReviewClick = {}
    )
}