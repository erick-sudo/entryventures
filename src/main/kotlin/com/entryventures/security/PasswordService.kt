package com.entryventures.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object PasswordService {

    private val bCryptPasswordEncoder by lazy { BCryptPasswordEncoder() }

    val encryptPassword: (String) -> String = { password -> bCryptPasswordEncoder.encode(password) }

    val verifyPassword: (String, String) -> Boolean = { password, passwordDigest -> bCryptPasswordEncoder.matches(password, passwordDigest) }
}