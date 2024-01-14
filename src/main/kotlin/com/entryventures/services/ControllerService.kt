package com.entryventures.services

import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.models.dto.AccessTokenRequest
import com.entryventures.models.dto.LoanCollectionDto
import com.entryventures.models.dto.LoanDto
import com.entryventures.models.jpa.Client
import com.entryventures.models.jpa.Loan
import com.entryventures.models.jpa.LoanCollection
import com.entryventures.models.jpa.User
import com.entryventures.repository.ClientRepository
import com.entryventures.repository.LoanCollectionRepository
import com.entryventures.repository.LoanRepository
import com.entryventures.repository.UserRepository
import com.entryventures.security.JwtService
import com.entryventures.security.PasswordService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class ControllerService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val loanRepository: LoanRepository,
    private val clientRepository: ClientRepository,
    private val loanCollectionRepository: LoanCollectionRepository
) {

    fun saveUserWithPassword(user: User, password: String): User  {
        val user1 = userRepository.findByUserName(user.userName)
        if(user1 == null) {
            user.passwordDigest = PasswordService.encryptPassword(password)
            userRepository.save(user)
        }
        return user
    }

    fun getAccessToken(credentials: AccessTokenRequest): Map<String, Any> {
        val user = Crud.find {
            userRepository.findByUsernameOrEmail(identifier = credentials.clientId)
        }
        if(user.authenticate(credentials.clientSecret)) {
            val payload = mapOf(
                "id" to user.id,
                "username" to user.userName,
                "email" to user.email,
                "roles" to user.getRoles().map { mapOf("name" to it.name, "description" to it.description ) },
                "groups" to user.getGroups().map { mapOf("name" to it.name, "description" to it.description ) }
            )

            val token = jwtService.generateJwtToken(payload)

            return mapOf(
                "token" to token,
                "expires_in" to jwtService.jwtExpirationMs,
                "realm" to "Bearer"
            )
        }

        throw EntryVenturesException(HttpStatus.UNAUTHORIZED) {
            "Invalid username or password"
        }
    }

    fun getLoans(): List<Loan> {
        return loanRepository.findAll().toList()
    }

    fun createLoan(
        loanDto: LoanDto
    ): Loan {
        val loan = Loan(loanDto, clientRepository)
        loanRepository.save(loan)
        return loan
    }

    fun showLoan(
        loanId: String
    ): Loan {
        return Crud.find {
            loanRepository.findById(loanId)
        }
    }

    fun getLoanCollections(): List<LoanCollection> {
        return loanCollectionRepository.findAll().toList()
    }

    fun createLoanCollection(
        loanCollectionDto: LoanCollectionDto
    ): LoanCollection {
        val loanCollection = LoanCollection(loanCollectionDto, loanRepository)
        loanCollectionRepository.save(loanCollection)
        return loanCollection
    }

    fun showLoanCollection(
        loanCollectionID: String
    ): LoanCollection {
        return Crud.find {
            loanCollectionRepository.findById(loanCollectionID)
        }
    }

    fun getClients(): List<Client> {
        return clientRepository.findAll().toList()
    }

    fun createClient(
        client: Client
    ): Client {
        clientRepository.save(client)
        return client
    }

    fun showClient(
        clientId: String
    ): Client {
        return Crud.find {
            clientRepository.findById(clientId)
        }
    }

    fun response(
        httpStatus: HttpStatus = HttpStatus.OK,
        response: Any
    ): ResponseEntity<*> {
        return ResponseEntity.status(httpStatus).body(response)
    }

    fun entryVenturesExceptionResponse(
        entryVenturesException: EntryVenturesException
    ) : ResponseEntity<*> {
        return ResponseEntity.status(entryVenturesException.serverStatus).body(mapOf("error" to entryVenturesException.message))
    }
}