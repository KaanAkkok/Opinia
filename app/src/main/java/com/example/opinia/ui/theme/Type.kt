package com.example.opinia.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.opinia.R

// Set of Material typography styles to start with
val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_variablefont_wght, FontWeight.Normal),
    Font(R.font.nunito_variablefont_wght, FontWeight.Medium),
    Font(R.font.nunito_variablefont_wght, FontWeight.Bold)
)

val WorkSansFontFamily = FontFamily(
    Font(R.font.worksans_variablefont_wght, FontWeight.Normal),
    Font(R.font.worksans_variablefont_wght, FontWeight.Medium),
    Font(R.font.worksans_variablefont_wght, FontWeight.Bold)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = WorkSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = WorkSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)