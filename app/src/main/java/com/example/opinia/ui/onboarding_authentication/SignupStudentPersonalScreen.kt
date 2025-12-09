package com.example.opinia.ui.onboarding_authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.data.model.Avatar
import com.example.opinia.ui.Destination
import com.example.opinia.ui.components.NextButton
import com.example.opinia.ui.components.PasswordTextFieldInput
import com.example.opinia.ui.components.TextFieldInput
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignupStudentPersonalContent(
    selectedAvatarId: String,
    allAvatars: List<Avatar>,
    onAvatarClick: () -> Unit,
    nameValue: String,
    onNameChange: (String) -> Unit,
    surnameValue: String,
    onSurnameChange: (String) -> Unit,
    emailValue: String,
    onEmailChange: (String) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    passwordDummyValue: String,
    onPasswordDummyChange: (String) -> Unit,
    onNextClick: () -> Unit,
) {

    val selectedAvatarRes = allAvatars.find { it.key == selectedAvatarId }?.resId

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

        Spacer(modifier = Modifier.height(32.dp))

        if (selectedAvatarRes != null) {
            Image(
                painter = painterResource(id = selectedAvatarRes),
                contentDescription = "Selected Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .clickable { onAvatarClick() }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(OpinialightBlue)
                    .clickable { onAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.account_circle),
                    contentDescription = "Avatar Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Profile", style = MaterialTheme.typography.titleSmall, color = OpinialightBlue)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Student Name", style = MaterialTheme.typography.titleSmall, color = OpinialightBlue)

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldInput(value = nameValue, onValueChange = onNameChange)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Student Surname", style = MaterialTheme.typography.titleSmall, color = OpinialightBlue)

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldInput(value = surnameValue, onValueChange = onSurnameChange)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Student Email", style = MaterialTheme.typography.titleSmall, color = OpinialightBlue)

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldInput(value = emailValue, onValueChange = onEmailChange)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Password", style = MaterialTheme.typography.titleSmall, color = OpinialightBlue)

        Spacer(modifier = Modifier.height(12.dp))

        PasswordTextFieldInput(value = passwordValue, onValueChange = onPasswordChange)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Confirm Password", style = MaterialTheme.typography.titleSmall, color = OpinialightBlue)

        Spacer(modifier = Modifier.height(12.dp))

        PasswordTextFieldInput(value = passwordDummyValue, onValueChange = onPasswordDummyChange)

        Spacer(modifier = Modifier.height(64.dp))

        NextButton(onClick = onNextClick)
    }
}

@Composable
fun SignupStudentPersonalScreen(navController: NavController, registerViewModel: RegisterViewModel) {

    val uiState by registerViewModel.uiState.collectAsState()
    val context  = LocalContext.current

    LaunchedEffect(key1 = true) {
        registerViewModel.uiEvent.collectLatest { event ->
            when(event) {
                is RegisterUiEvent.SignupError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }

    SignupStudentPersonalContent(
        selectedAvatarId = uiState.selectedAvatarId,
        allAvatars = registerViewModel.allAvatars,
        onAvatarClick = {
            navController.navigate(Destination.CHOOSE_AVATAR.route)
        },
        nameValue = uiState.name,
        onNameChange = { registerViewModel.onNameChange(it) },
        surnameValue = uiState.surname,
        onSurnameChange = { registerViewModel.onSurnameChange(it) },
        emailValue = uiState.email,
        onEmailChange = { registerViewModel.onEmailChange(it) },
        passwordValue = uiState.password,
        onPasswordChange = { registerViewModel.onPasswordChange(it) },
        passwordDummyValue = uiState.passwordDummy,
        onPasswordDummyChange = { registerViewModel.onPasswordDummyChange(it) },
        onNextClick = {
            if (registerViewModel.validateStep1()) {
                navController.navigate(Destination.SIGNUP_ACADEMIC_INFO.route)
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun SignupStudentPersonalScreenPreview() {

    val dummyAvatars = listOf(
        Avatar("turuncu", R.drawable.turuncu),
        Avatar("mor", R.drawable.mor),
        Avatar("turkuaz", R.drawable.turkuaz)
    )

    SignupStudentPersonalContent(
        selectedAvatarId = "",
        allAvatars = dummyAvatars,
        onAvatarClick = {},
        nameValue = "Kaan",
        onNameChange = {},
        surnameValue = "Akkök",
        onSurnameChange = {},
        emailValue = "ogrenci@ornek.edu.tr",
        onEmailChange = {},
        passwordValue = "123456",
        onPasswordChange = {},
        passwordDummyValue = "123456",
        onPasswordDummyChange = {},
        onNextClick = {}
    )

}