package com.example.opinia.ui.instructor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.opinia.data.model.Instructor
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomTopAppBar
import com.example.opinia.ui.search.GeneralSearchBar // Yeni bar
import com.example.opinia.ui.search.SearchViewModel // Yeni viewmodel
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.black

@Composable
fun InstructorListScreen(
    navController: NavController,
    departmentId: String,
    targetInstructorId: String? = null,
    viewModel: InstructorViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel() // YENİ: Parametre olarak eklendi
) {
    val uiState by viewModel.uiState.collectAsState()

    // Eski searchText state'ini sildik çünkü artık GeneralSearchBar kullanıyoruz.

    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(departmentId, targetInstructorId) {
        viewModel.loadInstructorsForDepartment(departmentId, targetInstructorId)
    }

    InstructorListContent(
        uiState = uiState,
        searchViewModel = searchViewModel, // İçeri gönderiyoruz
        navController = navController,
        onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) }
    )
}

@Composable
fun InstructorListContent(
    uiState: InstructorUiState,
    searchViewModel: SearchViewModel, // YENİ
    navController: NavController,
    onAvatarClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            // YENİ YAPI: TopBar ve SearchBar bir arada
            Column(modifier = Modifier.fillMaxWidth()) {
                CustomTopAppBar(
                    avatarResId = uiState.userAvatarResId,
                    onAvatarClick = onAvatarClick,
                    text = "Professors"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(10f) // SearchBar'ın listenin üstünde kalması için
                ) {
                    GeneralSearchBar(
                        searchViewModel = searchViewModel,
                        onNavigateToCourse = { courseId ->
                            navController.navigate(Destination.COURSE_DETAIL.route.replace("{courseId}", courseId))
                        }, // Burası boş kalabilir
                        onNavigateToInstructor = { instructor ->
                            // Arama sonucuna tıklayınca o hocaya gitme mantığı
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
            // Eski SearchBar buradan kaldırıldı.

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Text(
                    color = black,
                    text = uiState.currentDepartmentName,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.filteredInstructorList) { prof ->
                        ProfessorCard(prof)
                    }
                }
            }
        }
    }
}

// ProfessorCard ve ProfessorInfoRow bileşenlerin aynı kalabilir, aşağıda tekrar yazmadım ama dosyanın altında durmalılar.
@Composable
fun ProfessorCard(instructor: Instructor) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OpinialightBlue, shape = RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ProfessorInfoRow(
            icon = Icons.Outlined.AccountBox,
            text = "${instructor.instructorTitle} ${instructor.instructorName}",
            isBold = false
        )
        ProfessorInfoRow(icon = Icons.Outlined.Phone, text = instructor.phoneNumber ?: "N/A")
        ProfessorInfoRow(icon = Icons.Outlined.Email, text = instructor.instructorEmail)
    }
}

@Composable
fun ProfessorInfoRow(icon: ImageVector, text: String, isBold: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Black.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontFamily = WorkSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = Color.Black.copy(alpha = 0.8f)
        )
    }
}