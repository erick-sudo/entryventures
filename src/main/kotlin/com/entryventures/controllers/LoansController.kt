package com.entryventures.controllers

import com.entryventures.models.dto.LoanDto
import com.entryventures.models.jpa.Loan
import com.entryventures.services.ControllerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoansController(
    private val controllerService: ControllerService
) {

    @GetMapping("/loans")
    fun loans(): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getLoans().map { it.toLoanDto() })
    }

    @GetMapping("/loans/{loanId}")
    fun showLoan(@PathVariable("loanId") loanId: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.showLoan(loanId).toLoanDto())
    }

    @PostMapping("/loans")
    fun createLoan(@Valid @RequestBody loanDto: LoanDto): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createLoan(loanDto).toLoanDto())
    }

}