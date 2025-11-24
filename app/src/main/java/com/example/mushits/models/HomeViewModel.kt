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
import com.example.mushits.network.GooglePlacesInstance
import com.example.mushits.network.WeatherResponse
import com.example.mushits.BuildConfig
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
        Log.d("HomeViewModel", "🟦 fetchCityImage() called with: $cityQuery")
        Log.d("HomeViewModel", "🟦 API Key: ${BuildConfig.GOOGLE_PLACES_API_KEY.take(10)}...")

        viewModelScope.launch {
            try {
                // 1. Find place
                Log.d("HomeViewModel", "🟩 Calling FindPlace…")

                val findResponse = GooglePlacesInstance.api.findPlace(
                    input = cityQuery,
                    key = BuildConfig.GOOGLE_PLACES_API_KEY
                )

                Log.d("HomeViewModel", "🟩 FindPlace response: $findResponse")

                val placeId = findResponse.candidates.firstOrNull()?.place_id

                if (placeId == null) {
                    Log.e("HomeViewModel", "❌ No placeId found for query: $cityQuery")
                    _cityImageUrl.value = null
                    return@launch
                }

                Log.d("HomeViewModel", "🟩 Extracted placeId: $placeId")

                // 2. Get details → photos list
                Log.d("HomeViewModel", "🟩 Calling PlaceDetails for $placeId")

                val details = GooglePlacesInstance.api.placeDetails(
                    placeId = placeId,
                    key = BuildConfig.GOOGLE_PLACES_API_KEY
                )

                Log.d("HomeViewModel", "🟩 PlaceDetails response: $details")

                val photoRef = details.result?.photos?.firstOrNull()?.photo_reference

                if (photoRef == null) {
                    Log.e("HomeViewModel", "❌ No photo_reference found for placeId: $placeId")
                    _cityImageUrl.value = null
                    return@launch
                }

                Log.d("HomeViewModel", "🟩 photo_reference: $photoRef")

                // 3. Generate real photo URL
                val photoUrl =
                    "https://maps.googleapis.com/maps/api/place/photo" +
                            "?maxwidth=1500" +
                            "&photo_reference=$photoRef" +
                            "&key=${BuildConfig.GOOGLE_PLACES_API_KEY}"

                Log.d("HomeViewModel", "🟦 Final Photo URL: $photoUrl")

                _cityImageUrl.value = photoUrl

            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ Error fetching city image", e)
                _cityImageUrl.value = null
            }
        }
    }
}
