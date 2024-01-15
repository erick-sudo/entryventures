package com.entryventures.exceptions;

import com.entryventures.extensions.charactersUpto
import com.entryventures.services.ControllerService
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
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
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler(
    val controllerService: ControllerService
) {

    @ExceptionHandler(EntryVenturesException::class)
    fun handleEntryVenturesException(ex: EntryVenturesException): ResponseEntity<*>? {
        val errorDescription = mutableMapOf<String, Any>()
        ex.description?.let {
            errorDescription["description"] = ex.description!!
        }
        errorDescription["error"] = ex.message
        return controllerService.response(ex.serverStatus, errorDescription)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException?): ResponseEntity<*>? {
        return controllerService.response(HttpStatus.FORBIDDEN, mapOf("error" to "Forbidden Access", "message" to "Please contact your administrator"))
    }

    // Handle DuplicateKeyException
    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKeyException(ex: DuplicateKeyException?): ResponseEntity<*>? {
        return controllerService.response(HttpStatus.CONFLICT, mapOf("error" to "A record already exists bearing the supplied information."))
    }

    // Handle HttpMessageConversionException
    @ExceptionHandler(HttpMessageConversionException::class)
    fun handleHttpMessageConversionException(ex: HttpMessageConversionException): ResponseEntity<*>? {
        return controllerService.response(HttpStatus.UNPROCESSABLE_ENTITY, mapOf("error" to "Invalid request body", "message" to "${ex.message?.charactersUpto(':')}"))
    }

    // Handle Constraint Violation Errors
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationExceptions(ex: ConstraintViolationException): ResponseEntity<*>? {
        val errors: MutableMap<String, Any> = HashMap()
        errors["constraint_violations"] = ex.constraintViolations
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body<Map<String, Any>>(errors)
    }

    // Handle data integrity violation exceptions
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationExceptions(ex: DataIntegrityViolationException): ResponseEntity<*> {
        return controllerService.response(HttpStatus.UNPROCESSABLE_ENTITY, mapOf("error" to "Database integrity violation error occurred"))
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
    fun handleAuthenticationException(e: AuthenticationException): ResponseEntity<*>? {
        return controllerService.response(HttpStatus.UNAUTHORIZED, mapOf("error" to e.message))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun methodNotAllowed(ex: HttpRequestMethodNotSupportedException, req: HttpServletRequest): ResponseEntity<*>? {
        return controllerService.response(
            HttpStatus.METHOD_NOT_ALLOWED,
            mapOf("error" to String.format("%s on  %s", ex.message, req.servletPath))
        )
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(e: NoResourceFoundException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<*>? {
        e.printStackTrace()
        return controllerService.response(HttpStatus.INTERNAL_SERVER_ERROR, mapOf("error" to "An internal server error occurred"))
    }
}
