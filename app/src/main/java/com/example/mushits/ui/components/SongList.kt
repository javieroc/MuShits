package com.example.mushits.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.example.mushits.ui.theme.getMatrix

@Composable
fun SongList(
    songs: List<Song>,
    colorMode: ColorMode,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {

    val matrix = getMatrix(colorMode)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary)
            .background(Color.Black.copy(alpha = 0.35f))
    ) {

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "My Awesome Music",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp
                )
            }

            if (songs.isEmpty()) {
                Column(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        "Loading songs…",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    LoadingDots()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(songs) { song ->
                        SongRow(song = song, matrix = matrix, onClick = { onSongClick(song) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SongRow(
    song: Song,
    matrix: ColorMatrix,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.2f))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (song.artUri != null) {
            AsyncImage(
                model = song.artUri,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                colorFilter = ColorFilter.colorMatrix(matrix)
            )
        } else {
            Box(
                Modifier
                    .size(50.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                ShimmerBox(Modifier.fillMaxSize())
            }
        }

        Spacer(Modifier.width(10.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
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
        }

        Text(
            text = song.durationReadable,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 8.sp,
            modifier = Modifier.padding(end = 6.dp)
        )
    }
}
