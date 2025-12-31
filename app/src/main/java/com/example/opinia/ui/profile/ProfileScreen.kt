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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.opinia.ui.components.AreYouSureDialog
import com.example.opinia.ui.components.CustomButton
import com.example.opinia.ui.components.DeleteAccountDialog
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpinialightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    onLogoutClicked: () -> Unit,
    onSavedCoursesClicked: () -> Unit,
    onAddCoursesClicked: () -> Unit,
    onChangeProfileClicked: () -> Unit,
    onChangeAcademicInfoClicked: () -> Unit,
    onChangePasswordClicked: () -> Unit,
    onSupportClicked: () -> Unit,
    onDeleteClicked: (String) -> Unit,
    controller: NavController
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.yeni_koyu_amblem),
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
        val scrollState = rememberScrollState()
        var showDeleteDialog by remember { mutableStateOf(false) }
        var showAreYouSureDialog by remember { mutableStateOf(false) }
        var storedPassword by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .fillMaxSize()
                .background(OpiniaGreyWhite)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            CustomButton(
                onClick = { onSavedCoursesClicked() },
                text = "Saved Courses",
                shape = MaterialTheme.shapes.extraLarge,
                textStyle = MaterialTheme.typography.titleSmall,
                containerColor = OpiniaDeepBlue,
                contentColor = OpinialightBlue,
                modifier = Modifier
                    .height(36.dp)
                    .width(270.dp),
                fontSize = 15
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                onClick = { onAddCoursesClicked() },
                text = "Add Courses",
                shape = MaterialTheme.shapes.extraLarge,
                textStyle = MaterialTheme.typography.titleSmall,
                containerColor = OpiniaDeepBlue,
                contentColor = OpinialightBlue,
                modifier = Modifier
                    .height(36.dp)
                    .width(270.dp),
                fontSize = 15
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                onClick = { onChangeProfileClicked() },
                text = "Change Profile Photo",
                shape = MaterialTheme.shapes.extraLarge,
                textStyle = MaterialTheme.typography.titleSmall,
                containerColor = OpiniaDeepBlue,
                contentColor = OpinialightBlue,
                modifier = Modifier
                    .height(36.dp)
                    .width(270.dp),
                fontSize = 15
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                onClick = { onChangeAcademicInfoClicked() },
                text = "Change Academic Info",
                shape = MaterialTheme.shapes.extraLarge,
                textStyle = MaterialTheme.typography.titleSmall,
                containerColor = OpiniaDeepBlue,
                contentColor = OpinialightBlue,
                modifier = Modifier
                    .height(36.dp)
                    .width(270.dp),
                fontSize = 15
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                onClick = { onChangePasswordClicked() },
                text = "Change Password",
                shape = MaterialTheme.shapes.extraLarge,
                textStyle = MaterialTheme.typography.titleSmall,
                containerColor = OpiniaDeepBlue,
                contentColor = OpinialightBlue,
                modifier = Modifier
                    .height(36.dp)
                    .width(270.dp),
                fontSize = 15
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                onClick = { onSupportClicked() },
                text = "Support",
                shape = MaterialTheme.shapes.extraLarge,
                textStyle = MaterialTheme.typography.titleSmall,
                containerColor = OpiniaDeepBlue,
                contentColor = OpinialightBlue,
                modifier = Modifier
                    .height(36.dp)
                    .width(270.dp),
                fontSize = 15
            )

            Spacer(modifier = Modifier.height(42.dp))

            CustomButton(
                onClick = onLogoutClicked,
                text = "Log out",
                shape = MaterialTheme.shapes.medium,
                textStyle = MaterialTheme.typography.titleMedium,
                containerColor = OpinialightBlue,
                contentColor = OpiniaDeepBlue,
                modifier = Modifier
                    .height(42.dp)
                    .width(180.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomButton(
                onClick = { showDeleteDialog = true },
                text = "Delete Account",
                shape = MaterialTheme.shapes.medium,
                textStyle = MaterialTheme.typography.titleMedium,
                containerColor = Color.Red.copy(alpha = 0.2f),
                contentColor = OpiniaDeepBlue,
                modifier = Modifier
                    .height(42.dp)
                    .width(180.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (showDeleteDialog) {
                DeleteAccountDialog(
                    onDismiss = { showDeleteDialog = false },
                    onConfirm = { password ->
                        storedPassword = password
                        showDeleteDialog = false
                        showAreYouSureDialog = true
                    }
                )
            }

            if (showAreYouSureDialog) {
                AreYouSureDialog(
                    onDismiss = { showAreYouSureDialog = false },
                    onConfirm = {
                        showAreYouSureDialog = false
                        onDeleteClicked(storedPassword)
                    },
                    infoText = "account"
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel) {

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
                is ProfileUiEvent.LogoutSuccess -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    navController.navigate(Destination.CHOOSE_LOGIN_OR_SIGNUP.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
                is ProfileUiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is ProfileUiEvent.DeleteSuccess -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
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
        onSavedCoursesClicked = { navController.navigate(Destination.STUDENT_SAVED_COURSES.route) },
        onAddCoursesClicked = { navController.navigate(Destination.STUDENT_ADD_COURSE1.route) },
        onChangeProfileClicked = { navController.navigate(Destination.STUDENT_CHANGE_AVATAR.route) },
        onChangeAcademicInfoClicked = { navController.navigate(Destination.STUDENT_CHANGE_STUDENT_INFO.route) },
        onChangePasswordClicked = { navController.navigate(Destination.STUDENT_CHANGE_PASSWORD.route) },
        onSupportClicked = { navController.navigate(Destination.SUPPORT.route) },
        onDeleteClicked = { password ->
            profileViewModel.onDeleteClicked(password)
        },
        navController,
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileContent(
        onLogoutClicked = {},
        onSavedCoursesClicked = {},
        onAddCoursesClicked = {},
        onChangeProfileClicked = {},
        onChangeAcademicInfoClicked = {},
        onChangePasswordClicked = {},
        onSupportClicked = {},
        onDeleteClicked = {},
        controller = NavController(LocalContext.current)
    )
}