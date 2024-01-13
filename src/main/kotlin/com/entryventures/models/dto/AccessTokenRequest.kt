package com.entryventures.models.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class AccessTokenRequest(
    @JsonProperty("grant_type")
    @NotBlank
    var grantType: String,

    @JsonProperty("client_id")
    @NotBlank
    var clientId: String,

    @JsonProperty("client_secret")
    @NotBlank
    var clientSecret: String,
)
