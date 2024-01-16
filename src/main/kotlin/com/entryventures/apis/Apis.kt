package com.entryventures.apis

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException

object Apis {

    val MPESA_CLIENT: MpesaClient by lazy {
        Retrofit.Builder()
            .baseUrl("http://localhost:3001/mpesa/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(MpesaClient::class.java)
    }


    suspend fun <T> httpRequestWrapper(
        request: suspend () -> T,
        clientErrorHandler: (Int, ResponseBody?) -> Unit = {_,_ -> },
        serverErrorHandler: (Int, ResponseBody?) -> Unit = {_,_ -> },
        connectionErrorHandler: () -> Unit = {},
        unknownHostErrorHandler: () -> Unit = {}
    ): T? {
        try {
            return request()
        } catch (e: ConnectException) {
            // Connection exception (network not available)
            connectionErrorHandler()
        } catch (e: UnknownHostException) {
            // Unknown host exception (host not reachable)
            unknownHostErrorHandler()
        } catch (e: HttpException) {
            // Http exceptions (non-successful-response)

            val responseBody = e.response()?.errorBody()

            if(e.code() in (400..< 500)) {
                // Client errors
                clientErrorHandler(e.code(), responseBody)
            } else if (e.code() in (500..< 600)) {
                // Server errors
                serverErrorHandler(e.code(), responseBody)
            } else {
                // Other errors
            }
        } catch (e: Exception) {
            // Other exceptions
        }
        return null
    }
}