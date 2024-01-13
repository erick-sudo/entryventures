package com.entryventures.models.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class AccessTokenRequest(
    @JsonProperty("grant_type")
    @NotBlank
    private val grantType: String,

    @JsonProperty("client_id")
    @NotBlank
    private val clientId: String,

    @JsonProperty("client_secret")
    @NotBlank
    private val clientSecret: String,
)
