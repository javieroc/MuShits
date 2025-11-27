package com.example.mushits.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mushits.R
import com.example.mushits.models.HomeViewModel
import com.example.mushits.models.MusicViewModel
import com.example.mushits.ui.components.InfoBox
import com.example.mushits.ui.components.Player
import com.example.mushits.ui.components.SongList
import com.example.mushits.ui.theme.ColorMode
import com.google.android.gms.location.LocationServices

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mode: ColorMode,
    onToggleMode: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
    musicViewModel: MusicViewModel = viewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val weatherState = viewModel.weather.collectAsState()
    val city by viewModel.cityName.collectAsState()
    val date by viewModel.currentDate.collectAsState()
    val year by viewModel.currentYear.collectAsState()
    val time by viewModel.currentTime.collectAsState()
    val cityImage by viewModel.cityImageUrl.collectAsState()

    val songs by musicViewModel.songs.collectAsState()
    val currentSong by musicViewModel.currentSong.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                viewModel.fetchUserLocation(context, fusedLocationClient)
            }
        }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.fetchUserLocation(context, fusedLocationClient)
        }
    }

    val audioPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                musicViewModel.loadSongs()
            }
        }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            audioPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            musicViewModel.loadSongs()
        }
    }


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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    TopAppBar(
                        title = {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "MuShits",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 20.sp,
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = onToggleMode) {
                                Icon(
                                    imageVector = if (mode == ColorMode.MODE1) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                    contentDescription = "Toggle Mode",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .background(Color.Transparent),
                        scrollBehavior = null,
                    )
                }
            },
            bottomBar = {
                Player(
                    song = currentSong,
                    isPlaying = isPlaying,
                    colorMode = mode,
                    onPlayPause = { musicViewModel.togglePlayPause() },
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                )
            },
            containerColor = Color.Transparent,
            content = { innerPadding ->
                val weather = weatherState.value

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoBox(
                        modifier = Modifier.fillMaxWidth(),
                        date = date,
                        time = time,
                        year = year,
                        city = city,
                        temperature = weather?.current_weather?.temperature?.toString() + "°C",
                        condition = "N/A",
                        humidity = "N/A",
                        imageUrl = cityImage,
                        colorMode = mode
                    )

                    SongList(
                        songs = songs,
                        colorMode = mode,
                        onSongClick = { song ->
                            musicViewModel.playSong(song)
                        },
                        modifier = Modifier.fillMaxWidth()
                            .weight(1f)
                    )
                }
            }
        )
    }
}
