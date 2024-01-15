package com.entryventures.services

import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.extensions.isValidEmail
import com.entryventures.extensions.toDate
import com.entryventures.extensions.toLoanStatus
import com.entryventures.extensions.toPaymentMethod
import com.entryventures.models.LoanStatus
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
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

val LOAN_UPDATE_FIELDS = listOf("amount", "status")
val CLIENT_UPDATE_FIELDS = listOf("name", "email", "address")
val LOAN_COLLECTION_UPDATE_FIELDS = listOf("amount", "collection_date", "payment_method")

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

    fun getLoans(pageNumber: Int, pageSize: Int): List<Loan> {
        return Crud.paginate(
            pageNumber = pageNumber,
            pageSize = pageSize,
            count = { loanRepository.count() },
            execute = { pageable ->
                loanRepository.findAll(pageable).toList()
            }
        )
    }

    fun countLoans(): Map<String, Long> = mapOf("count" to loanRepository.count())

    fun countLoanCollectionsByLoanId(loanId: String): Map<String, Long> = mapOf("count" to loanCollectionRepository.countLoanCollectionsByLoanId(loanId))

    fun loanTallyByStatus(): Map<String, Long> = LoanStatus.entries.fold(mutableMapOf()) { acc, curr ->
        acc += (curr.name to loanRepository.countByStatus(curr))
        acc
    }

    fun loansByStatus(
        loanStatus: LoanStatus,
        pageNumber: Int,
        pageSize: Int
    ): List<Loan> {
        return loanRepository.findLoanByStatus(loanStatus, PageRequest.of(if(pageNumber > 0) pageNumber -1 else 1, pageSize))
    }

    fun createLoan(
        loanDto: LoanDto
    ): Loan {
        val loan = Loan(loanDto, clientRepository)
        loanRepository.save(loan)
        return loan
    }

    fun updateLoan(
        loanId: String,
        payload: Map<String, String>
    ): Loan {
        val loan = Crud.find {
            loanRepository.findById(loanId)
        }

        payload.keys.filter { LOAN_UPDATE_FIELDS.contains(it) }.forEach { key ->
            when(key) {
                "amount" -> loan.apply {
                    payload["amount"]?.let { amountString ->
                        try {
                            amount = amountString.toFloat()
                        } catch (exc: NumberFormatException) {
                            throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY) {
                                "Invalid loan amount"
                            }
                        }
                    }
                }
                "status" -> loan.apply {
                    payload["status"]?.let {  statusString ->
                        status = statusString.toLoanStatus()
                    }
                }
            }
        }

        loanRepository.save(loan)
        return loan
    }

    fun deleteLoan(
        loanId: String
    ) {
        Crud.find {
            loanRepository.findById(loanId)
        }.apply {
            loanRepository.deleteById(id)
        }
    }

    fun showLoan(
        loanId: String
    ): Loan {
        return Crud.find {
            loanRepository.findById(loanId)
        }
    }

    fun getLoanRepayments(
        loanId: String,
        pageNumber: Int,
        pageSize: Int
    ): List<LoanCollection> {
        val loan = Crud.find {
            loanRepository.findById(loanId)
        }

        return loanCollectionRepository.findLoanCollectionsByLoan(loan.id, PageRequest.of(pageNumber - 1, pageSize))
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

    fun updateLoanCollection(
        loanCollectionID: String,
        payload: Map<String, String>
    ): LoanCollection {
        return Crud.find {
            loanCollectionRepository.findById(loanCollectionID)
        }.apply {
            payload.keys
                .filter { LOAN_COLLECTION_UPDATE_FIELDS.contains(it) }
                .forEach { key ->
                    when(key) {
                        "collection_date" -> {
                            payload["collection_date"]?.let { date ->
                                collectionDate = date.toDate()
                            }
                        }
                        "amount" -> {
                            payload["amount"]?.let { amountString ->
                                try {
                                    amount = amountString.toFloat()
                                } catch (exc: NumberFormatException) {
                                    throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY) {
                                        "Invalid amount"
                                    }
                                }
                            }
                        }
                        "payment_method" -> {
                            payload["payment_method"]?.let {  paymentMethodString ->
                                paymentMethod = paymentMethodString.toPaymentMethod()
                            }
                        }
                    }
                }
        }
    }

    fun showLoanCollection(
        loanCollectionID: String
    ): LoanCollection {
        return Crud.find {
            loanCollectionRepository.findById(loanCollectionID)
        }
    }

    fun deleteLoanCollection(
        collectionId: String
    ) {
        Crud.find {
            loanCollectionRepository.findById(collectionId)
        }.apply {
            loanCollectionRepository.deleteById(id)
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

    fun updateClient(
        clientId: String,
        payload: Map<String, String>
    ): Client {
        val client = Crud.find {
            clientRepository.findById(clientId)
        }

        val errors = mutableMapOf<String, String>()

        payload.keys.filter { CLIENT_UPDATE_FIELDS.contains(it) }.forEach { key ->
            try {
                when(key) {
                    "name" -> payload["name"]?.let { name ->
                        if(StringUtils.hasText(name)) {
                            client.name = name
                        } else {
                            errors["name"] = "name cannot be blank"
                        }
                    }
                    "email" -> payload["email"]?.let { email ->
                        if(email.isValidEmail()) {
                            client.email = email
                        } else {
                            errors["name"] = "invalid email address"
                        }
                    }
                    "address" -> payload["address"]?.let { address ->
                        if(StringUtils.hasText(address)) {
                            client.address = address
                        } else {
                            errors["name"] = "address cannot be blank"
                        }
                    }
                }
            } catch (ex: IllegalArgumentException) {
                errors
            }
        }

        if(errors.isNotEmpty()) {
            throw EntryVenturesException(
                serverStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                errorDescription = { errors },
                errorMessage = { "Error processing client details" }
            )
        }

        clientRepository.save(client)
        return client
    }

    fun deleteClient(
        clientID: String
    ) {
        Crud.find {
            clientRepository.findById(clientID)
        }.apply {
            clientRepository.deleteById(id)
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