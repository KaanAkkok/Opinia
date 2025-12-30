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
import androidx.compose.runtime.Composable
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
import com.example.opinia.data.model.Avatar
import com.example.opinia.ui.components.AvatarChooseBox
import com.example.opinia.ui.components.CustomButton
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue

@Composable
fun SignupStudentAvatarContent(
    avatars: List<Avatar>,
    selectedAvatarId: String,
    onAvatarSelect: (String) -> Unit,
    onNextClick: () -> Unit
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

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "Choose Your Profile",
            color = OpinialightBlue,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        AvatarChooseBox(
            avatars = avatars,
            selectedAvatarId = selectedAvatarId,
            onAvatarSelect = onAvatarSelect
        )

        Spacer(modifier = Modifier.height(80.dp))

        CustomButton(
            onClick = onNextClick,
            text = "Next",
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

@Composable
fun SignupStudentAvatarScreen(navController: NavController, registerViewModel: RegisterViewModel) {
    val uiState by registerViewModel.uiState.collectAsState()
    val context  = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    SignupStudentAvatarContent(
        avatars = registerViewModel.allAvatars,
        selectedAvatarId = uiState.selectedAvatarId,
        onAvatarSelect = { avatarId ->
            registerViewModel.selectAvatar(avatarId)
        },
        onNextClick = {
            if (uiState.selectedAvatarId.isNotEmpty()) {
                navController.popBackStack()
            }
            else {
                Toast.makeText(context, "Please select an avatar", Toast.LENGTH_LONG).show()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SignupStudentAvatarScreenPreview() {
    val dummyAvatars = listOf(
        Avatar("turuncu", R.drawable.turuncu),
        Avatar("mor", R.drawable.mor),
        Avatar("turkuaz", R.drawable.turkuaz)
    )

    SignupStudentAvatarContent(
        avatars = dummyAvatars,
        selectedAvatarId = "turuncu",
        onAvatarSelect = {},
        onNextClick = {}
    )
}