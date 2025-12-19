@file:Suppress("DEPRECATION")

package com.example.opinia.ui.catalog

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.black
import com.example.opinia.ui.theme.gray

@Composable
fun AddCourse1Content(
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
                    text = "Add Course",
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Arama Çubuğu Alanı
                if (!isPreview && searchViewModel != null) {
                    Box(modifier = Modifier.fillMaxWidth().zIndex(10f)) {
                        GeneralSearchBar(
                            searchViewModel = searchViewModel,
                            onNavigateToCourse = { courseId ->
                                controller.navigate(Destination.COURSE_DETAIL.route.replace("{courseId}", courseId))
                            },
                            onNavigateToInstructor = {
                                controller.navigate(Destination.INSTRUCTOR_LIST.route)
                            }
                        )
                    }
                } else if (isPreview) {
                    // Preview modunda arama çubuğu temsili
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(48.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .background(OpinialightBlue),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("  Search...", color = black.copy(0.5f), modifier = Modifier.padding(start = 16.dp))
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
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // --- FACULTIES SECTION ---
            Text(
                text = "Faculties",
                style = MaterialTheme.typography.titleMedium,
                color = black,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )

            // Arama yapılıyorsa fakülteleri liste olarak göster, değilse Dropdown kullan
            if (query.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableFaculties) { faculty ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(OpiniaPurple)
                                .clickable { onFacultySelected(faculty) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = faculty.facultyName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = black
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
                // Dropdown bileşeni (Görseldeki Faculty of Communication kısmı)
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CustomDropdown(
                        items = availableFaculties,
                        selectedItem = selectedFaculty,
                        onItemSelected = onFacultySelected,
                        itemLabel = { it.facultyName },
                        placeholder = "Select Faculty"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- DEPARTMENTS SECTION ---
            // Sadece bir fakülte seçiliyse ve arama yapılmıyorsa bölümleri göster
            if (selectedFaculty != null && query.isEmpty()) {
                Text(
                    text = "Departments",
                    style = MaterialTheme.typography.titleMedium,
                    color = black,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(availableDepartments) { department ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp) // Görseldeki gibi biraz yüksek
                                .clip(shape = MaterialTheme.shapes.medium) // Yuvarlak köşeler
                                .background(OpiniaPurple) // Mor arka plan
                                .clickable { onDepartmentSelected(department) } // TIKLANABİLİR
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = department.departmentName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = black
                            )
                        }
                    }
                    if (availableDepartments.isEmpty()) {
                        item {
                            Text(
                                text = "Loading departments...",
                                modifier = Modifier.padding(8.dp),
                                color = gray
                            )
                        }
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun AddCourse1ScreenPreview() {
    val sampleFaculties = listOf(
        Faculty("1", "Faculty of Communication"),
        Faculty("2", "Faculty of Engineering")
    )
    val sampleDepartments = listOf(
        Department("1", "Visual Communication Design", "1"),
        Department("2", "Radio Television and Cinema", "1"),
        Department("3", "Advertising Design and Communucation", "1"),
        Department("4","Public Relations and Advertising", "2"),
        Department("5","Journalism" , "2")

    )

    AddCourse1Content(
        avatarResId = R.drawable.turuncu,
        onAvatarClick = {},
        query = "",
        controller = NavController(LocalContext.current),
        availableFaculties = sampleFaculties,
        // FIX: Select the first faculty from the list for the preview.
        selectedFaculty = sampleFaculties.first(),
        onFacultySelected = {},
        availableDepartments = sampleDepartments,
        onDepartmentSelected = {}
    )
}

