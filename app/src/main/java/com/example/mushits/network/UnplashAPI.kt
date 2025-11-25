package com.example.mushits.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("client_id") clientId: String,
        @Query("orientation") orientation: String = "landscape",
        @Query("per_page") perPage: Int = 1
    ): UnsplashSearchResponse
}

@Serializable
data class UnsplashSearchResponse(
    val results: List<UnsplashPhoto> = emptyList()
)

@Serializable
data class UnsplashPhoto(
    val urls: UnsplashPhotoUrls
)

@Serializable
data class UnsplashPhotoUrls(
    val regular: String,
    val full: String? = null,
    val small: String? = null
)
