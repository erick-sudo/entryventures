package com.entryventures.controllers

import com.entryventures.models.jpa.Client
import com.entryventures.services.ControllerService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Clients", description = "Clients management APIs")
@RestController
class ClientsController(
    private val controllerService: ControllerService
) {

    @GetMapping("/clients")
    fun clients(): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getClients())
    }

    @GetMapping("/clients/{clientId}")
    fun showClient(@PathVariable("clientId") clientId: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.showClient(clientId))
    }

    @PostMapping("/clients")
    fun createClient(@Valid @RequestBody client: Client): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createClient(client))
    }

    @PatchMapping("/clients/{clientId}")
    fun updateClient(@RequestBody payload: Map<String, String>, @PathVariable("clientId") clientId: String): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.updateClient(clientId, payload))
    }

    @DeleteMapping("/clients/{clientId}")
    fun deleteClient(@PathVariable("clientId") clientId: String): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.NO_CONTENT, controllerService.deleteClient(clientId))
    }
}