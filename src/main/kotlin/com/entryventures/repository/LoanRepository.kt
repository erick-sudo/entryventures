package com.entryventures.repository

import com.entryventures.models.LoanStatus
import com.entryventures.models.jpa.Loan
import jakarta.transaction.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LoanRepository : JpaRepository<Loan, String> {

    @Transactional
    fun countByStatus(status: LoanStatus): Long

    @Query("SELECT l FROM Loan l WHERE l.status = :status")
    fun findLoanByStatus(@Param("status") status: LoanStatus, pageable: Pageable): List<Loan>
}