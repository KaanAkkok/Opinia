package com.example.opinia.ui.profile

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.opinia.R
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomButton
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpinialightBlue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    onLogoutClicked: () -> Unit,
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
                    .padding(top = 48.dp)
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
                .background(OpiniaGreyWhite),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            CustomButton(
                onClick = { controller.navigate(Destination.STUDENT_SAVED_COURSES.route) },
                text = "Saved Courses"
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                onClick = { controller.navigate(Destination.STUDENT_ADD_COURSES.route) },
                text = "Add Courses"
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                onClick = { controller.navigate(Destination.STUDENT_EDIT_PROFILE.route) },
                text = "Change Profile"
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                onClick = { controller.navigate(Destination.STUDENT_CHANGE_PASSWORD.route) },
                text = "Change Password"
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                onClick = { controller.navigate(Destination.SUPPORT.route) },
                text = "Support"
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = { /* TODO: Handle Turkish language selection */ },
                    modifier = Modifier
                        .height(40.dp)
                        .width(100.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OpiniaDeepBlue,
                        contentColor = OpinialightBlue
                    )
                ) {
                    Text("TR", style = MaterialTheme.typography.titleSmall)
                }

                Spacer(modifier = Modifier.width(27.dp))

                Text("|", style = MaterialTheme.typography.titleLarge.copy(fontSize = 48.sp), color = OpinialightBlue)

                Spacer(modifier = Modifier.width(27.dp))

                Button(
                    onClick = { /* TODO: Handle English language selection */ },
                    modifier = Modifier
                        .height(40.dp)
                        .width(100.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OpiniaDeepBlue,
                        contentColor = OpinialightBlue
                    )
                ) {
                    Text("ENG", style = MaterialTheme.typography.titleSmall)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onLogoutClicked,
                modifier = Modifier
                    .height(40.dp)
                    .width(180.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OpinialightBlue,
                    contentColor = OpiniaDeepBlue
                )
            ) {
                Text("Log out", style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp))
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel) {

    val uiState by profileViewModel.uiState.collectAsState()
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
        profileViewModel.uiEvent.collect { event ->
            when (event) {
                is ProfileUiEvent.LogoutError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is ProfileUiEvent.LogoutSuccess -> {
                    navController.navigate(Destination.CHOOSE_LOGIN_OR_SIGNUP.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    ProfileContent(
        onLogoutClicked = { profileViewModel.onLogoutClicked() },
        navController,
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileContent(
        onLogoutClicked = {},
        controller = NavController(LocalContext.current)
    )
}