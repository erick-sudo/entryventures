package com.entryventures.apis.mpesa

import com.entryventures.apis.Apis
import com.entryventures.apis.Apis.httpRequestWrapper
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.*
import kotlin.math.roundToInt

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

    suspend fun requestMpesaAccessToken(
        clientErrorHandler: (Int, ResponseBody?) -> Unit = {_,_ -> },
        serverErrorHandler: (Int, ResponseBody?) -> Unit = {_,_ -> },
        connectionErrorHandler: () -> Unit = {},
        unknownHostErrorHandler: () -> Unit = {}
    ): MpesaAccessTokenResponse? {
        return httpRequestWrapper(
            request = {
                accessToken(
                    authorization = "Basic bVJmUXd1OGpBUkdSYVlKdFZXWkE3OVlhTHB0bWs4cDY6dlh6SmJtMTZBcjhLOVpWOQ=="
                )
            },
            clientErrorHandler = clientErrorHandler,
            serverErrorHandler = serverErrorHandler,
            connectionErrorHandler = connectionErrorHandler,
            unknownHostErrorHandler = unknownHostErrorHandler
        )
    }

    // Default B2C Transaction
    suspend fun processB2CTransaction(
        transactionId: String,
        customer: RegisteredMpesaClient,
        amount: Float,
        responseCallback: suspend (Map<String, String>) -> Unit
    ) {

        // Obtain mpesa authorization access token
        val accessTokenResponse = requestMpesaAccessToken(
                clientErrorHandler = { status, responseBody ->
                    println("${Date()} MPESA_AUTHORIZATION_API: $status :  ${responseBody?.string()}")
                },
                serverErrorHandler = { status, responseBody ->
                    println("${Date()} MPESA_AUTHORIZATION_API: $status :  ${responseBody?.string()}")
                }
        )

        accessTokenResponse?.apply {
            // Successful access token request
            val b2cResponse = Apis.httpRequestWrapper(
                    request = {
                        Apis.MPESA_CLIENT.b2c(
                                payload = B2cRequestPayload(
                                    originatorConversationID = transactionId,
                                    initiatorName = "testapi",
                                    commandID = "BusinessPayment",
                                    amount = "${amount.roundToInt()}",
                                    partyA = "600996",
                                    partyB = "254${customer.phone}",
                                    remarks = "Loan Disbursement",
                                    queueTimeOutURL = "",
                                    resultURL = "http://localhost:8080/entry-ventures/mpesa/callback/b2c",
                                    occasion = "Loan Disbursement"
                                ),
                                authorization = "Bearer $accessToken"
                        )
                    },
                    clientErrorHandler = { status, responseBody ->
                        println("${Date()} MPESA_B2C_API: $status :  ${responseBody?.string()}")
                    },
                    serverErrorHandler = { status, responseBody ->
                        println("${Date()} MPESA_B2C_API: $status :  ${responseBody?.string()}")
                    }
            )

            b2cResponse?.let { b2cRes ->
                // Successful b2c initiation
                responseCallback(b2cRes)
            }
        }
    }
}