package com.entryventures.apis.mpesa

import com.entryventures.apis.Apis
import okhttp3.ResponseBody
import java.util.*
import kotlin.math.roundToInt

object MpesaTransactions {
    suspend fun requestMpesaAccessToken(
        clientErrorHandler: (Int, ResponseBody?) -> Unit = { _, _ -> },
        serverErrorHandler: (Int, ResponseBody?) -> Unit = { _, _ -> },
        connectionErrorHandler: () -> Unit = {},
        unknownHostErrorHandler: () -> Unit = {}
    ): MpesaAccessTokenResponse? {
        return Apis.httpRequestWrapper(
            request = {
                Apis.MPESA_CLIENT.accessToken(
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