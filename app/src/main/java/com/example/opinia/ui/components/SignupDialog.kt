package com.example.opinia.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.black
import com.example.opinia.ui.theme.gray

@Composable
fun SignupDialog(
    email: String,
    isResendEnabled: Boolean,
    cooldown: Int,
    onDismissRequest: () -> Unit,
    onWrongEmailClick: () -> Unit,
    onResendClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /*do nothing*/ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        containerColor = OpiniaPurple,
        title = {
            Text(
                text = "Email Verification",
                color = black,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            Column {
                Text(
                    text = buildAnnotatedString {
                        append("We sent a verification email to ")
                        withStyle(
                            style = SpanStyle(
                                color = OpiniaDeepBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = WorkSansFontFamily
                            )
                        ) {
                            append(email)
                        }
                        append(". Please verify your email and check spam folder")
                    },
                    color = black,
                    fontFamily = WorkSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Once you confirm, this window will close automatically and your registration will be complete",
                    color = black,
                    fontFamily = WorkSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onResendClick,
                enabled = isResendEnabled
            ) {
                val buttonText = if (isResendEnabled) "Resend Email" else "Wait ${cooldown}s"
                Text(
                    text = buttonText,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (isResendEnabled) black else gray
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onWrongEmailClick
            ) {
                Text(
                    text = "Change Email",
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = black
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SignupDialogPreview() {
    SignupDialog(
        email = "example@example.com",
        onDismissRequest = {},
        isResendEnabled = true,
        cooldown = 60,
        onWrongEmailClick = {},
        onResendClick = {}
    )
}