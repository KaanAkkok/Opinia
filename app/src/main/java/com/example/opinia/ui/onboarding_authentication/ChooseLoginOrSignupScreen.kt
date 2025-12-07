package com.example.opinia.ui.onboarding_authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.ui.Destination
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue

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
            contentDescription = "Opinia Logo",
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
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .height(40.dp)
                    .width(200.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OpinialightBlue,
                    contentColor = OpiniaDeepBlue
                )
            ) {
                Text("Log In", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold))
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onSignupClick,
                modifier = Modifier
                    .height(40.dp)
                    .width(200.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OpinialightBlue,
                    contentColor = OpiniaDeepBlue
                )
            ) {
                Text("Sign Up", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChooseLoginOrSignupPreview() {
    ChooseLoginOrSignupContent(onLoginClick = {}, onSignupClick = {})
}
