package com.example.mushits.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp

@Composable
fun LoadingDots() {
    val dots = listOf("", ".", "..", "...")
    val index = remember { androidx.compose.runtime.mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(400)
            index.value = (index.value + 1) % dots.size
        }
    }

    Text(
        text = "Loading${dots[index.value]}",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 10.sp
    )
}
