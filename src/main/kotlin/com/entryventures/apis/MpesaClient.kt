package com.entryventures.apis

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface MpesaClient {

    // Mpesa Authorization api
    @POST("access-token")
    suspend fun accessToken(): MpesaAccessTokenResponse

    // Mpesa Express Stk push
    @POST("express")
    suspend fun mpesaExpress(): Map<String, String>

    @POST("b2c")
    suspend fun b2c(@Header("Authorization") authorization: String, @Body payload: Map<String, String>): Map<String, String>
}