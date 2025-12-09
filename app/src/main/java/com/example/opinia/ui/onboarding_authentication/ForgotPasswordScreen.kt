package com.example.opinia.ui.onboarding_authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.ui.components.ResetPasswordButton
import com.example.opinia.ui.components.TextFieldInput
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ForgotPasswordScreenContent(
    emailValue: String,
    onEmailChange: (String) -> Unit,
    isButtonEnabled: Boolean,
    onResetPasswordClick: () -> Unit,
    onGoBackToLoginClick: () -> Unit
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

        Spacer(modifier = Modifier.height(120.dp))

        ResetPasswordButton(onClick = onResetPasswordClick, isButtonEnabled)

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onGoBackToLoginClick) {
            Text(text = "Back to login", style = MaterialTheme.typography.bodyMedium, color = OpinialightBlue)
        }

    }
}

@Composable
fun ForgotPasswordScreen(navController: NavController, forgotPasswordViewModel: ForgotPasswordViewModel) {

    val uiState by forgotPasswordViewModel.uiState.collectAsState()
    val isButtonEnabled by forgotPasswordViewModel.isResetButtonEnabled.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        forgotPasswordViewModel.uiEvent.collectLatest { event ->
            when(event) {
                is ForgotPasswordUiEvent.ForgotPasswordError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    ForgotPasswordScreenContent(
        emailValue = uiState.email,
        onEmailChange = { forgotPasswordViewModel.onEmailChange(it) },
        isButtonEnabled = isButtonEnabled, // 15 saniye kuralı burada işleyecek
        onResetPasswordClick = { forgotPasswordViewModel.onResetPasswordClicked() },
        onGoBackToLoginClick = { navController.popBackStack() }
    )

}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreenContent(
        emailValue = "ogrenci@ornek.edu.tr",
        onEmailChange = {},
        isButtonEnabled = true,
        onResetPasswordClick = {},
        onGoBackToLoginClick = {}
    )
}