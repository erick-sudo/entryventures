package com.entryventures.repository

import com.entryventures.models.jpa.Loan
import org.springframework.data.jpa.repository.JpaRepository

interface LoanRepository : JpaRepository<Loan, String> {
}