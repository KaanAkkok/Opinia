package com.example.opinia.ui.instructor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomDropdown
import com.example.opinia.ui.components.CustomTopAppBar
import com.example.opinia.ui.search.GeneralSearchBar
import com.example.opinia.ui.search.SearchViewModel
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.black
import com.example.opinia.ui.theme.gray

@Composable
fun InstructorCatalogScreen(
    navController: NavController,
    viewModel: InstructorViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    InstructorCatalogContent(
        uiState = uiState,
        searchViewModel = searchViewModel,
        navController = navController,
        onFacultySelected = { viewModel.onFacultySelected(it) },
        onDepartmentClicked = { deptId ->
            val baseRoute = Destination.INSTRUCTOR_LIST.route.substringBefore("?")
            navController.navigate(baseRoute.replace("{departmentName}", deptId))
        },
        onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) }
    )
}

@Composable
fun InstructorCatalogContent(
    uiState: InstructorUiState,
    searchViewModel: SearchViewModel,
    navController: NavController,
    onFacultySelected: (com.example.opinia.data.model.Faculty) -> Unit,
    onDepartmentClicked: (String) -> Unit,
    onAvatarClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CustomTopAppBar(
                    avatarResId = uiState.userAvatarResId,
                    onAvatarClick = onAvatarClick,
                    text = "Professors" // <--- İsim değişti
                )
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(10f)
                ) {
                    GeneralSearchBar(
                        searchViewModel = searchViewModel,
                        onNavigateToCourse = { courseId ->
                            navController.navigate(Destination.COURSE_DETAIL.route.replace("{courseId}", courseId))
                        },
                        onNavigateToInstructor = { instructor ->
                            val deptId = instructor.departmentIds.firstOrNull() ?: "unknown"
                            val route = Destination.INSTRUCTOR_LIST.route
                                .replace("{departmentName}", deptId)
                                .replace("{targetInstructorId}", instructor.instructorId)
                            navController.navigate(route)
                        }
                    )
                }
            }
        },
        bottomBar = { BottomNavBar(navController = navController) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(OpiniaGreyWhite)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 1. FAKÜLTE BAŞLIĞI
            Text(
                color = black,
                text = "Faculties",
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. DROPDOWN (DÜZELTİLDİ)
            // defaultText yerine placeholder kullanıyoruz.
            // Eğer parametre isimlerinde hala hata alırsan 'items', 'selectedItem' gibi isimleri silip
            // sırasıyla (pozisyonel olarak) vermeyi deneyebilirsin, ama muhtemelen bu isimler doğrudur.
            CustomDropdown(
                items = uiState.facultyList,
                selectedItem = uiState.selectedFaculty,
                onItemSelected = onFacultySelected,
                itemLabel = { it.facultyName },
                placeholder = "Select Faculty", // <--- BURASI DÜZELTİLDİ (Eskiden defaultText idi)
                color = Color(0xFF9E9EE8)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. DEPARTMAN LİSTESİ
            if (uiState.selectedFaculty != null) {
                Text(
                    color = black,
                    text = "Departments",
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.departmentList) { dept ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(shape = MaterialTheme.shapes.extraLarge)
                                .background(Color(0xFFB4B4ED))
                                .clickable { onDepartmentClicked(dept.departmentId) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dept.departmentName,
                                color = black,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                                fontFamily = WorkSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        }
                    }
                    if (uiState.departmentList.isEmpty()) {
                        item {
                            Text(
                                text = "No departments found.",
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