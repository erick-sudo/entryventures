package com.entryventures.apis

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MpesaAccessTokenResponse(
    @Json(name = "access_token") var accessToken: String,
    @Json(name = "expires_in") var expiresIn: Long,
    @Json(name = "realm") var realm: String
)
