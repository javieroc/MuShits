package com.connan.mushits.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.connan.mushits.data.Song
import com.connan.mushits.ui.theme.ColorMode
import com.connan.mushits.ui.theme.getMatrix

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
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
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
        AlbumArt(artUri = song.artUri, matrix = matrix)

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

@Composable
private fun AlbumArt(
    artUri: String?,
    matrix: ColorMatrix,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val fallback = @Composable {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(30.dp)
            )
        }
    }

    Box(modifier = modifier.size(50.dp)) {
        SubcomposeAsyncImage(
            model = artUri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        ) {
            val state by painter.state.collectAsState()
            when (state) {
                is AsyncImagePainter.State.Loading -> {
                    ShimmerBox(Modifier.fillMaxSize())
                }
                is AsyncImagePainter.State.Success -> {
                    SubcomposeAsyncImageContent(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.colorMatrix(matrix)
                    )
                }
                else -> fallback()
            }
        }
    }
}
