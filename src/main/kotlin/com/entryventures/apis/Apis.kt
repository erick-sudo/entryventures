package com.entryventures.apis

import com.entryventures.apis.mpesa.MpesaAccessTokenResponse
import com.entryventures.apis.mpesa.MpesaClient
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.*

private const val MPESA_BASEURL = "https://sandbox.safaricom.co.ke/"

object Apis {

    val MPESA_CLIENT: MpesaClient by lazy {
        Retrofit.Builder()
            .baseUrl(MPESA_BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(MpesaClient::class.java)
    }

    suspend fun requestMpesaAccessToken(
        clientErrorHandler: (Int, ResponseBody?) -> Unit = {_,_ -> },
        serverErrorHandler: (Int, ResponseBody?) -> Unit = {_,_ -> },
        connectionErrorHandler: () -> Unit = {},
        unknownHostErrorHandler: () -> Unit = {}
    ): MpesaAccessTokenResponse? {
        return httpRequestWrapper(
            request = {
                MPESA_CLIENT.accessToken(
                    authorization = "Basic bVJmUXd1OGpBUkdSYVlKdFZXWkE3OVlhTHB0bWs4cDY6dlh6SmJtMTZBcjhLOVpWOQ=="
                )
            },
            clientErrorHandler = clientErrorHandler,
            serverErrorHandler = serverErrorHandler,
            connectionErrorHandler = connectionErrorHandler,
            unknownHostErrorHandler = unknownHostErrorHandler
        )
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