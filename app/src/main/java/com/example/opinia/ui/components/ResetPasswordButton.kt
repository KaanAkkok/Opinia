package com.example.opinia.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue

@Composable
fun ResetPasswordButton(onClick: () -> Unit, isButtonEnabled: Boolean = true, modifier: Modifier = Modifier) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .height(40.dp)
            .width(180.dp),
        enabled = isButtonEnabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = OpinialightBlue,
            contentColor = OpiniaDeepBlue
        )
    ) {
        Text("Reset Password", style = MaterialTheme.typography.titleMedium)
    }

}