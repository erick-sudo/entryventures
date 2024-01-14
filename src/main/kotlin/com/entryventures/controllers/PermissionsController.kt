package com.entryventures.controllers

import com.entryventures.repository.GroupRepository
import com.entryventures.repository.RoleRepository
import org.springframework.web.bind.annotation.RestController

@RestController
class PermissionsController(
    private val roleRepository: RoleRepository,
    private val groupRepository: GroupRepository
) {



}