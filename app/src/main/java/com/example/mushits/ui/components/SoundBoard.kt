package com.example.mushits.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

@Composable
fun SoundBoard(
    onSoundClick: (Int) -> Unit,
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
            .height(187.dp) // Fixed minimal common height
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

            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 0 until 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (j in 0 until 4) {
                            val index = i * 4 + j
                            Box(modifier = Modifier.weight(1f)) {
                                SoundButton(
                                    name = soundNames[index],
                                    onClick = { onSoundClick(index) }
                                )
                            }
                        }
                    }
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
            .fillMaxWidth()
            .height(45.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary)
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            lineHeight = 10.sp
        )
    }
}
