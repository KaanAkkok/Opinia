package com.example.opinia.ui.onboarding_authentication

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.ui.Destination
import com.example.opinia.ui.theme.OpiniaDeepBlue
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, splashViewModel: SplashViewModel) {

    val uiState by splashViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    LaunchedEffect(uiState) {
        delay(2000) //2 saniye bekle
        when (val state = uiState) {
            is SplashUiState.GoToDashboard -> {
                navController.navigate(Destination.DASHBOARD.route) {
                    popUpTo(Destination.START.route) { inclusive = true }
                }
            }
            is SplashUiState.GoToChooseLoginOrSignup -> {
                navController.navigate(Destination.CHOOSE_LOGIN_OR_SIGNUP.route) {
                    popUpTo(Destination.START.route) { inclusive = true }
                }
            }
            is SplashUiState.NoInternet -> {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
            is SplashUiState.Loading -> {
                Unit //sadece beklesin
            }
        }
    }

    SplashScreenContent()
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OpiniaDeepBlue)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.yeni_acikmavi_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(285.dp)
                    .height(85.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreenContent()
}