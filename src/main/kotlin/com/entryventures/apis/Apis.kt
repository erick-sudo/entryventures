package com.entryventures.apis

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Apis {

    val MPESA_CLIENT: MpesaClient by lazy {
        Retrofit.Builder()
            .baseUrl("http://localhost:3001/mpesa/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(MpesaClient::class.java)
    }


}