package com.example.opinia.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {

    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
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