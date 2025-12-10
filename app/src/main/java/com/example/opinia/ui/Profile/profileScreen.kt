package com.example.opinia.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.opinia.R
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue

// Renk Tanımları
val DarkBlue = Color(0xFF163456)
val LightBackground = Color(0xFFEFF3F6)
val LightBlueButton = Color(0xFFD4E1F2)

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileScreenViewModel // BURASI DEĞİŞTİ: Artık ViewModel alıyor
) {
    ProfileContent(
        navController = navController,
        onLogoutClick = {
            // ViewModel üzerinden çıkış yapılıyor
            viewModel.signOut {
                // Çıkış tamamlanınca Login ekranına yönlendir ve stack'i temizle
                navController.navigate(Destination.CHOOSE_LOGIN_OR_SIGNUP.route) { // Destination.LOGIN.route string'ini buraya yazmalısın
                    popUpTo(0) { inclusive = true } // Veya Dashboard'a kadar sil
                }
            }
        },
        onSavedCoursesClick = {},
        onAddCourseClick = {},
        onChangeProfileClick = {},
        onChangePasswordClick = {},
        onSupportClick = {}
    )
}

@Composable
fun ProfileContent(
    navController: NavController,
    onLogoutClick: () -> Unit,
    onSavedCoursesClick: () -> Unit,
    onAddCourseClick: () -> Unit,
    onChangeProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    Scaffold(
        containerColor = LightBackground,
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(185.dp))

            Image(
                painter = painterResource(id = R.drawable.yeni_lacivert_logo),
                contentDescription = "Opinia Logo",
                modifier = Modifier
                    .width(200.dp)
                    .height(80.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            val buttonWidth = 300.dp
            val buttonHeight = 40.dp

            MenuButton(text = "Saved Courses", width = buttonWidth, height = buttonHeight, onClick = onSavedCoursesClick)
            Spacer(modifier = Modifier.height(25.dp))

            MenuButton(text = "Add Course", width = buttonWidth, height = buttonHeight, onClick = onAddCourseClick)
            Spacer(modifier = Modifier.height(25.dp))

            MenuButton(text = "Change Profile", width = buttonWidth, height = buttonHeight, onClick = onChangeProfileClick)
            Spacer(modifier = Modifier.height(25.dp))

            MenuButton(text = "Change Password", width = buttonWidth, height = buttonHeight, onClick = onChangePasswordClick)
            Spacer(modifier = Modifier.height(25.dp))

            MenuButton(text = "Support", width = buttonWidth, height = buttonHeight, onClick = onSupportClick)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = LightBlueButton),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .width(220.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = "Log Out",
                    color = OpiniaDeepBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun MenuButton(text: String, width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = OpiniaDeepBlue),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .width(width)
            .height(height)
    ) {
        Text(
            text = text,
            color = OpinialightBlue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileContent(
            navController = rememberNavController(),
            onLogoutClick = {},
            onSavedCoursesClick = {},
            onAddCourseClick = {},
            onChangeProfileClick = {},
            onChangePasswordClick = {},
            onSupportClick = {}
        )
    }
}
