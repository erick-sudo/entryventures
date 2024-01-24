package com.entryventures.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val entryVenturesAuthenticationEntryPoint: EntryVenturesAuthenticationEntryPoint,
    private val entryVenturesUserDetailsService: EntryVenturesUserDetailsService,
    private val jwtService: JwtService,
    @Value("\${cors.origins}") private val allowedOrigins: String
) {

    private val authTokenFilter: () -> AuthorizationTokenFilter = @Bean { AuthorizationTokenFilter(entryVenturesUserDetailsService, jwtService) }

    private val bCryptPasswordEncoder: () -> BCryptPasswordEncoder = @Bean { BCryptPasswordEncoder() }

    @Bean
    fun daoAuthenticationProvider() : DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()

        authProvider.setUserDetailsService(entryVenturesUserDetailsService)
        authProvider.setPasswordEncoder(bCryptPasswordEncoder())

        return authProvider
    }

    @Bean
    fun filterChain(http: HttpSecurity) : SecurityFilterChain {

        http
            .csrf { crfConfigurer ->
                crfConfigurer.disable()
            }
            .cors { corsConfigurer ->
                corsConfigurer.configurationSource(corsCustomizer())
            }
            .authorizeHttpRequests { auth ->
            auth.requestMatchers("/", "/entryventures/api/v1/auth/access-token", "/entry-ventures/mpesa/callback/**", "/swagger-ui/**", "/v3/**").permitAll()
                .anyRequest().authenticated()
        }
            .exceptionHandling { exc ->
                exc.authenticationEntryPoint(entryVenturesAuthenticationEntryPoint)
            }

        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
        http.authenticationProvider(daoAuthenticationProvider())

        return http.build()
    }

    private fun corsCustomizer(): UrlBasedCorsConfigurationSource {

        val configuration = CorsConfiguration()
        configuration.allowedOrigins = (allowedOrigins.split(","))
        configuration.addAllowedMethod("*")
        configuration.addAllowedHeader("*")
        configuration.allowCredentials = true


        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }
}