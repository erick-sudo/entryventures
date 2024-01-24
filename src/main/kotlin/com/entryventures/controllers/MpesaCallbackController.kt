package com.entryventures.controllers

import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.services.ControllerService
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@Tag(name = "Mpesa Callbacks", description = "Mpesa Transactions APIs")
@RestController
@RequestMapping("/entry-ventures/mpesa/callback")
class MpesaCallbackController(
    private val controllerService: ControllerService
) {

    @PostMapping("/stk")
    fun stkExpressCallback(@RequestBody payload: Map<String, String>): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, mapOf("message" to "Success"))
    }

    @PostMapping("/request-stk")
    fun stkRequest(@RequestBody clientStkReq: Map<String, Long>): ResponseEntity<*> = runBlocking {
        if(clientStkReq["amount"] == null || clientStkReq["amount"]!! < 5) {
            throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY) { "Invalid amount" }
        }
        if(clientStkReq["phone"] == null || !("${clientStkReq["phone"]}".matches("254[0-9]{9}\$".toRegex()))) {
            throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY, errorDescription = {
                val errors = mutableListOf("Required format 254XXXXXXXXX")
                if(clientStkReq["phone"] == null) {
                    errors += "Phone number can't be null"
                }
                errors
            }) { "Invalid phone number" }
        }
        ResponseEntity.ok(controllerService.initiateStk(clientStkReq["amount"]!!, clientStkReq["phone"]!!))
    }

    @PostMapping("/b2c")
    fun b2cCallback(@RequestBody payload: Map<String, String>): ResponseEntity<*> {
        println("${Date()} MPESA_B2C_CALLBACK: $payload")
        return controllerService.response(HttpStatus.OK, mapOf("message" to "Success"))
    }
}