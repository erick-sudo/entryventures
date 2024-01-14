package com.entryventures.repository

import com.entryventures.models.jpa.Client
import org.springframework.data.jpa.repository.JpaRepository

interface ClientRepository: JpaRepository<Client, String> {
}