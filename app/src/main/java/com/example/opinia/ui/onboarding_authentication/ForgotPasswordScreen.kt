package com.example.opinia.ui.onboarding_authentication

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.ui.components.CustomButton
import com.example.opinia.ui.components.TextFieldInput
import com.example.opinia.ui.theme.NunitoFontFamily
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
            .background(OpiniaDeepBlue)
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.yeni_acik_amblem),
            contentDescription = "Logo",
            modifier = Modifier
                .width(210.dp)
                .height(63.dp)
        )

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Reset Password",
            color = OpinialightBlue,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            "Student Email",
            color = OpinialightBlue,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldInput(emailValue, onValueChange = onEmailChange)

        Spacer(modifier = Modifier.height(120.dp))

        CustomButton(
            onClick = onResetPasswordClick,
            text = "Reset Password",
            isButtonEnabled = isButtonEnabled,
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.titleMedium,
            containerColor = OpinialightBlue,
            contentColor = OpiniaDeepBlue,
            modifier = Modifier
                .height(42.dp)
                .width(200.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onGoBackToLoginClick) {
            Text(
                text = "Back to login",
                color = OpinialightBlue,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp
            )
        }

    }
}

@Composable
fun ForgotPasswordScreen(navController: NavController, forgotPasswordViewModel: ForgotPasswordViewModel) {

    val uiState by forgotPasswordViewModel.uiState.collectAsState()
    val isButtonEnabled by forgotPasswordViewModel.isResetButtonEnabled.collectAsState()
    val context = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

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