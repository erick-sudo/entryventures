package com.entryventures.controllers

import com.entryventures.models.dto.LoanCollectionDto
import com.entryventures.services.ControllerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoanCollectionsController(
    private val controllerService: ControllerService
) {

    @GetMapping("/loan/collections")
    fun collection(): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getLoanCollections().map { it.toLoanCollectionDto() })
    }

    @GetMapping("/loan/collections/{loanCollectionId}")
    fun showCollection(@PathVariable("loanCollectionId") loanCollectionId: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.showLoanCollection(loanCollectionId).toLoanCollectionDto())
    }

    @PostMapping("/loan/collections")
    fun createCollection(@Valid @RequestBody loanCollectionDto: LoanCollectionDto): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createLoanCollection(loanCollectionDto).toLoanCollectionDto())
    }
}