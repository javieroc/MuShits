package com.example.mushits

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.mushits.ui.screens.HomeScreen
import com.example.mushits.ui.theme.ColorMode
import com.example.mushits.ui.theme.MuShitsTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var mode by remember { mutableStateOf(ColorMode.MODE1) }
            MuShitsTheme(mode = mode) {
                HomeScreen(
                    mode = mode,
                    onToggleMode = {
                        mode = if (mode == ColorMode.MODE1)
                            ColorMode.MODE2
                        else ColorMode.MODE1
                    }
                )
            }
        }
    }
}
