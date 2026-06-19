package com.noemi_balazs.animator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.noemi_balazs.animator.resources.Res
import com.noemi_balazs.animator.resources.philosopher_bold
import com.noemi_balazs.animator.resources.philosopher_italic
import com.noemi_balazs.animator.resources.philosopher_regular
import org.jetbrains.compose.resources.Font

@Composable
fun philosopherFontFamily() = FontFamily(
    Font(Res.font.philosopher_regular, weight = FontWeight.Normal),
    Font(Res.font.philosopher_italic, weight = FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.philosopher_bold, weight = FontWeight.Bold)
)

@Composable
fun philosopherTypography() = Typography().run {

    val fontFamily = philosopherFontFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}