package com.entryventures.services

import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.models.dto.AccessTokenRequest
import com.entryventures.models.jpa.User
import com.entryventures.repository.UserRepository
import com.entryventures.security.JwtService
import com.entryventures.security.PasswordService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class ControllerService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    val findUser: (identity: String) -> User = { userIdentity ->
        userRepository.findByUsernameOrEmail(identifier = userIdentity).let {
            when(it.isPresent) {
                true -> it.get()
                else -> throw EntryVenturesException(HttpStatus.NOT_FOUND) {
                    String.format("The user \'$userIdentity\' was not found")
                }
            }
        }
    }

    fun getAccessToken(credentials: AccessTokenRequest): ResponseEntity<*> {
        val user = findUser(credentials.clientId)
        if(user.authenticate(credentials.clientSecret)) {
            val payload = mapOf(
                "id" to user.id,
                "username" to user.userName,
                "email" to user.email,
                "roles" to user.getRoles().map { mapOf("name" to it.name, "description" to it.description ) },
                "groups" to user.getGroups().map { mapOf("name" to it.name, "description" to it.description ) }
            )

            val token = jwtService.generateJwtToken(payload)

            return ResponseEntity.status(200).body(
                mapOf(
                    "token" to token,
                    "expires_in" to jwtService.jwtExpirationMs,
                    "realm" to "Bearer"
                )
            )
        }

        throw EntryVenturesException(HttpStatus.UNAUTHORIZED) {
            "Invalid username or password"
        }
    }

    fun sendResponse(
        httpStatus: HttpStatus = HttpStatus.OK,
        response: Map<String, Any>
    ): ResponseEntity<*> {
        return ResponseEntity.status(httpStatus).body(response)
    }

    fun entryVenturesExceptionResponse(
        entryVenturesException: EntryVenturesException
    ) : ResponseEntity<*> {
        return ResponseEntity.status(entryVenturesException.serverStatus).body(mapOf("error" to entryVenturesException.message))
    }

    fun saveUserWithPassword(user: User, password: String): User  {
        val user1 = userRepository.findByUserName(user.userName)
        if(user1 == null) {
            user.passwordDigest = PasswordService.encryptPassword(password)
            userRepository.save(user)
        }
        return user
    }
}