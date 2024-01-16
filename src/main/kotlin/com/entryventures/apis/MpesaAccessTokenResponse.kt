package com.entryventures.apis

import com.google.gson.annotations.SerializedName

data class MpesaAccessTokenResponse(
    @SerializedName("access_token") var accessToken: String,
    @SerializedName("expires_in") var expiresIn: Long,
    @SerializedName("realm") var realm: String
)
