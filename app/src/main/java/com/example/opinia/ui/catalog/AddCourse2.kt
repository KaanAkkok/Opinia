package com.example.opinia.ui.catalog





import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.data.model.Course
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomCourseCard
import com.example.opinia.ui.components.CustomTopAppBar
import com.example.opinia.ui.components.SearchBar
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.OpinialightBlue
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
    onCourseToggle: (Course, Boolean) -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            CustomTopAppBar(
                avatarResId = avatarResId,
                onAvatarClick = onAvatarClick,
                text = "Course Catalog" // Görseldeki başlık
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

            // Arama Çubuğu
            SearchBar(query, onQueryChange)

            Spacer(modifier = Modifier.height(32.dp))

            // Bölüm Adı Başlığı
            Text(
                text = departmentName,
                style = MaterialTheme.typography.titleMedium,
                color = black,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Ders Listesi
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), // Görseldeki gibi kenar boşlukları
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = courses,
                    key = { course -> course.courseId }
                ) { course ->
                    val isAdded = enrolledCourseIds.contains(course.courseId)

                    // Seçili olma durumuna göre kenarlık (Border) ayarı
                    val modifier = if (isAdded) {
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(2.dp, OpinialightBlue, MaterialTheme.shapes.medium) // Mavi çerçeve
                            .clip(MaterialTheme.shapes.medium)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(MaterialTheme.shapes.medium)
                    }

                    CustomCourseCard(
                        course = course,
                        isActive = isAdded,
                        onRowClick = { onCourseToggle(course, isAdded) },
                        onIconClick = null, // Tüm satır tıklanabilir
                        modifier = modifier,
                        backgroundColor = OpiniaPurple,
                        innerPadding = PaddingValues(0.dp),
                        // Görselde checkbox yok ama kod yapısında varsa gizleyebilir veya kullanabiliriz.
                        // Burada görseldeki gibi temiz görünüm için ikonları null veya görünmez yapabiliriz
                        // Ancak kod yapısını bozmamak için varsayılan ikonları bırakıyorum.
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

@Preview(showBackground = true)
@Composable
fun AddCourse2ScreenPreview() {

    // Önizleme için sahte veriler
    val vcd111 = Course(
        "vcd111", "VCD 111", "Basic Drawing", "fac_communication", 6, 3, "vcd111", 0.0, 0,
        listOf("dept_visual_communication_design"), emptyList()
    )

    val vcd171 = Course(
        "vcd171", "VCD 171", "Design Fundamentals", "fac_communication", 5, 3, "vcd171", 0.0, 0,
        listOf("dept_visual_communication_design"), emptyList()
    )

    val vcd271 = Course(
        "vcd271", "VCD 271", "Modeling inVurtual Enviroments", "fac_communication", 6, 3, "vcd271", 0.0, 0,
        listOf("dept_visual_communication_design"), emptyList()
    )
    val vcd272 = Course(
        "vcd272", "VCD 272", "Motion Design in 3D", "fac_communication", 7, 3, "vcd272", 0.0, 0,
        listOf("dept_visual_communication_design"), emptyList()
    )
    val vcd273 = Course(
        "vcd273", "VCD 273", "Digital Design and Illustration", "fac_communication", 7, 3, "vcd273", 0.0, 0,
        listOf("dept_visual_communication_design"), emptyList()
    )
    val vcd274 = Course(
        "vcd274", "VCD 274", "Motion Graphics", "fac_communication", 6, 3, "vcd274", 0.0, 0,
        listOf("dept_visual_communication_design"), emptyList()
    )
    val vcd311 = Course(
        "vcd311", "VCD 311", "Introduction to Digital Video", "fac_communication", 6, 3, "vcd311", 0.0, 0,
        listOf("dept_visual_communication_design"), emptyList()
    )
    val vcd321 = Course(
        "vcd321", "VCD 321", "Culturel Icons in Design", "fac_communication", 3, 3, "vcd321", 0.0, 0,
        listOf("dept_visual_communication_design"), emptyList()
    )
    val courses = listOf(vcd111, vcd171, vcd271, vcd272, vcd273, vcd274, vcd311, vcd321)
    val enrolledIds = listOf("vcd272") // Ortadaki ders seçili görünsün

    AddCourse2Content(
        avatarResId = R.drawable.turuncu,
        onAvatarClick = {},
        controller = NavController(LocalContext.current),
        query = "",
        onQueryChange = {},
        departmentName = "Visual Communication Design",
        courses = courses,
        enrolledCourseIds = enrolledIds,
        onCourseToggle = { _, _ -> }
    )
}