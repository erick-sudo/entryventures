package com.entryventures.controllers

import com.entryventures.services.ControllerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
@RequestMapping("/entry-ventures/mpesa/callback")
class MpesaCallbackController(
    private val controllerService: ControllerService
) {

    @PostMapping("/stk")
    fun stkExpressCallback(@RequestBody payload: Map<String, String>): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, mapOf("message" to "Success"))
    }

    @PostMapping("/b2c")
    fun b2cCallback(@RequestBody payload: Map<String, String>): ResponseEntity<*> {
        println("${Date()} MPESA_B2C_CALLBACK: $payload")
        return controllerService.response(HttpStatus.OK, mapOf("message" to "Success"))
    }
}