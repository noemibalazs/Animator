package com.noemi_balazs.animator.util.ext

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

val TextStyle.light: TextStyle
    get() = copy(fontWeight = FontWeight.Light)

val TextStyle.bold: TextStyle
    get() = copy(fontWeight = FontWeight.Bold)

val TextStyle.semibold: TextStyle
    get() = copy(fontWeight = FontWeight.SemiBold)

val TextStyle.extrabold: TextStyle
    get() = copy(fontWeight = FontWeight.ExtraBold)