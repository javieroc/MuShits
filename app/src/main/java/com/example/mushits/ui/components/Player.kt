package com.example.mushits.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.mushits.data.Song
import com.example.mushits.ui.theme.ColorMode

@Composable
fun Player(
    song: Song?,
    isPlaying: Boolean,
    colorMode: ColorMode,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (song == null) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.35f))
                .border(1.dp, MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "No song playing",
                color = MaterialTheme.colorScheme.primary
            )
        }
        return
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

    val matrix = when (colorMode) {
        ColorMode.MODE1 -> grayscale
        ColorMode.MODE2 -> greenMono
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.35f))
            .border(1.dp, MaterialTheme.colorScheme.primary)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Artwork
        AsyncImage(
            model = song.artUri,
            contentDescription = null,
            modifier = Modifier.size(55.dp),
            colorFilter = ColorFilter.colorMatrix(matrix)
        )

        Spacer(Modifier.width(10.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )
            Text(
                text = song.artist,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }

        IconButton(onClick = onPlayPause) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
