package com.example.opinia.ui.instructor

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExpandCircleDown
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.opinia.data.model.Faculty
import com.example.opinia.data.model.Instructor
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.OpSearchBar
import com.example.opinia.ui.components.OpTopBar
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.black

@Composable
fun InstructorCatalogScreen(
    navController: NavController,
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

    InstructorCatalogContent(
        uiState = uiState,
        searchText = searchText,
        navController = navController,
        onSearchChange = {
            searchText = it
            viewModel.onCatalogSearch(it)
        },
        onToggleDepartments = { viewModel.toggleDepartments() },
        onDepartmentClicked = { deptId ->
            navController.navigate(Destination.INSTRUCTOR_LIST.route.replace("{departmentName}", deptId))
        },
        onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) }
    )
}

@Composable
fun InstructorCatalogContent(
    uiState: InstructorUiState,
    searchText: String,
    navController: NavController,
    onSearchChange: (String) -> Unit,
    onToggleDepartments: () -> Unit,
    onDepartmentClicked: (String) -> Unit,
    onAvatarClick: () -> Unit
) {
    Scaffold(
        // topBar PARAMETRESİ KALDIRILDI
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = OpiniaGreyWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp) // Dashboard ile aynı yan boşluk
        ) {
            // DASHBOARD İLE AYNI ÜST BOŞLUK (50dp)
            Spacer(modifier = Modifier.height(50.dp))

            // HEADER ARTIK BURADA
            OpTopBar(
                title = "Professors",
                avatarResId = uiState.userAvatarResId,
                onAvatarClick = onAvatarClick
            )

            // Header ile SearchBar arası boşluk (24dp)
            Spacer(modifier = Modifier.height(24.dp))

            OpSearchBar(value = searchText, onValueChange  = onSearchChange)

            Spacer(modifier = Modifier.height(24.dp))

            // --- ARAMA MODU ---
            if (uiState.isGlobalSearching) {
                Text(
                    text = "Search Results",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.globalSearchResults.isEmpty()) {
                    Text(text = "No professors found.", color = Color.Gray)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(uiState.globalSearchResults) { instructor ->
                            CatalogProfessorCard(instructor)
                        }
                    }
                }

            } else {
                // --- NORMAL MOD ---
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
}

// --- YARDIMCI BİLEŞENLER (Aynı Kalıyor) ---
@Composable
fun CatalogProfessorCard(instructor: Instructor) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OpinialightBlue, shape = RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CatalogProfessorInfoRow(icon = Icons.Outlined.Person, text = "${instructor.instructorTitle} ${instructor.instructorName}", isBold = true)
        CatalogProfessorInfoRow(icon = Icons.Outlined.Phone, text = instructor.phoneNumber ?: "N/A")
        CatalogProfessorInfoRow(icon = Icons.Outlined.Email, text = instructor.instructorEmail)
    }
}

@Composable
fun CatalogProfessorInfoRow(icon: ImageVector, text: String, isBold: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon, contentDescription = null,
            modifier = Modifier.size(20.dp), tint = black.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = black.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun DepartmentItem(name: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(OpiniaPurple)
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
            .background(OpiniaPurple)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start // Sola yasla
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = black.copy(alpha = 0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
            // modifier = Modifier.weight(1f) <-- BU SATIRI SİLDİK
        )

        Spacer(modifier = Modifier.width(8.dp)) // Yazı ile ok arasına minik boşluk

        Icon(
            imageVector = Icons.Default.ExpandCircleDown,
            contentDescription = "Expand",
            tint = OpiniaDeepBlue,
            modifier = Modifier.size(24.dp).rotate(rotationState)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InstructorCatalogScreenPreview() {
    val dummyFaculties = listOf(Faculty("1", "Faculty of Communication"))
    val dummyState = InstructorUiState(
        facultyList = dummyFaculties,
        selectedFaculty = dummyFaculties[0],
        isDepartmentsVisible = false
    )
    InstructorCatalogContent(
        uiState = dummyState, searchText = "", navController = rememberNavController(),
        onSearchChange = {}, onToggleDepartments = {}, onDepartmentClicked = {}, onAvatarClick = {}
    )
}
@Preview(showBackground = true, name = "Departments Expanded (Açık)")
@Composable
fun InstructorCatalogScreenExpandedPreview() {
    // 1. Dummy Fakülte
    val dummyFaculties = listOf(Faculty("1", "Faculty of Communication"))

    // 2. Dummy Departmanlar (Açıldığında görünecekler)
    val dummyDepartments = listOf(
        com.example.opinia.data.model.Department("1", "Visual Communication Design", "1"),
        com.example.opinia.data.model.Department("2", "Public Relations", "1"),
        com.example.opinia.data.model.Department("3", "Radio, Television and Cinema", "1")
    )

    // 3. State'i "AÇIK" (isDepartmentsVisible = true) olarak ayarlıyoruz
    val dummyState = InstructorUiState(
        facultyList = dummyFaculties,
        selectedFaculty = dummyFaculties[0],
        departmentList = dummyDepartments, // Listeyi veriyoruz
        isDepartmentsVisible = true // <--- İŞTE BURASI KİLİT NOKTA: True yaptık
    )

    InstructorCatalogContent(
        uiState = dummyState,
        searchText = "",
        navController = rememberNavController(),
        onSearchChange = {},
        onToggleDepartments = {},
        onDepartmentClicked = {},
        onAvatarClick = {}
    )
}