package com.example.supercompras.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.supercompras.R


val kronaFontFamily = FontFamily(
    Font(R.font.krona)
)
val numansFontFamily = FontFamily(
    Font(R.font.numans)
)
// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = kronaFontFamily,
        fontWeight = FontWeight.Normal,
        color = Coral,
        fontSize = 20.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = numansFontFamily,
        fontWeight = FontWeight.Normal,
        color = Marinho,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = numansFontFamily,
        fontWeight = FontWeight.Normal,
        color = Marinho,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = numansFontFamily,
        fontWeight = FontWeight.Normal,
        color = Marinho,
        fontSize = 12.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    )
)