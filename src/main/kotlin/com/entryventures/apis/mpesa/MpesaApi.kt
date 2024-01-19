package com.entryventures.apis.mpesa

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.*

private const val AUTHORIZATION_ENDPOINT = "oauth/v1/generate?grant_type=client_credentials"
private const val B2C_ENDPOINT = "mpesa/b2c/v3/paymentrequest"
private const val C2B_ENDPOINT = "mpesa/c2b/v1/registerurl"
private const val STK_EXPRESS = "mpesa/stkpush/v1/processrequest"

interface MpesaApi {

    // Mpesa Authorization api
    @GET(AUTHORIZATION_ENDPOINT)
    suspend fun accessToken(@Header("Authorization") authorization: String): MpesaAccessTokenResponse

    // Mpesa Express Stk push
    @POST(STK_EXPRESS)
    suspend fun mpesaExpress(@Header("Authorization") authorization: String, @Body payload: StkRequestPayload): Map<String, String>

    // Business to Client endpoint
    @POST(B2C_ENDPOINT)
    suspend fun b2c(@Header("Authorization") authorization: String, @Body payload: B2cRequestPayload): Map<String, String>
}