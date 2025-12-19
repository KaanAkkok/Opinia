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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.opinia.data.model.Instructor
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.OpSearchBar
import com.example.opinia.ui.components.OpTopBar
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.black

@Composable
fun InstructorListScreen(
    navController: NavController,
    departmentId: String,
    targetInstructorId: String? = null, // <--- BU SATIRI EKLE (varsayılanı null)
    viewModel: InstructorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            // true = siyah ikonlar (açık renk arka plan için)
            androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(departmentId, targetInstructorId) {
        viewModel.loadInstructorsForDepartment(departmentId, targetInstructorId)
    }

    InstructorListContent(
        uiState = uiState,
        searchText = searchText,
        navController = navController,
        onSearchChange = {
            searchText = it
            viewModel.searchInstructorsLocally(it)
        },
        onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) }
    )
}

@Composable
fun InstructorListContent(
    uiState: InstructorUiState,
    searchText: String,
    navController: NavController,
    onSearchChange: (String) -> Unit,
    onAvatarClick: () -> Unit
) {
    Scaffold(
        // topBar PARAMETRESİ KALDIRILDI
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            // DASHBOARD İLE AYNI ÜST BOŞLUK (50dp)
            Spacer(modifier = Modifier.height(50.dp))

            // HEADER BURADA
            OpTopBar(
                title = "Professors",
                avatarResId = uiState.userAvatarResId,
                onAvatarClick = onAvatarClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            OpSearchBar(
                value = searchText,
                onValueChange = onSearchChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Text(
                    color = black,
                    text = uiState.currentDepartmentName,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
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
            isBold = false // <--- BURAYI true YERİNE false YAP
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
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
            ),
            color = Color.Black.copy(alpha = 0.8f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InstructorListScreenPreview() {
    val dummyInstructors = listOf(
        Instructor(instructorName = "Neda Üçer", instructorTitle = "Prof.", instructorEmail = "neda@edu.tr", phoneNumber = "1234"),
        Instructor(instructorName = "Cem Bölüktaş", instructorTitle = "Asst. Prof.", instructorEmail = "cem@edu.tr", phoneNumber = "5678")
    )
    val dummyState = InstructorUiState(
        currentDepartmentName = "Visual Communication Design",
        filteredInstructorList = dummyInstructors
    )

    InstructorListContent(
        uiState = dummyState,
        searchText = "",
        navController = rememberNavController(),
        onSearchChange = {},
        onAvatarClick = {}
    )
}