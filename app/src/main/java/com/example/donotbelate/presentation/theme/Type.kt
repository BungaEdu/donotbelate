package com.example.donotbelate.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.donotbelate.R

val GalanoGrotesque = FontFamily(
    Font(R.font.galano_grotesque) // sin extensión
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = GalanoGrotesque,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = GalanoGrotesque,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    )
)
