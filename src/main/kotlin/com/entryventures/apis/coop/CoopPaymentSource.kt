package com.entryventures.apis.coop

import com.entryventures.models.MonetaryCurrency
import com.google.gson.annotations.SerializedName

data class CoopPaymentSource(
    @SerializedName("Narration") var narration: String,
    @SerializedName("Amount") var amount: String,
    @SerializedName("TransactionCurrency") var transactionCurrency: MonetaryCurrency,
    @SerializedName("AccountNumber") var accountNumber: String
)

data class CoopPaymentSourceRes(
    @SerializedName("ResponseCode") var responseCode: String,
    @SerializedName("Narration") var narration: String,
    @SerializedName("Amount") var amount: String,
    @SerializedName("ResponseDescription") var responseDescription: String,
    @SerializedName("TransactionCurrency") var transactionCurrency: MonetaryCurrency,
    @SerializedName("AccountNumber") var accountNumber: String
)