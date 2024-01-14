package com.entryventures.repository

import com.entryventures.models.jpa.LoanCollection
import org.springframework.data.jpa.repository.JpaRepository

interface LoanCollectionRepository: JpaRepository<LoanCollection, String> {
}