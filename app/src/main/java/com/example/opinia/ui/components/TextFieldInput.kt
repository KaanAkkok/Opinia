package com.example.opinia.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.opinia.ui.theme.OpinialightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {

    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        interactionSource = interactionSource,
        textStyle = TextStyle(color = Color.Black),
        modifier = Modifier
            .height(30.dp)
            .width(270.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = OpinialightBlue,
                        unfocusedContainerColor = OpinialightBlue,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                )
        }
    )


}