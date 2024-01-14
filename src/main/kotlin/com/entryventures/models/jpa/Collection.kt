package com.entryventures.models.jpa

import com.entryventures.models.PaymentMethod
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.GenericGenerator
import java.util.Date

@Entity
@Table(name = "loan_collections")
class Collection {
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
    var amount: Int = 0

    @Column(name = "payment_method", nullable = false)
    @JsonProperty("payment_method")
    var paymentMethod: PaymentMethod = PaymentMethod.Cash
}