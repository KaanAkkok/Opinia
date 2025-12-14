package com.example.opinia.ui.profile

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomButton
import com.example.opinia.ui.components.PasswordTextFieldInput
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpinialightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordContent(
    controller: NavController,
    oldPassword: String = "",
    onOldPasswordChange: (String) -> Unit,
    newPassword: String = "",
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String = "",
    onConfirmPasswordChange: (String) -> Unit,
    onChangeClicked: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.yeni_lacivert_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .width(210.dp)
                            .height(63.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OpiniaGreyWhite,
                    scrolledContainerColor = OpiniaGreyWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 72.dp)
            )
        },
        bottomBar = {
            BottomNavBar(navController = controller)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(OpiniaGreyWhite)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text("Change Password", style = MaterialTheme.typography.titleLarge, color = OpiniaDeepBlue)

            Spacer(modifier = Modifier.height(36.dp))

            Text("Old Password", style = MaterialTheme.typography.titleMedium, color = OpiniaDeepBlue)

            Spacer(modifier = Modifier.height(12.dp))

            PasswordTextFieldInput(
                oldPassword,
                onOldPasswordChange,
                backgroundColor = OpiniaDeepBlue,
                textColor = OpinialightBlue,
                iconColor = OpinialightBlue,
                cursorColor = OpinialightBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("New Password", style = MaterialTheme.typography.titleMedium, color = OpiniaDeepBlue)

            Spacer(modifier = Modifier.height(12.dp))

            PasswordTextFieldInput(
                newPassword,
                onNewPasswordChange,
                backgroundColor = OpiniaDeepBlue,
                textColor = OpinialightBlue,
                iconColor = OpinialightBlue,
                cursorColor = OpinialightBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Confirm Password", style = MaterialTheme.typography.titleMedium, color = OpiniaDeepBlue)

            Spacer(modifier = Modifier.height(12.dp))

            PasswordTextFieldInput(
                confirmPassword,
                onConfirmPasswordChange,
                backgroundColor = OpiniaDeepBlue,
                textColor = OpinialightBlue,
                iconColor = OpinialightBlue,
                cursorColor = OpinialightBlue
            )

            Spacer(modifier = Modifier.height(84.dp))

            CustomButton(
                onClick = onChangeClicked,
                text = "Change",
                containerColor = OpiniaDeepBlue,
                contentColor = OpinialightBlue,
                shape = MaterialTheme.shapes.medium,
                textStyle = MaterialTheme.typography.titleMedium,
                height = 40,
                width = 180
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    changePasswordViewModel: ChangePasswordViewModel
) {

    val uiState by changePasswordViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(key1 = true) {
        changePasswordViewModel.uiEvent.collect { event ->
            when (event) {
                is ChangePasswordUiEvent.PasswordChanged -> {
                    Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate(Destination.CHOOSE_LOGIN_OR_SIGNUP.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
                is ChangePasswordUiEvent.ChangePasswordError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    ChangePasswordContent(
        controller = navController,
        oldPassword = uiState.oldPassword,
        onOldPasswordChange = { changePasswordViewModel.onOldPasswordChange(it) },
        newPassword = uiState.newPassword,
        onNewPasswordChange = { changePasswordViewModel.onNewPasswordChange(it) },
        confirmPassword = uiState.confirmPassword,
        onConfirmPasswordChange = { changePasswordViewModel.onConfirmPasswordChange(it) },
        onChangeClicked = { changePasswordViewModel.onChangeClicked() }
    )
    
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    ChangePasswordContent(
        controller = NavController(LocalContext.current),
        oldPassword = "",
        onOldPasswordChange = {},
        newPassword = "",
        onNewPasswordChange = {},
        confirmPassword = "",
        onConfirmPasswordChange = {},
        onChangeClicked = {}
    )
}