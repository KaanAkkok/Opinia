package com.example.opinia.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Tıklama için gerekli import
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.opinia.R
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.data.model.Course
import com.example.opinia.data.model.Instructor
import com.example.opinia.data.model.CommentReview
import com.example.opinia.ui.Destination
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.white
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),

) {
    val state by viewModel.uiState.collectAsState()

    DashboardContent(
        state = state,
        navController = navController,
        onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
        onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) } // Tıklamayı içeri aktar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    state: DashboardUiState,
    navController: NavController,
    onSearchQueryChange: (String) -> Unit,
    onAvatarClick: () -> Unit // Header'a iletmek için parametre
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = white
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            // Boşluk 50 DP
            Spacer(modifier = Modifier.height(50.dp))

            // Header (Tıklama fonksiyonu eklendi)
            DashboardHeader(
                name = state.studentName,
                surname = state.studentSurname,
                avatarResId = state.avatarResId ?: R.drawable.turuncu,
                onAvatarClick = onAvatarClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Search Bar
            SearchBarCustom(
                query = state.searchText,
                onQueryChange = onSearchQueryChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isSearching) {
                SearchResultsList(
                    courses = state.searchResultsCourses,
                    instructors = state.searchResultsInstructors
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Featured Course Card
                    item {
                        if (state.featuredCourse != null) {
                            FeaturedCourseCard(
                                course = state.featuredCourse!!,
                                comment = state.featuredComment,
                                averageRating = state.featuredRatingAverage
                            )
                        } else {
                            if (!state.isLoading) Text(
                                "No enrolled courses yet.",
                                color = Color.Gray,
                                fontFamily = WorkSansFontFamily
                            )
                        }
                    }

                    // My Courses List
                    item {
                        MyCoursesSection(courses = state.enrolledCourses)
                    }

                    // Listenin en altında biraz pay bırakmak için
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// --- YARDIMCI COMPONENTLER ---

// --- GÜNCELLENEN HEADER ---
@Composable
fun DashboardHeader(
    name: String,
    surname: String,
    avatarResId: Int,
    onAvatarClick: () -> Unit // Tıklama parametresi
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.lacivert_amblem),
            contentDescription = "Logo",
            modifier = Modifier.size(50.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome, ",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = WorkSansFontFamily,
                color = Color.Black
            )
            Text(
                text = "$name $surname",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = WorkSansFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Avatar Tıklanabilir Yapıldı
        Image(
            painter = painterResource(id = avatarResId),
            contentDescription = "Profile Avatar",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable { onAvatarClick() } // Tıklama aksiyonu
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarCustom(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp)),
        placeholder = {
            Text(
                "search",
                fontFamily = WorkSansFontFamily
            )
        },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFD1E1F3),
            unfocusedContainerColor = Color(0xFFD1E1F3),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontFamily = WorkSansFontFamily)
    )
}

@Composable
fun FeaturedCourseCard(course: Course, comment: CommentReview?, averageRating: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = OpiniaPurple)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        text = course.courseCode,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = course.courseName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = WorkSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row {
                    repeat(3) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300)) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (comment != null) {
                Row(verticalAlignment = Alignment.Top) {
                    Image(
                        painter = painterResource(id = R.drawable.turuncu),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Hediye B***",
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = WorkSansFontFamily
                            )
                            val date = Date(comment.timestamp)
                            val format = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                            Text(
                                text = format.format(date),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontFamily = WorkSansFontFamily
                            )
                        }
                        Row {
                            repeat(comment.rating.toInt()) {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFFB300))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = comment.comment,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = WorkSansFontFamily,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(id = R.drawable.mor), contentDescription = "dots", modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun MyCoursesSection(courses: List<Course>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = OpiniaPurple)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "My Courses",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            courses.forEach { course ->
                Text(
                    text = "${course.courseCode} - ${course.courseName}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = WorkSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SearchResultsList(courses: List<Course>, instructors: List<Instructor>) {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (courses.isNotEmpty()) {
            item {
                Text(
                    "Courses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = NunitoFontFamily
                )
            }
            items(courses) { course ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Text(
                        text = "${course.courseCode} - ${course.courseName}",
                        modifier = Modifier.padding(16.dp),
                        fontFamily = WorkSansFontFamily
                    )
                }
            }
        }
        if (instructors.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp));
                Text(
                    "Instructors",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = NunitoFontFamily
                )
            }
            items(instructors) { instructor ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Text(
                        text = "${instructor.instructorTitle} ${instructor.instructorName}",
                        modifier = Modifier.padding(16.dp),
                        fontFamily = WorkSansFontFamily
                    )
                }
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    val dummyCourse = Course(
        courseId = "1",
        courseCode = "VCD 471",
        courseName = "Interactive Design Studio",
        akts = 5,
        credits = 3
    )

    val dummyCoursesList = listOf(
        dummyCourse,
        Course(courseId = "2", courseCode = "VCD 111", courseName = "Basic Drawing", akts = 4, credits = 3),
        Course(courseId = "3", courseCode = "VCD 171", courseName = "Design Fundamentals", akts = 4, credits = 3)
    )

    val dummyComment = CommentReview(
        comment = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit...",
        rating = 4,
        timestamp = System.currentTimeMillis()
    )

    val dummyState = DashboardUiState(
        isLoading = false,
        studentName = "Sıla",
        studentSurname = "Kaplan",
        avatarResId = R.drawable.mor,
        enrolledCourses = dummyCoursesList,
        featuredCourse = dummyCourse,
        featuredComment = dummyComment,
        featuredRatingAverage = 4.5
    )

    DashboardContent(
        state = dummyState,
        navController = rememberNavController(),
        onSearchQueryChange = {},
        onAvatarClick = {} // Preview için boş lambda
    )
}