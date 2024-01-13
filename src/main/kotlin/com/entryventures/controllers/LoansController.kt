package com.entryventures.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoansController {

    @GetMapping("/")
    fun welcome(): ResponseEntity<*> {
        return ResponseEntity.ok(mapOf("message" to "Welcome to Entry Ventures"))
    }
}