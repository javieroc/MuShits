package com.connan.mushits.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.connan.mushits.models.HomeViewModel
import com.connan.mushits.models.MusicViewModel
import com.connan.mushits.models.SoundViewModel
import com.connan.mushits.ui.components.InfoBox
import com.connan.mushits.ui.components.Player
import com.connan.mushits.ui.components.SongList
import com.connan.mushits.ui.components.SoundBoard
import com.connan.mushits.ui.components.getWeatherDescription
import com.connan.mushits.ui.theme.ColorMode
import com.connan.mushits.ui.theme.MuShitsTheme
import com.connan.mushits.R
import com.google.android.gms.location.LocationServices

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mode: ColorMode,
    onToggleMode: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
    musicViewModel: MusicViewModel = viewModel(),
    soundViewModel: SoundViewModel = viewModel()
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
    val position by musicViewModel.position.collectAsState()

    val isUssrMode by musicViewModel.isUssrPlaying.collectAsState()

    val effectiveMode =
        if (isUssrMode) ColorMode.USSR_MODE
        else mode

    val pagerState = rememberPagerState(pageCount = { 2 })

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            viewModel.fetchUserLocation(context, fusedLocationClient)
        }
        if (permissions[Manifest.permission.READ_MEDIA_AUDIO] == true) {
            musicViewModel.loadSongs()
        }
    }

    LaunchedEffect(Unit) {
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        )
        musicViewModel.connectToService()
    }

    MuShitsTheme(mode = effectiveMode) {
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
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 4.dp,
                                    shadowElevation = 4.dp,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .clickable { musicViewModel.toggleUssrSong(context) }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ussr_logo),
                                        contentDescription = "Toggle USSR Song",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }

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
                    if (currentSong != null) {
                        Player(
                            song = currentSong,
                            isPlaying = isPlaying,
                            position = position,
                            colorMode = effectiveMode,
                            onPlayPause = musicViewModel::togglePlayPause,
                            onSeek = musicViewModel::seekTo,
                            onNext = musicViewModel::playNext,
                            onPrevious = musicViewModel::playPrevious,
                            modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                        )
                    }
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
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth()
                        ) { page ->
                            if (page == 0) {
                                InfoBox(
                                    modifier = Modifier.fillMaxWidth(),
                                    date = date,
                                    time = time,
                                    year = year,
                                    city = city,
                                    temperature = weather?.current?.temperature_2m?.toString() + "°C",
                                    condition = weather?.current?.weather_code?.let {
                                        getWeatherDescription(
                                            it
                                        )
                                    } ?: "N/A",
                                    humidity = weather?.current?.relative_humidity_2m?.toString() + "%",
                                    imageUrl = cityImage,
                                    colorMode = effectiveMode
                                )
                            } else {
                                SoundBoard(
                                    modifier = Modifier.fillMaxWidth(),
                                    onSoundClick = { index ->
                                        soundViewModel.playSound(index)
                                    }
                                )
                            }
                        }

                        SongList(
                            songs = songs,
                            colorMode = effectiveMode,
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
}
