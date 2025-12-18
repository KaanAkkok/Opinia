package com.example.opinia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.OpinialightBlue

@Composable
fun SelectableAvatarItems(
    resId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderModifier = if (isSelected) {
        Modifier.border(4.dp, OpiniaPurple, CircleShape)
    } else {
        Modifier
    }

    Image(
        painter = painterResource(id = resId),
        contentDescription = "Avatar",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(130.dp)
            .clip(CircleShape)
            .then(borderModifier)
            .clickable { onClick() }
            .background(OpinialightBlue),
        alpha = 1f
    )
}