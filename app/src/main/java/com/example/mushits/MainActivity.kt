package com.example.mushits

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.mushits.ui.screens.HomeScreen
import com.example.mushits.ui.theme.ColorMode
import com.example.mushits.ui.theme.MuShitsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = this
            val storedMode by getColorMode(context).collectAsState(initial = ColorMode.MODE1)
            var mode by remember(storedMode) { mutableStateOf(storedMode) }

            val scope = rememberCoroutineScope()
            MuShitsTheme(mode = mode) {
                HomeScreen(
                    mode = mode,
                    onToggleMode = {
                        mode = if (mode == ColorMode.MODE1)
                            ColorMode.MODE2
                        else ColorMode.MODE1

                        scope.launch {
                            saveColorMode(context, mode)
                        }
                    }
                )
            }
        }
    }
}
