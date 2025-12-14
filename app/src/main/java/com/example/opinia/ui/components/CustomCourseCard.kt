package com.example.opinia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.opinia.data.model.Course
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.black

@Composable
fun CustomCourseCard(
    course: Course,
    isActive: Boolean,
    onRowClick: () -> Unit,
    onIconClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    innerPadding: PaddingValues = PaddingValues(10.dp),
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    iconColor: Color = OpiniaDeepBlue,
    iconSize: Dp = 24.dp,
    iconStartPadding: Dp = 0.dp,
    textColor: Color = black,
    codeStyle: SpanStyle = MaterialTheme.typography.titleSmall.toSpanStyle(),
    nameStyle: SpanStyle = MaterialTheme.typography.bodyMedium.toSpanStyle()
) {
    Row(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .clickable { onRowClick() }
            .padding(innerPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Logic
        val iconModifier = Modifier
            .padding(start = iconStartPadding)
            .size(iconSize)
            .then(
                if (onIconClick != null) {
                    Modifier.clickable { onIconClick() }
                } else {
                    Modifier
                }
            )

        Icon(
            imageVector = if (isActive) activeIcon else inactiveIcon,
            contentDescription = null,
            tint = iconColor,
            modifier = iconModifier
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(style = codeStyle) {
                    append(course.courseCode)
                }
                append(" - ")
                withStyle(style = nameStyle) {
                    append(course.courseName)
                }
            },
            color = textColor
        )
    }
}