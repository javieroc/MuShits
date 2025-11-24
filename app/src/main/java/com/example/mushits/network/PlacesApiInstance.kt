package com.example.mushits.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object GooglePlacesInstance {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    val api: PlacesApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PlacesApi::class.java)
    }
}
