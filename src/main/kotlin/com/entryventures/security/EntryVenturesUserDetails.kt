package com.entryventures.security

import com.entryventures.models.jpa.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class EntryVenturesUserDetails(
    private val user: User
) : UserDetails{
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val auths: MutableCollection<GrantedAuthority> = ArrayList()
        auths.addAll(user.authorities)
        return auths
    }

    override fun getPassword(): String = user.passwordDigest

    override fun getUsername(): String = user.userName

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean  = true

    override fun isCredentialsNonExpired(): Boolean  = true

    override fun isEnabled(): Boolean  = true
}