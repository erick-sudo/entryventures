package com.entryventures.models.jpa

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = "disbursement_schedules")
class LoanDisbursementSchedule {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String

    @Column(name = "processed", nullable = false)
    var processed: Boolean = false

    // A disbursement schedule belongs to a Loan
    @OneToOne
    @JoinColumn(name = "loan_id")
    lateinit var loan: Loan
}