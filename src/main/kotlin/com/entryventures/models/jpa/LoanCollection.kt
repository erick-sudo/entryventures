package com.entryventures.models.jpa

import com.entryventures.models.PaymentMethod
import com.entryventures.models.dto.LoanCollectionDto
import com.entryventures.repository.LoanRepository
import com.entryventures.services.Crud
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.GenericGenerator
import java.util.Date

@Entity
@Table(name = "loan_collections")
class LoanCollection() {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    @JsonIgnore
    lateinit var loan: Loan

    @Column(name = "collection_date", nullable = false)
    @JsonProperty("collection_date")
    var collectionDate: Date = Date()

    @Column(nullable = false)
    var amount: Float = 0f

    @Column(name = "payment_method", nullable = false)
    @JsonProperty("payment_method")
    var paymentMethod: PaymentMethod = PaymentMethod.Cash

    constructor(loanCollectionDto: LoanCollectionDto, loanRepository: LoanRepository): this() {
        id = loanCollectionDto.id
        loan = Crud.find {
            loanRepository.findById(loanCollectionDto.loanId)
        }
        collectionDate = loanCollectionDto.collectionDate
        amount = loanCollectionDto.amount
        paymentMethod = loanCollectionDto.paymentMethod
    }

    fun toLoanCollectionDto(): LoanCollectionDto {
        return LoanCollectionDto(
            id = id,
            loanId = loan.id,
            collectionDate = collectionDate,
            amount = amount,
            paymentMethod = paymentMethod
        )
    }
}