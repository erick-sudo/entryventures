package com.entryventures.security

import com.entryventures.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class EntryVenturesUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(identity: String): UserDetails {
        val optionalUser = userRepository.findByUsernameOrEmail(identity)

        when(optionalUser.isPresent) {
            true -> return EntryVenturesUserDetails(optionalUser.get())
            else -> throw UsernameNotFoundException(String.format("User with Identity: \'$identity\' not found"))
        }
    }
}