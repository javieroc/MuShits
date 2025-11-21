package com.example.mushits.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mushits.R
import com.example.mushits.models.HomeViewModel
import com.example.mushits.ui.theme.ColorMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mode: ColorMode,
    onToggleMode: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val weatherState = viewModel.weather.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg1),
            contentDescription = "Earth Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Scaffold(
            modifier = modifier.fillMaxSize().padding(4.dp),
            topBar = {
                TopAppBar(
                    title = {
                        Text("MuShits", color = MaterialTheme.colorScheme.primary)
                    },
                    actions = {
                        TextButton(onClick = onToggleMode) {
                            Text(
                                text = if (mode == ColorMode.MODE1) "Mode 2" else "Mode 1",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .background(Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
                        ),
                    scrollBehavior = null
                )
            },
            containerColor = Color.Transparent,
            content = { innerPadding ->
                val weather = weatherState.value

                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    if (weather == null) {
                        Text(
                            text = "Loading weather...",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = """
                            Temperature: ${weather.current_weather.temperature}°C
                            Sunrise: ${weather.daily?.sunrise?.firstOrNull() ?: "N/A"}
                            Sunset: ${weather.daily?.sunset?.firstOrNull() ?: "N/A"}
                        """.trimIndent(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        )
    }
}
