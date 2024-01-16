package com.entryventures.apis

import retrofit2.http.Body
import retrofit2.http.POST


interface MpesaClient {

    // Mpesa Authorization api
    @POST("access-token")
    suspend fun accessToken(): Map<String, String>

    // Mpesa Express Stk push
    @POST("express")
    suspend fun mpesaExpress(): Map<String, String>

    @POST("b2c")
    suspend fun b2c(@Body payload: Map<String, String>): Map<String, String>
}