package com.example.opinia.ui.comment_review

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomCommentField
import com.example.opinia.ui.components.CustomTopAppBar
import com.example.opinia.ui.search.GeneralSearchBar
import com.example.opinia.ui.search.SearchViewModel
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.black

@Composable
fun CommentReviewContent(
    controller: NavController,
    avatarResId: Int,
    onAvatarClick: () -> Unit,
    courseCode: String = "",
    courseName: String = "",
    toggleSaveCourse: () -> Unit,
    isCourseSaved: Boolean = false,
    rating: Int = 0,
    onRatingChanged: (Int) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
    isInputValid: Boolean = false,
    isLoading: Boolean = false,
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
                    text = "Course Comment",
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

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(OpiniaGreyWhite)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Ders Bilgisi ve Kaydet Butonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = NunitoFontFamily)) {
                            append(courseCode)
                        }
                        append(" - ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = NunitoFontFamily)) {
                            append(courseName)
                        }
                    },
                    color = black,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = toggleSaveCourse) {
                    Icon(
                        imageVector = if (isCourseSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Save Course",
                        tint = OpiniaDeepBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Puanlama Alanı
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(46.dp)
                            .width(100.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(OpiniaPurple),
                        contentAlignment = Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.StarBorder,
                                contentDescription = "Star",
                                tint = black,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text("Rate", style = MaterialTheme.typography.titleSmall, color = black)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Yıldızlar
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..3) {
                            val isSelected = i <= rating
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Star else Icons.Default.StarBorder,
                                contentDescription = "$i Star",
                                tint = Color(0xFFF9A75D), //yellow
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable { onRatingChanged(i) }
                                    .padding(4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Yorum Kısmı
                CustomCommentField(
                    comment = comment,
                    onCommentChange = onCommentChange,
                    onSubmitComment = onSubmitComment,
                    isLoading = isLoading,
                    isInputValid = isInputValid,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

        }
    }
}

@Composable
fun CommentReviewScreen(navController: NavController, commentReviewViewModel: CommentReviewViewModel, searchViewModel: SearchViewModel) {

    val uiState by commentReviewViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        commentReviewViewModel.uiEvent.collect { event ->
            when (event) {
                is CommentReviewUiEvent.CommentSuccessfullyCreated -> {
                    Toast.makeText(context, "Comment submitted successfully!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                is CommentReviewUiEvent.ErrorCreatingComment -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    CommentReviewContent(
        controller = navController,
        avatarResId = uiState.avatarResId ?: R.drawable.turuncu,
        onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) },
        courseCode = uiState.courseCode,
        courseName = uiState.courseName,
        toggleSaveCourse = commentReviewViewModel::onToggleSaveCourse,
        isCourseSaved = uiState.isCourseSaved,
        rating = uiState.rating,
        onRatingChanged = commentReviewViewModel::onRatingChanged,
        comment = uiState.comment,
        onCommentChange = commentReviewViewModel::onCommentChanged,
        onSubmitComment = commentReviewViewModel::submitCommentReview,
        isLoading = uiState.isLoading,
        isInputValid = uiState.comment.isNotBlank() && uiState.rating > 0,
        searchViewModel = searchViewModel
    )

}


@Preview(showBackground = true)
@Composable
fun CommentReviewContentPreview() {
    CommentReviewContent(
        controller = NavController(LocalContext.current),
        avatarResId = R.drawable.turuncu,
        onAvatarClick = {},
        courseCode = "CS101",
        courseName = "Introduction to Computer Science",
        toggleSaveCourse = {},
        isCourseSaved = false,
        rating = 2,
        onRatingChanged = {},
        comment = "",
        onCommentChange = {},
        onSubmitComment = {},
        isLoading = false
    )
}