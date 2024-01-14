package com.entryventures.repository

import com.entryventures.models.jpa.Group
import com.entryventures.models.jpa.Role
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<Group, String> {
    @Transactional
    fun findByName(name: String): Group?
}