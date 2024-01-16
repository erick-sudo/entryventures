package com.entryventures.repository

import com.entryventures.models.jpa.LoanDisbursementSchedule
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LoanDisbursementScheduleRepository: JpaRepository<LoanDisbursementSchedule, String> {

    @Query("SELECT ld FROM LoanDisbursementSchedule ld WHERE ld.processed = false")
    fun findUnprocessedLoanDisbursementSchedules(pageable: Pageable): List<LoanDisbursementSchedule>
}