package com.entryventures.apis.mpesa

import com.google.gson.annotations.SerializedName

data class B2cRequestPayload(
    @SerializedName("OriginatorConversationID") var originatorConversationID: String,
    @SerializedName("InitiatorName")var initiatorName: String,
    @SerializedName("CommandID")var commandID: String,
    @SerializedName("Amount")var amount: String,
    @SerializedName("PartyA")var partyA: String,
    @SerializedName("PartyB")var partyB: String,
    @SerializedName("Remarks")var remarks: String,
    @SerializedName("QueueTimeOutURL")var queueTimeOutURL: String,
    @SerializedName("ResultURL")var resultURL: String,
    @SerializedName("Occassion")var occasion: String
) {
    @SerializedName("SecurityCredential")
    var securityCredential: String = ""
}