package com.entryventures.controllers

import com.entryventures.extensions.toLoanStatus
import com.entryventures.models.dto.LoanDto
import com.entryventures.services.ControllerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoansController(
    private val controllerService: ControllerService
) {

    @GetMapping("/loans/stats/count")
    fun count(): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.countLoans())
    }

    @GetMapping("/loans/stats/status/tally")
    fun loanTallyByStatus(): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.loanTallyByStatus())
    }

    @GetMapping("/loans/by/{loanStatus}/{pageNumber}/{pageSize}")
    fun loansByStatus(
        @PathVariable("loanStatus") loanStatus: String,
        @PathVariable("pageNumber") pageNumber: Int,
        @PathVariable("pageSize") pageSize: Int
    ): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.loansByStatus(loanStatus.toLoanStatus(), pageNumber, pageSize))
    }

    @GetMapping("/loans/pagination/{pageNumber}/{pageSize}")
    fun loans(@PathVariable("pageNumber") pageNumber: Int, @PathVariable("pageSize") pageSize: Int): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getLoans(pageNumber, pageSize).map { it.toLoanDto() })
    }

    @GetMapping("/loans/{loanId}")
    fun showLoan(@PathVariable("loanId") loanId: String): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.showLoan(loanId).toLoanDto())
    }

    @PostMapping("/loans")
    fun createLoan(@Valid @RequestBody loanDto: LoanDto): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.createLoan(loanDto).toLoanDto())
    }

    @GetMapping("/loans/{loanId}/collections/{pageNumber}/{pageSize}")
    fun getLoanRepayments(
        @PathVariable("loanId") loanId: String,
        @PathVariable("pageNumber") pageNumber: Int,
        @PathVariable("pageSize") pageSize: Int
    ): ResponseEntity<*> {
        return controllerService.response(HttpStatus.OK, controllerService.getLoanRepayments(
            loanId,
            pageNumber,
            pageSize
        ).map { it.toLoanCollectionDto() })
    }

    @PatchMapping("/loans/{loanId}")
    fun updateLoan(@RequestBody payload: Map<String, String>, @PathVariable("loanId") loanId: String): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.CREATED, controllerService.updateLoan(loanId, payload).toLoanDto())
    }

    @DeleteMapping("/loans/{loanId}")
    fun deleteLoan(@PathVariable("loanId") loanId: String): ResponseEntity<*> {
        return controllerService.response(httpStatus = HttpStatus.NO_CONTENT, controllerService.deleteLoan(loanId))
    }
}