package com.entryventures.models.dto

import com.entryventures.models.PaymentMethod
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class LoanCollectionDto(
    @JsonProperty("id") var id: String = "",
    @JsonProperty("loan_id") var loanId: String,
    @JsonProperty("collection_date") var collectionDate: Date = Date(),
    @JsonProperty("amount") var amount: Int = 0,
    @JsonProperty("payment_method") var paymentMethod: PaymentMethod = PaymentMethod.Cash
)
