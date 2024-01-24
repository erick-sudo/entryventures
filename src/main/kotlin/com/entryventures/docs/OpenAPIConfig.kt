package com.entryventures.docs

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAPIConfig {

    @Value("\${docs.openapi.dev-url}")
    private lateinit var devUrl: String

    @Value("\${docs.openapi.prod-url}")
    private lateinit var prodUrl: String

    @Bean
    fun openApi(): OpenAPI {
        val devServer = Server().apply {
            url = devUrl
            description = "Server URL in Development environment"
        }

        val prodServer = Server().apply {
            url = prodUrl
            description = "Server URL in Production environment"
        }

        val contact = Contact().apply {
            email = "entryventures@gmail.com"
            name = "Entry Ventures"
            url = "https://www.entry_ventures.com"
        }

        val mitLicense = License().name("MIT License")

        val info = Info().apply {
            title = "Entry Ventures Loan Management API"
            version = "1.0"
            contact(contact)
            description = "This API exposes endpoints to manage loan applications, and collections"
            license = mitLicense
        }

        return OpenAPI()
            .info(info)
            .servers(listOf(devServer, prodServer))
    }
}