package com.example.echonote.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.echonote.R

val ArchitectsFamily = FontFamily(
    listOf(
        Font(R.font.architects_daughter_regular,FontWeight.Normal)
    )
)
val StylishFamily = FontFamily(
    listOf(
        Font(R.font.stylish_regular,FontWeight.Normal)
    )
)


val PoppinsFamily = FontFamily(
    listOf(
        Font(R.font.poppins_bold, FontWeight.Bold),
        Font(R.font.poppins_regular,FontWeight.Normal),
        Font(R.font.poppins_extrabold,FontWeight.ExtraBold),
        Font(R.font.poppins_semibold,FontWeight.SemiBold),
        Font(R.font.poppins_thin,FontWeight.Thin)

    )
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)