package com.entryventures.apis.coop

import com.google.gson.annotations.SerializedName

data class SendToMpesaPayload(
    @SerializedName("CallBackUrl") var callBackUrl: String,
    @SerializedName("Destinations") var destinations: List<CoopPaymentDestination>,
    @SerializedName("MessageReference") var messageReference: String,
    @SerializedName("Source") var source: CoopPaymentSource
)

data class SendToMpesaResponse(
    @SerializedName("MessageDateTime") var messageDateTime: String,
    @SerializedName("MessageCode") var messageCode: String,
    @SerializedName("MessageDescription") var messageDescription: String,
    @SerializedName("MessageReference") var messageReference: String
)
