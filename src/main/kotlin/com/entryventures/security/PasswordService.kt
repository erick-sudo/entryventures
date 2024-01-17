package com.entryventures.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*


object PasswordService {

    private val bCryptPasswordEncoder by lazy { BCryptPasswordEncoder() }

    val encryptPassword: (String) -> String = { password -> bCryptPasswordEncoder.encode(password) }

    val verifyPassword: (String, String) -> Boolean = { password, passwordDigest -> bCryptPasswordEncoder.matches(password, passwordDigest) }

    fun toBase64(vararg tokens: String): String {
        return Base64.getEncoder().encodeToString(
            String.format(
                "%s".repeat(tokens.size),
                *tokens
            ).toByteArray()
        )
    }

    fun timeStamp(): String {
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now

        return String.format(
            "%04d%02d%02d%02d%02d%02d",
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            calendar[Calendar.SECOND]
        )
    }
}