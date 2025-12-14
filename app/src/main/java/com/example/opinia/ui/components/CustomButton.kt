package com.example.opinia.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue

@Composable
fun CustomButton(
    onClick: () -> Unit,
    isButtonEnabled: Boolean = true,
    containerColor: Color = OpinialightBlue,
    contentColor: Color = OpiniaDeepBlue,
    shape: Shape = MaterialTheme.shapes.medium,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    height: Int = 36,
    width: Int = 270,
    modifier: Modifier = Modifier,
    text: String
) {

    Button(
        onClick = onClick,
        modifier = modifier
            .height(height.dp)
            .width(width.dp),
        enabled = isButtonEnabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(text, style = textStyle)
    }

}