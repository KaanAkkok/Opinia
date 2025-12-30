package com.example.opinia.ui.onboarding_authentication

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.ui.Destination
import com.example.opinia.ui.components.CustomButton
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue

@Composable
fun ChooseLoginOrSignupScreen(navController: NavController) {

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    ChooseLoginOrSignupContent(
        onLoginClick = { navController.navigate(Destination.LOGIN.route) },
        onSignupClick = { navController.navigate(Destination.SIGNUP_PERSONAL_INFO.route) }
    )
}

@Composable
fun ChooseLoginOrSignupContent(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OpiniaDeepBlue)
            .padding(horizontal = 12.dp)
    ) {

        Image(
            painter = painterResource(id = R.drawable.yeni_acik_amblem),
            contentDescription = "Logo",
            modifier = Modifier
                .width(285.dp)
                .height(85.dp)
                .align(Alignment.Center)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CustomButton(
                onClick = onLoginClick,
                text = "Log In",
                shape = MaterialTheme.shapes.medium,
                textStyle = MaterialTheme.typography.titleMedium,
                containerColor = OpinialightBlue,
                contentColor = OpiniaDeepBlue,
                modifier = Modifier
                    .height(42.dp)
                    .width(180.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            CustomButton(
                onClick = onSignupClick,
                text = "Sign Up",
                shape = MaterialTheme.shapes.medium,
                textStyle = MaterialTheme.typography.titleMedium,
                containerColor = OpinialightBlue,
                contentColor = OpiniaDeepBlue,
                modifier = Modifier
                    .height(42.dp)
                    .width(180.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseLoginOrSignupPreview() {
    ChooseLoginOrSignupContent(onLoginClick = {}, onSignupClick = {})
}
