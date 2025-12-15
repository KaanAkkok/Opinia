package com.example.opinia.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomButton
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpinialightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportContent(
    controller: NavController,
    onContactClicked: () -> Unit = {},
    OnBackClicked: () -> Unit = {}
) {

    Scaffold (
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
                .background(OpiniaGreyWhite),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
                    .background(OpiniaGreyWhite),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Support", style = MaterialTheme.typography.titleMedium, color = OpiniaDeepBlue)

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .width(270.dp)
                        .height(30.dp)
                        .background(OpiniaDeepBlue, MaterialTheme.shapes.extraLarge),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("support@opinia.com", style = MaterialTheme.typography.titleSmall, color = OpinialightBlue)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
                    .background(OpiniaGreyWhite),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomButton(
                    onClick = onContactClicked,
                    text = "Contact Us",
                    textStyle = MaterialTheme.typography.titleMedium,
                    shape = MaterialTheme.shapes.medium,
                    containerColor = OpiniaDeepBlue,
                    contentColor = OpinialightBlue,
                    modifier = Modifier
                        .height(40.dp)
                        .width(180.dp)
                )

                Spacer(Modifier.height(12.dp))

                CustomButton(
                    onClick = OnBackClicked,
                    text = "Back",
                    textStyle = MaterialTheme.typography.titleMedium,
                    shape = MaterialTheme.shapes.medium,
                    containerColor = OpinialightBlue,
                    contentColor = OpiniaDeepBlue,
                    modifier = Modifier
                        .height(40.dp)
                        .width(180.dp)
                )
            }

        }

    }

}

@Composable
fun SupportScreen(navController: NavController, supportViewModel: SupportViewModel) {

    val _uiState by supportViewModel.uiState.collectAsState()
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
        supportViewModel.uiEvent.collect { event ->
            when(event) {
                is SupportUiEvent.OpenEmailClient -> {
                    val uriText = "mailto:${event.email}?subject=${Uri.encode(event.subject)}&body=${Uri.encode(event.body)}"
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse(uriText)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Mail app not found", Toast.LENGTH_SHORT).show()
                    }
                }
                is SupportUiEvent.SupportSuccess -> {
                    navController.popBackStack()
                }
                is SupportUiEvent.SupportError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    SupportContent(
        controller = navController,
        onContactClicked = { supportViewModel.onSupportClicked() },
        OnBackClicked = { navController.popBackStack() }
    )
}

@Preview(showBackground = true)
@Composable
fun SupportScreenPreview() {
    SupportContent(
        controller = NavController(LocalContext.current),
        onContactClicked = {},
        OnBackClicked = {}
    )
}