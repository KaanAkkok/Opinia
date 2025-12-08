package com.example.opinia.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextFieldInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {

    // Şifre görünürlük durumu sadece bu bileşeni ilgilendirir
    var showPassword by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (showPassword) "Hide password" else "Show password"

            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(imageVector = icon, contentDescription = description, tint = black)
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = OpinialightBlue,
            unfocusedContainerColor = OpinialightBlue,
            focusedTextColor = black,
            unfocusedTextColor = black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        modifier = modifier
            .width(280.dp)
            .height(50.dp)
    )

}