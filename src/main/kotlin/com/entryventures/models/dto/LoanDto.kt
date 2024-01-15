package com.entryventures.models.dto

import com.entryventures.models.LoanStatus
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class LoanDto(
    @JsonProperty("id") var id: String = "",
    @JsonProperty("status") var status: LoanStatus = LoanStatus.Pending,
    @JsonProperty("amount") var amount: Float,
    @JsonProperty("client_id") @NotBlank var clientId: String
)
