package com.entryventures.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver


/**
 * Handle unsuccessful Bearer JWT authentication.
 * Also delegated the exception handler to HandlerExceptionResolver
 */
@Component
class EntryVenturesAuthenticationEntryPoint(
    // Injecting the DefaultHandlerExceptionHandler and delegating the handler to this resolver.
    // This security exception can now be handled with controller advice with an exception handler method
    @Qualifier("handlerExceptionResolver") val resolver: HandlerExceptionResolver
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        this.resolver.resolveException(request, response, null, authException)
    }
}