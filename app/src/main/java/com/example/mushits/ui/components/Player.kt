package com.example.mushits.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Player(
    song: Song?,
    isPlaying: Boolean,
    position: Long,
    colorMode: ColorMode,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
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
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = song.artUri,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            colorFilter = ColorFilter.colorMatrix(matrix)
        )

        Spacer(Modifier.width(10.dp))

        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = song.title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp
            )
            Text(
                text = song.artist,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 8.sp
            )

            Slider(
                value = position.toFloat(),
                onValueChange = { onSeek(it.toLong()) },
                valueRange = 0f..song.duration.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                ),
                track = { sliderState ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(sliderState.value / song.duration.toFloat())
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(position), fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                Text(formatTime(song.duration), fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerIconButton(
                    onClick = onPrevious,
                    icon = Icons.Filled.SkipPrevious
                )

                PlayerIconButton(
                    onClick = onPlayPause,
                    icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
                )

                PlayerIconButton(
                    onClick = onNext,
                    icon = Icons.Filled.SkipNext
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
