package com.entryventures.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
object JwtService {

    @Value("\${frenzy.app.jwt.jwtSecretKey}")
    private var jwtSecretKey: String = ""

    @Value("\${frenzy.app.jwt.jwtExpirationMs}")
    private var jwtExpirationMs: Long = 3600

    fun generateJwtToken(claimsMap: Map<String, Any>): String? {
        val now = Date()
        val expirationDate = Date(now.time + jwtExpirationMs)
        return Jwts.builder()
            .setClaims(claimsMap)
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm., jwtSecretKey)
            .compact()
    }

    fun getFieldFromJwtToken(fieldName: String, token: String): String? {
        return try {
            val claims: Claims = Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
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
                .getBody()
            !isTokenExpired(claims.getExpiration())
        } catch (e: Exception) {
            false
        }
    }

    private fun isTokenExpired(expiration: Date): Boolean {
        return expiration.before(Date())
    }
}