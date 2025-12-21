package com.example.opinia.ui.profile

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.opinia.data.model.Avatar
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.AvatarChooseBox
import com.example.opinia.ui.components.CustomButton
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpinialightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeAvatarContent(
    avatars: List<Avatar>,
    selectedAvatarId: String,
    isLoading: Boolean,
    onAvatarSelect: (String) -> Unit,
    onOkClicked: () -> Unit,
    controller: NavController
) {
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
                .padding(horizontal = 12.dp)
                .fillMaxSize()
                .background(OpiniaGreyWhite),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Change Your Profile", style = MaterialTheme.typography.titleMedium, color = OpiniaDeepBlue)

            Spacer(modifier = Modifier.height(48.dp))

            AvatarChooseBox(
                avatars = avatars,
                selectedAvatarId = selectedAvatarId,
                onAvatarSelect = onAvatarSelect
            )

            Spacer(modifier = Modifier.height(48.dp))

            CustomButton(
                onClick = onOkClicked,
                isButtonEnabled = !isLoading,
                text = "OK",
                shape = MaterialTheme.shapes.extraLarge,
                textStyle = MaterialTheme.typography.titleMedium,
                containerColor = OpiniaDeepBlue,
                contentColor = OpinialightBlue,
                modifier = Modifier
                    .height(42.dp)
                    .width(180.dp)
            )
        }
    }
}

@Composable
fun ChangeAvatarScreen(navController: NavController, changeAvatarViewModel: ChangeAvatarViewModel) {

    val uiState by changeAvatarViewModel.uiState.collectAsState()
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
        changeAvatarViewModel.uiEvent.collect { event ->
            when (event) {
                is ChangeAvatarUiEvent.AvatarChangeError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is ChangeAvatarUiEvent.AvatarChangeSuccess -> {
                    Toast.makeText(context, "Avatar updated successfully", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }
        }
    }

    ChangeAvatarContent(
        avatars = changeAvatarViewModel.allAvatars,
        selectedAvatarId = uiState.selectedAvatarId,
        isLoading = uiState.isLoading,
        onAvatarSelect = { avatarId ->
            changeAvatarViewModel.selectAvatar(avatarId)
        },
        onOkClicked = {
            changeAvatarViewModel.onOKclicked()
        },
        controller = navController
    )

}

@Preview(showBackground = true)
@Composable
fun ChangeAvatarScreenPreview() {

    val dummyAvatars = listOf(
        Avatar("turuncu", R.drawable.turuncu),
        Avatar("mor", R.drawable.mor),
        Avatar("turkuaz", R.drawable.turkuaz)
    )

    ChangeAvatarContent(
        avatars = dummyAvatars,
        selectedAvatarId = "turuncu",
        isLoading = false,
        onAvatarSelect = {},
        onOkClicked = {},
        controller = NavController(LocalContext.current)
    )
}