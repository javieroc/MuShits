package com.example.mushits.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorMatrix

enum class ColorMode {
    MODE1,
    MODE2,
    USSR_MODE
}

val grayscale = ColorMatrix().apply { setToSaturation(0f) }

val greenMono = ColorMatrix(
    floatArrayOf(
        0.3f, 0.3f, 0.3f, 0f, 0f,
        0f,   1f,   0f,  0f, 0f,
        0.3f, 0.3f, 0.3f, 0f, 0f,
        0f,   0f,   0f,  1f, 0f
    )
)

val redMono = ColorMatrix(
    floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0.3f, 0.3f, 0.3f, 0f, 0f,
        0.3f, 0.3f, 0.3f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
)

fun getMatrix(mode: ColorMode): ColorMatrix {
    val matrix = when (mode) {
        ColorMode.MODE1 -> grayscale
        ColorMode.MODE2 -> greenMono
        ColorMode.USSR_MODE -> redMono
    }
    return matrix
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
