package com.example.mushits.models

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mushits.network.ForecastApiInstance
import com.example.mushits.network.WeatherResponse
import com.example.mushits.BuildConfig
import com.example.mushits.network.UnsplashInstance
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    private val _cityName = MutableStateFlow("Loading…")
    val cityName: StateFlow<String> = _cityName

    val currentDate: StateFlow<String> = MutableStateFlow(
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM"))
    )

    val currentYear: StateFlow<String> = MutableStateFlow(
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"))
    )

    val currentTime: StateFlow<String> = MutableStateFlow(
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    )

    private val _cityImageUrl = MutableStateFlow<String?>(null)
    val cityImageUrl: StateFlow<String?> = _cityImageUrl


    fun fetchUserLocation(
        context: Context,
        fusedLocationClient: FusedLocationProviderClient
    ) {
        val permission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Location permission is not granted.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude

                    fetchWeather(lat, lon)
                    fetchCityName(context, lat, lon)
                } else {
                    Toast.makeText(context, "Unable to get location.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Permission error occurred.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchCityName(context: Context, lat: Double, lon: Double) {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            geocoder.getFromLocation(lat, lon, 1) { result ->
                printGeocoderResult(result)

                if (result.isNotEmpty()) {
                    val a = result[0]
                    val county = a.adminArea
                    val country = a.countryName

                    _cityName.value = listOfNotNull(county, country).joinToString(", ")
                    Log.d("HomeViewModel", "🟦 Final city name used: ${_cityName.value}")
                    fetchCityImage(_cityName.value)
                } else {
                    _cityName.value = "Unknown"
                }
            }
            return
        }

        try {
            val result = geocoder.getFromLocation(lat, lon, 1)
            printGeocoderResult(result)

            if (!result.isNullOrEmpty()) {
                val a = result[0]
                val county = a.adminArea
                val country = a.countryName

                _cityName.value = listOfNotNull(county, country).joinToString(", ")
                Log.d("HomeViewModel", "🟦 Final city name used: ${_cityName.value}")
                fetchCityImage(_cityName.value)
            } else {
                _cityName.value = "Unknown"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _cityName.value = "Unknown"
        }
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = ForecastApiInstance.api.getWeather(lat, lon)
                _weather.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun printGeocoderResult(result: List<Address>?) {
        if (result.isNullOrEmpty()) {
            Log.d("HomeViewModel", "Geocoder result: EMPTY")
            return
        }

        val a = result[0]
        Log.d("HomeViewModel", "---- GEOCODER RESULT ----")
        Log.d("HomeViewModel", "Locality         → ${a.locality}")
        Log.d("HomeViewModel", "SubAdminArea     → ${a.subAdminArea}")
        Log.d("HomeViewModel", "AdminArea        → ${a.adminArea}")
        Log.d("HomeViewModel", "CountryName      → ${a.countryName}")
        Log.d("HomeViewModel", "SubLocality      → ${a.subLocality}")
        Log.d("HomeViewModel", "FeatureName      → ${a.featureName}")
        Log.d("HomeViewModel", "PostalCode       → ${a.postalCode}")
        Log.d("HomeViewModel", "Thoroughfare     → ${a.thoroughfare}")
        Log.d("HomeViewModel", "-------------------------")
    }

    private fun fetchCityImage(cityQuery: String) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "🟦 Searching Unsplash for: $cityQuery")

            try {
                val response = UnsplashInstance.api.searchPhotos(
                    query = cityQuery,
                    clientId = BuildConfig.UNSPLASH_ACCESS_KEY
                )

                Log.d("HomeViewModel", "🟩 Unsplash results: ${response.results.size}")

                val photoUrl = response.results.firstOrNull()?.urls?.regular

                if (photoUrl == null) {
                    Log.e("HomeViewModel", "❌ No image found for: $cityQuery")
                    _cityImageUrl.value = null
                } else {
                    Log.d("HomeViewModel", "🟩 Selected image: $photoUrl")
                    _cityImageUrl.value = photoUrl
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ Unsplash error", e)
                _cityImageUrl.value = null
            }
        }
    }
}
