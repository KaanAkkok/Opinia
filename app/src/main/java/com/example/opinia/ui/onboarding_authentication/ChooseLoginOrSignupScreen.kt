package com.example.opinia.ui.onboarding_authentication

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.ui.Destination
import com.example.opinia.ui.components.LoginButton
import com.example.opinia.ui.components.SignupButton
import com.example.opinia.ui.theme.OpiniaDeepBlue

@Composable
fun ChooseLoginOrSignupScreen(navController: NavController) {
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
    ) {

        Image(
            painter = painterResource(id = R.drawable.yeni_acikmavi_logo),
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

            LoginButton(onClick = onLoginClick)

            Spacer(modifier = Modifier.height(30.dp))

            SignupButton(onClick = onSignupClick)

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseLoginOrSignupPreview() {
    ChooseLoginOrSignupContent(onLoginClick = {}, onSignupClick = {})
}
