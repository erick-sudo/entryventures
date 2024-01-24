package com.entryventures.apis.coop

import com.google.gson.annotations.SerializedName

data class CoopPaymentDestination(
    @SerializedName("MobileNumber") var mobileNumber: String,
    @SerializedName("Narration") var narration: String,
    @SerializedName("ReferenceNumber") var referenceNumber: String,
    @SerializedName("Amount") var amount: String
)

data class CoopPaymentDestinationRes(
    @SerializedName("MobileNumber") var mobileNumber: String,
    @SerializedName("ResponseCode") var responseCode: String,
    @SerializedName("Narration") var narration: String,
    @SerializedName("ReferenceNumber") var referenceNumber: String,
    @SerializedName("Amount") var amount: String,
    @SerializedName("ResponseDescription") var responseDescription: String,
    @SerializedName("TransactionID") var transactionId: String
)