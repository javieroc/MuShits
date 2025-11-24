package com.example.mushits.network

import retrofit2.http.GET
import retrofit2.http.Query


@kotlinx.serialization.Serializable
data class PlacesFindResponse(
    val candidates: List<PlaceCandidate> = emptyList()
)

@kotlinx.serialization.Serializable
data class PlaceCandidate(
    val place_id: String
)

@kotlinx.serialization.Serializable
data class PlaceDetailsResponse(
    val result: PlaceResult? = null
)

@kotlinx.serialization.Serializable
data class PlaceResult(
    val photos: List<PlacePhoto>? = null
)

@kotlinx.serialization.Serializable
data class PlacePhoto(
    val photo_reference: String,
    val height: Int,
    val width: Int
)

interface PlacesApi {
    @GET("place/findplacefromtext/json")
    suspend fun findPlace(
        @Query("input") input: String,
        @Query("inputtype") inputType: String = "textquery",
        @Query("fields") fields: String = "place_id",
        @Query("key") key: String
    ): PlacesFindResponse

    @GET("place/details/json")
    suspend fun placeDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "photo",
        @Query("key") key: String
    ): PlaceDetailsResponse
}
