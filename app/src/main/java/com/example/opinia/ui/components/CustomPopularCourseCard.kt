package com.example.opinia.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opinia.R
import com.example.opinia.data.model.CommentReview
import com.example.opinia.data.model.Course
import com.example.opinia.ui.course.CommentAndStudent
import com.example.opinia.ui.dashboard.PopularCourseAndComment
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.black
import kotlinx.coroutines.launch

@Composable
fun CustomPopularCourseCard(
    Courses: List<PopularCourseAndComment>,
    onCourseClick: (String) -> Unit = {}
) {
    val popularCourses = remember(Courses) { Courses.take(5) }
    val pagerState = rememberPagerState(pageCount = { popularCourses.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(OpiniaPurple),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (Courses.isEmpty()) {
            Text(
                text = "No popular courses found.",
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        }
        else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(0.dp),
                    pageSpacing = 16.dp,
                    modifier = Modifier.fillMaxWidth(),
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                        snapAnimationSpec = spring(stiffness = Spring.StiffnessLow)
                    ),
                    verticalAlignment = Alignment.Top
                ) { pageIndex ->
                    val course = popularCourses[pageIndex]

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Ders Başlığı
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(onClick = {
                                        onCourseClick(course.Course.courseId)
                                    })
                            ) {
                                Text(
                                    text = course.Course.courseCode,
                                    color = black,
                                    fontFamily = NunitoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = course.Course.courseName,
                                    color = black,
                                    fontFamily = NunitoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            // 2. Rating Bar
                            RatingBar(
                                rating = course.CourseAverageRating.toInt()
                            )
                        }

                        // 3. Yorum Kartı
                        val item = CommentAndStudent(
                            comment = course.LatestComment,
                            studentName = course.CommenterName,
                            studentSurname = course.CommenterSurname,
                            studentYear = course.CommenterYear,
                            studentAvatarResId = course.CommenterAvatarResId
                        )

                        CustomCommentCard(
                            item = item,
                            isMyReview = false,
                            containerColor = OpiniaPurple,
                            imageSize = 64.dp
                        )
                    }
                }

                if (pagerState.currentPage > 0) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = pagerState.currentPage - 1,
                                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                                )
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous",
                            tint = Color(0xFF2F4874)
                        )
                    }
                }

                if (pagerState.currentPage < popularCourses.size - 1) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = pagerState.currentPage + 1,
                                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                                )
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next",
                            tint = Color(0xFF2F4874)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(popularCourses.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration

                    val size by animateDpAsState(
                        targetValue = if (isSelected) 8.dp else 5.dp,
                        label = "indicatorSize"
                    )

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2F4874))
                            .size(size)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomPopularCourseCardPreview() {
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

    val item = CommentAndStudent(
        comment = CommentReview(
            studentId = "123",
            courseId = "456",
            rating = 3,
            comment = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."
        ),
        studentName = "Aylin",
        studentSurname = "Keremaaşlı",
        studentYear = "8th Semester",
        studentAvatarResId = R.drawable.turuncu
    )

    CustomPopularCourseCard(
        Courses = listOf(
            PopularCourseAndComment(
                Course = vcd111,
                CourseAverageRating = 3.0,
                LatestComment = item.comment,
                CommenterName = item.studentName,
                CommenterSurname = item.studentSurname,
                CommenterYear = item.studentYear,
                CommenterAvatarResId = item.studentAvatarResId!!
            )
        )
    )
}