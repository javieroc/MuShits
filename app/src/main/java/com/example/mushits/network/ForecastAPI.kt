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

interface ForecastAPI {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("daily") daily: String = "sunrise,sunset",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
