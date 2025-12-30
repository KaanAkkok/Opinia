package com.example.opinia.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextFieldInput(
    value: String,
    onValueChange: (String) -> Unit,
    backgroundColor: Color = OpinialightBlue,
    textColor: Color = black,
    iconColor: Color = black,
    cursorColor: Color = black,
    modifier: Modifier = Modifier
) {
    var showPassword by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    val rules = listOf(
        "At least 8 character" to (value.length >= 8),
        "At least one number" to value.contains(Regex("[0-9]")),
        "Uppercase and Lowercase character" to (value.contains(Regex("[A-Z]")) && value.contains(Regex("[a-z]"))),
        "No spaces" to (!value.contains(" ") && value.isNotEmpty())
    )

    Column {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            singleLine = true,
            cursorBrush = SolidColor(cursorColor),
            textStyle = TextStyle(color = textColor, fontSize = 14.sp),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            decorationBox = { innerTextField ->
                Row(
                    modifier = modifier
                        .width(270.dp)
                        .height(30.dp)
                        .background(
                            color = backgroundColor,
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .padding(start = 16.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                    }
                    val icon = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(
                        onClick = { showPassword = !showPassword },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Toggle password visibility",
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        )

        AnimatedVisibility(
            visible = value.isNotEmpty() && isFocused,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(270.dp),
                colors = CardDefaults.cardColors(containerColor = OpinialightBlue),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Password Rules:",
                        color = Color(0xFF1E2223),
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    rules.forEach { (label, isValid) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (isValid) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                contentDescription = null,
                                tint = if (isValid) Color.Green.copy(0.6f) else Color.Red.copy(0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                color = if (isValid) Color(0xFF1E2223) else Color(0xFF1E2223).copy(0.8f),
                                fontFamily = WorkSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 10.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}