package com.example.mushits.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mushits.network.RetrofitInstance
import com.example.mushits.network.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    init {
        fetchWeather()
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            try {
                val api = RetrofitInstance.api

                val response = api.getWeather(
                    latitude = 40.7128,
                    longitude = -74.0060
                )

                _weather.value = response

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
