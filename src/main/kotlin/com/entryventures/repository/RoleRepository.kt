package com.entryventures.repository

import com.entryventures.models.jpa.Role
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, String> {
    @Transactional
    fun findByName(name: String): Role?
}