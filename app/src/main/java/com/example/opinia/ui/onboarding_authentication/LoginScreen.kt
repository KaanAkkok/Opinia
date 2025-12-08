package com.example.opinia.ui.onboarding_authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.ui.Destination
import com.example.opinia.ui.components.LoginButton
import com.example.opinia.ui.components.PasswordTextFieldInput
import com.example.opinia.ui.components.TextFieldInput
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreenContent(
    emailValue: String,
    passwordValue: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OpiniaDeepBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.yeni_acikmavi_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .width(210.dp)
                .height(63.dp)
        )

        Spacer(modifier = Modifier.height(120.dp))

        Text("Student Email", style = MaterialTheme.typography.bodyMedium, color = OpinialightBlue)

        Spacer(modifier = Modifier.height(16.dp))

        TextFieldInput(emailValue, onValueChange = onEmailChange)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Password", style = MaterialTheme.typography.bodyMedium, color = OpinialightBlue)

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.width(IntrinsicSize.Max)) {

            PasswordTextFieldInput(value = passwordValue, onValueChange = onPasswordChange)

            TextButton(
                onClick = onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Forgot My Password", style = MaterialTheme.typography.bodySmall, color = OpinialightBlue)
            }
        }

        Spacer(modifier = Modifier.height(120.dp))

        LoginButton(onClick = onLoginClick)
    }
}

@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel) {
    val uiState by loginViewModel.uiState.collectAsState()
    val context = LocalContext.current // Toast için Context gerekli

    LaunchedEffect(key1 = true) {
        loginViewModel.uiEvent.collectLatest { event ->
            when(event) {
                is LoginUiEvent.LoginError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is LoginUiEvent.LoginSuccess -> {
                    navController.navigate(Destination.DASHBOARD.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    LoginScreenContent(
        emailValue = uiState.email,
        passwordValue = uiState.password,
        onEmailChange = { loginViewModel.onEmailChange(it) },
        onPasswordChange = { loginViewModel.onPasswordChange(it) },
        onLoginClick = { loginViewModel.onLoginClicked() },
        onForgotPasswordClick = { navController.navigate(Destination.FORGOT_PASSWORD.route) }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        emailValue = "ogrenci@ornek.edu.tr",
        passwordValue = "123456",
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onForgotPasswordClick = {}
    )
}