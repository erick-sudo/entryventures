package com.entryventures.repository

import com.entryventures.models.jpa.LoanCollection
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LoanCollectionRepository: JpaRepository<LoanCollection, String> {
    @Query("SELECT COUNT(lc) FROM LoanCollection lc WHERE lc.loan.id = :loanId")
    fun countLoanCollectionsByLoanId(@Param("loanId") loanId: String): Long

    @Query("SELECT lc FROM LoanCollection lc WHERE lc.loan.id = :loanId")
    fun findLoanCollectionsByLoan(loanId: String, pageable: Pageable): List<LoanCollection>
}