package com.example.mushits.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mushits.ui.theme.ColorMode

@Composable
fun SoundBoard(
    onSoundClick: (Int) -> Unit,
    colorMode: ColorMode,
    modifier: Modifier = Modifier
) {
    val soundNames = listOf(
        "FART 1", "FART 2", "CAT", "ALIENS",
        "ALARM", "APPLAUSE", "DEMON 1", "DEMON 2",
        "GHOST", "KNOCK", "LAUGH", "SHOTGUN"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp) // Approximate height to match InfoBox
            .border(1.dp, MaterialTheme.colorScheme.primary)
            .background(Color.Black.copy(alpha = 0.4f))
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "SOUND BOARD",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(soundNames.size) { index ->
                    SoundButton(
                        name = soundNames[index],
                        onClick = { onSoundClick(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun SoundButton(
    name: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary)
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}
