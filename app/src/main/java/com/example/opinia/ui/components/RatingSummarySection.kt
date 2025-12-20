package com.example.opinia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.black
import kotlin.math.roundToInt

@Composable
fun RatingSummarySection(
    averageRating: Float,
    totalCount: Int,
    distribution: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    if(totalCount == 0) {
        return
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format("%.1f", averageRating).replace(".", ","),
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 67.sp,
                color = black
            )

            Spacer(modifier = Modifier.height(4.dp))

            RatingBar(rating = averageRating.roundToInt(), modifier = Modifier.size(12.dp))

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$totalCount Reviews",
                fontFamily = WorkSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = black
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            (1..3).forEach { star ->
                val count = distribution[star] ?: 0
                val progress = if (totalCount > 0) count / totalCount.toFloat() else 0.05f
                val barHeightFraction = progress.coerceAtLeast(0.0f)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .weight(1f, fill = false)
                            .fillMaxHeight(fraction = if(barHeightFraction == 0f) 0.01f else barHeightFraction)
                            .clip(RoundedCornerShape(8.dp))
                            .background(OpiniaPurple.copy(alpha = if(count == 0) 0.2f else 1f))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$star",
                        fontFamily = WorkSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = black
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun RatingSummaryPreview() {
    RatingSummarySection(
        averageRating = 2.4f,
        totalCount = 10,
        distribution = mapOf(1 to 2, 2 to 3, 3 to 5)
    )
}