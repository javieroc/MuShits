package com.example.mushits.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

@Serializable
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current_weather: CurrentWeather,
    val daily: DailyWeather? = null
)

@Serializable
data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Double,
    val weathercode: Int,
    val time: String
)

@Serializable
data class DailyWeather(
    val sunrise: List<String>,
    val sunset: List<String>
)

interface ApiService {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,

        // enable current weather
        @Query("current_weather") currentWeather: Boolean = true,

        // request sunrise + sunset
        @Query("daily") daily: String = "sunrise,sunset",

        // automatically adjust to user's timezone
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
