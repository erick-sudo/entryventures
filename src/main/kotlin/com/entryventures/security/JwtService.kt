package com.entryventures.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
object JwtService {

    @Value("\${entry.ventures.jwt.jwtSecretKey}")
    private var jwtSecretKey: String = ""

    @Value("\${entry.ventures.jwt.jwtExpirationMs}")
    private var jwtExpirationMs: Long = 3600

    fun generateJwtToken(claimsMap: Map<String, Any>): String? {
        val now = Date()
        val expirationDate = Date(now.time + jwtExpirationMs)
        return Jwts.builder()
            .setClaims(claimsMap)
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
            .compact()
    }

    fun getFieldFromJwtToken(fieldName: String, token: String): String? {
        return try {
            val claims: Claims = Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .body
            claims.get(fieldName).toString()
        } catch (e: Exception) {
            null
        }
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims: Claims = Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .body
            !isTokenExpired(claims.getExpiration())
        } catch (e: Exception) {
            false
        }
    }

    private fun isTokenExpired(expiration: Date): Boolean {
        return expiration.before(Date())
    }
}