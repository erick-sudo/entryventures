package com.entryventures.controllers

import com.entryventures.repository.GroupRepository
import com.entryventures.repository.RoleRepository
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Resource Permissions", description = "Role Based Identity Access Management APIs")
@RestController
class PermissionsController(
    private val roleRepository: RoleRepository,
    private val groupRepository: GroupRepository
) {



}