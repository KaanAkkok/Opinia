package com.example.opinia.ui.onboarding_authentication

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.data.model.Course
import com.example.opinia.ui.Destination
import com.example.opinia.ui.components.CustomButton
import com.example.opinia.ui.components.CustomCourseCard
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.WorkSansFontFamily
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignupStudentCourseContent(
    courses : List<Course>,
    selectedCourses : List<Course>,
    onCourseSelected : (Course) -> Unit,
    onSignUpClick : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OpiniaDeepBlue)
            .padding(bottom = 32.dp)
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.yeni_acikmavi_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .width(210.dp)
                .height(63.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Choose Your Courses",
            color = OpinialightBlue,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(courses) { course ->
                val isSelected = selectedCourses.contains(course)
                CustomCourseCard(
                    course = course,
                    isActive = isSelected,
                    onRowClick = { onCourseSelected(course) },
                    onIconClick = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    backgroundColor = OpinialightBlue,
                    activeIcon = Icons.Default.CheckBox,
                    inactiveIcon = Icons.Default.CheckBoxOutlineBlank,
                    iconSize = 20.dp,
                    codeStyle = SpanStyle(fontFamily = WorkSansFontFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp),
                    nameStyle = SpanStyle(fontFamily = WorkSansFontFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        CustomButton(
            onClick = onSignUpClick,
            text = "Sign Up",
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.titleMedium,
            containerColor = OpinialightBlue,
            contentColor = OpiniaDeepBlue,
            modifier = Modifier
                .height(42.dp)
                .width(180.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SignupStudentCourseScreen(navController: NavController, registerViewModel: RegisterViewModel) {

    val uiState by registerViewModel.uiState.collectAsState()
    val context  = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    LaunchedEffect(key1 = true) {
        registerViewModel.uiEvent.collectLatest { event ->
            when(event) {
                is RegisterUiEvent.SignupError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is RegisterUiEvent.SignupSuccess -> {
                    Toast.makeText(context, "Signup Success", Toast.LENGTH_SHORT).show()
                    navController.navigate(Destination.DASHBOARD.route) {
                        popUpTo(Destination.CHOOSE_LOGIN_OR_SIGNUP.route) { inclusive = true }
                    }
                }
                else -> Unit
            }
        }
    }

    SignupStudentCourseContent(
        courses = uiState.availableCourses,
        selectedCourses = uiState.selectedCourses,
        onCourseSelected = { registerViewModel.onCourseSelected(it) },
        onSignUpClick = {
            if (uiState.selectedCourses.isNotEmpty()) {
                registerViewModel.completeRegistration()
            } else {
                // Ders seçilmediyse kullanıcıyı uyar
                Toast.makeText(context, "Lütfen en az bir ders seçiniz.", Toast.LENGTH_SHORT).show()
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun SignupStudentCourseScreenPreview() {
    val sampleCourses = listOf(
        Course("VCD 111", "VCD 111", "Basic Drawing"),
        Course("VCD 171", "VCD 171", "Design Fundamentals"),
        Course("VCD 271", "VCD 271", "Modeling in Virtual Environments"),
        Course("VCD 272", "VCD 272", "Motion Design in 3D"),
        Course("VCD 273", "VCD 273", "Digital Design and Illustration"),
        Course("VCD 274", "VCD 274", "Motion Graphics"),
        Course("VCD 311", "VCD 311", "Introduction to Digital Video"),
        Course("VCD 312", "VCD 312", "Digital Video Production"),
        Course("VCD 321", "VCD 321", "Cultural Icons in Design"),
        Course("VCD 371", "VCD 371", "Introduction to Game Design")
    )

    val sampleSelectedCourses = listOf(
        sampleCourses[1],
        sampleCourses[3]
    )

    SignupStudentCourseContent(
        courses = sampleCourses,
        selectedCourses = sampleSelectedCourses,
        onCourseSelected = {},
        onSignUpClick = {}
    )
}