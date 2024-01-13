package com.entryventures.exceptions;

import com.entryventures.extensions.charactersUpto
import com.entryventures.services.ControllerService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(
    val controllerService: ControllerService
) {

    @ExceptionHandler(EntryVenturesException::class)
    fun handleEntryVenturesException(ex: EntryVenturesException): ResponseEntity<*>? {
        return controllerService.sendResponse(ex.serverStatus, mapOf("error" to ex.message))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException?): ResponseEntity<*>? {
        return controllerService.sendResponse(HttpStatus.FORBIDDEN, mapOf("error" to "Forbidden Access", "message" to "Please contact your administrator"))
    }

    // Handle DuplicateKeyException
    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKeyException(ex: DuplicateKeyException?): ResponseEntity<*>? {
        return controllerService.sendResponse(HttpStatus.CONFLICT, mapOf("error" to "A record already exists bearing the supplied information."))
    }

    // Handle HttpMessageConversionException
    @ExceptionHandler(HttpMessageConversionException::class)
    fun handleHttpMessageConversionException(ex: HttpMessageConversionException): ResponseEntity<*>? {
        // Create a custom error response or log the exception
        return controllerService.sendResponse(HttpStatus.UNPROCESSABLE_ENTITY, mapOf("error" to "Invalid request body", "message" to "${ex.message?.charactersUpto(':')}"))
    }

    // Handle Constraint Violation Errors
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationExceptions(ex: ConstraintViolationException): ResponseEntity<*>? {
        val errors: MutableMap<String, Any> = HashMap()
        errors["constraint_violations"] = ex.constraintViolations
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body<Map<String, Any>>(errors)
    }

    // Handle MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<*>? {
        val errorsWrapper: MutableMap<String, Any> = HashMap()
        val errors: MutableMap<String, String?> = HashMap()
        val bindingResult = ex.bindingResult
        if (bindingResult.hasErrors()) {
            val fieldErrorList = bindingResult.fieldErrors
            for (fieldError in fieldErrorList) {
                errors[fieldError.field] = fieldError.defaultMessage
            }
        }
        errorsWrapper["errors"] = errors
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body<Map<String, Any>>(errorsWrapper)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleException(e: AuthenticationException?): ResponseEntity<*>? {
        return controllerService.sendResponse(HttpStatus.UNAUTHORIZED, mapOf("error" to "You are not authorized to access this resource"))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun methodNotAllowed(ex: HttpRequestMethodNotSupportedException, req: HttpServletRequest): ResponseEntity<*>? {
        return controllerService.sendResponse(
            HttpStatus.METHOD_NOT_ALLOWED,
            mapOf("error" to String.format("%s on  %s", ex.message, req.servletPath))
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<*>? {
        println(e.message)
        e.printStackTrace()
        return controllerService.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, mapOf("error" to "An internal server error occurred"))
    }
}
