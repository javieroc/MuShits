package com.example.mushits.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

enum class ColorMode {
    MODE1,
    MODE2,
    USSR_MODE
}

@Composable
fun MuShitsTheme(
    mode: ColorMode = ColorMode.MODE1,
    content: @Composable () -> Unit
) {
    val colorScheme = when (mode) {
        ColorMode.MODE1 -> MonochromeMode1
        ColorMode.MODE2 -> MonochromeMode2
        ColorMode.USSR_MODE -> UssrRedMode
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
