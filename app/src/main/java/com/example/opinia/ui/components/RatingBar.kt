package com.example.opinia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun RatingBar(rating: Int, maxRating: Int = 3, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(maxRating) { index ->
            val isSelected = index < rating
            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Default.StarBorder,
                contentDescription = "$index Star",
                tint = Color(0xFFF9A75D), //yellow,
                modifier = modifier
            )
        }
    }
}