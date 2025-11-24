package com.example.mushits.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object ForecastApiInstance {

    private const val BASE_URL = "https://api.open-meteo.com/"

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        isLenient = true
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val api: ForecastAPI by lazy {
        retrofit.create(ForecastAPI::class.java)
    }
}
