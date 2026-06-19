package com.noemi_balazs.animator.ui.component

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AnimatorButton(
    value: String,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            contentColor = contentColor,
            containerColor = containerColor
        ),
        modifier = modifier.wrapContentWidth()
    ) {
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold
            )
        )
    }
}