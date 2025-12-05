package com.example.mushits.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.mushits.ui.theme.ColorMode

@Composable
fun InfoBox(
    date: String,
    time: String,
    year: String,
    city: String,
    temperature: String,
    condition: String,
    humidity: String,
    imageUrl: String?,
    colorMode: ColorMode,
    modifier: Modifier = Modifier
) {
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

    val matrix = when (colorMode) {
        ColorMode.MODE1 -> grayscale
        ColorMode.MODE2 -> greenMono
        ColorMode.USSR_MODE -> redMono
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary)
            .background(Color.Black.copy(alpha = 0.4f))
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = date, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                Text(text = year, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Location Image",
                        modifier = Modifier
                            .size(100.dp),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.colorMatrix(matrix)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .border(2.dp, MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        ShimmerBox(modifier = Modifier.fillMaxSize())
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (city == "Loading…" || temperature.contains("null")) {
                        LoadingDots()
                    } else {
                        Text("LOC – $city", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
                        Text("TMP – $temperature", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
                        Text("CON – $condition", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
                        Text("HUM – $humidity", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
