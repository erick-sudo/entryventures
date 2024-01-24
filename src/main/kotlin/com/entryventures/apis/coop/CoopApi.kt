package com.entryventures.apis.coop

import retrofit2.http.POST

interface CoopApi {

    @POST("FundsTransfer/External/A2M/Mpesa")
    fun sendToMpesa(): SendToMpesaResponse

}