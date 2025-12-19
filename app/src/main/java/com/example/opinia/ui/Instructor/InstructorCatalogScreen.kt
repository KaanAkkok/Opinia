package com.example.opinia.ui.instructor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandCircleDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex // <-- Bu önemli, listeyi üstte tutar
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.opinia.data.model.Faculty
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.OpTopBar
import com.example.opinia.ui.search.GeneralSearchBar // <-- Yeni barımız
import com.example.opinia.ui.search.SearchViewModel // <-- Yeni beynimiz
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.black

@Composable
fun InstructorCatalogScreen(
    navController: NavController,
    viewModel: InstructorViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel() // <-- 1. SearchViewModel eklendi
) {
    val uiState by viewModel.uiState.collectAsState()

    // Status bar rengi ayarı (Bunu senin için korudum)
    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    InstructorCatalogContent(
        uiState = uiState,
        searchViewModel = searchViewModel, // <-- İçeri paslıyoruz
        navController = navController,
        onToggleDepartments = { viewModel.toggleDepartments() },
        onDepartmentClicked = { deptId ->
            // Normal departman seçimi (düz giriş)
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
    onToggleDepartments: () -> Unit,
    onDepartmentClicked: (String) -> Unit,
    onAvatarClick: () -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = OpiniaGreyWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            OpTopBar(
                title = "Professors",
                avatarResId = uiState.userAvatarResId,
                onAvatarClick = onAvatarClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- YENİ SEARCH BAR (ÖZEL DAVETİYE KISMI) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(10f) // <-- Listeyi diğer yazıların üstüne çıkarır
            ) {
                GeneralSearchBar(
                    searchViewModel = searchViewModel,
                    onNavigateToCourse = { courseId ->
                        // Burası kurs detayına gider (senin işin değilse boş kalabilir)
                        // navController.navigate(Destination.COURSE_DETAIL.route.replace("{courseId}", courseId))
                    },
                    onNavigateToInstructor = { instructor ->
                        // İŞTE SİHİR BURADA:
                        // 1. Hocanın departmanını buluyoruz
                        val deptId = instructor.departmentIds.firstOrNull() ?: "unknown"

                        // 2. Rotayı oluşturuyoruz: Hem departman ID hem de Hoca ID ekliyoruz
                        val route = Destination.INSTRUCTOR_LIST.route
                            .replace("{departmentName}", deptId)
                            .replace("{targetInstructorId}", instructor.instructorId)

                        // 3. Gönderiyoruz
                        navController.navigate(route)
                    }
                )
            }
            // ---------------------------------------------

            Spacer(modifier = Modifier.height(24.dp))

            // --- ESKİ SEARCH SONUÇLARI SİLİNDİ, SADECE FAKÜLTE LİSTESİ KALDI ---
            if (uiState.isLoading && uiState.facultyList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Text(
                    color = black,
                    text = "Faculties",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val displayText = uiState.selectedFaculty?.facultyName ?: "Faculty of Communication"
                FacultyHeaderBox(
                    text = displayText,
                    isExpanded = uiState.isDepartmentsVisible,
                    onClick = onToggleDepartments
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState.isDepartmentsVisible) {
                    Text(
                        color = black,
                        text = "Departments",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(uiState.departmentList) { dept ->
                            DepartmentItem(
                                name = dept.departmentName,
                                onClick = { onDepartmentClicked(dept.departmentId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- YARDIMCI BİLEŞENLER (Aynen korudum) ---

@Composable
fun DepartmentItem(name: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(com.example.opinia.ui.theme.OpiniaPurple)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = name, style = MaterialTheme.typography.bodyLarge, color = black.copy(alpha = 0.8f))
    }
}

@Composable
fun FacultyHeaderBox(text: String, isExpanded: Boolean, onClick: () -> Unit) {
    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "Arrow")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(Color(0xFF9E9EE8)) // Senin istediğin renk
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = black.copy(alpha = 0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f) // Oku sağa yaslar
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(Color.White, androidx.compose.foundation.shape.CircleShape)
            )
            Icon(
                imageVector = Icons.Default.ExpandCircleDown,
                contentDescription = "Expand",
                tint = OpiniaDeepBlue,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationState)
            )
        }
    }
}