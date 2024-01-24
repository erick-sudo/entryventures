package com.entryventures.controllers

import com.entryventures.models.dto.AccessTokenRequest
import com.entryventures.services.ControllerService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Authentication", description = "Entry Ventures Authentication and Authorization APIs")
@RestController
@RequestMapping("/entryventures/api/v1/auth")
class AuthController(
    private val controllerService: ControllerService
) {
    @PostMapping("/access-token")
    fun accessToken(@Valid @RequestBody credentials: AccessTokenRequest): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getAccessToken(credentials))
    }
}