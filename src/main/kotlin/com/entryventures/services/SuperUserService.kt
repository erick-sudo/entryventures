package com.entryventures.services

import com.entryventures.models.jpa.Group
import com.entryventures.models.jpa.Role
import com.entryventures.models.jpa.User
import com.entryventures.repository.GroupRepository
import com.entryventures.repository.RoleRepository
import com.entryventures.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class SuperUserService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val roleRepository: RoleRepository,
    private val controllerService: ControllerService
) {

    @Bean
    fun initializeSuperUser() {
        val superUser = userRepository.findByUserName("johndoe")

        val roles = listOf(
                Role(name = "ROLE_LOAN_OFFICER", description = "Asses and approve loan applications"),
                Role(name = "ROLE_DISBURSEMENT_OFFICER", description = "Authorize to disburse approved loans"),
                Role(name = "ROLE_COLLECTION_OFFICER", description = "Manage loan collections based on schedule"),
                Role(name = "ROLE_SYSTEM_ADMINISTRATOR", description = "User management, system configuration and maintenance"),
                Role(name = "ROLE_MANAGER", description = "Oversee overall loan management process")
        )

        roles.filter { roleRepository.findByName(it.name) == null }.forEach { role ->
            roleRepository.save(role)
            println("${role.id} ${role.name}")
        }

        if(superUser == null) {

            val superUser = User(
                "John",
                "Doe",
                "Smith",
                "johndoe",
                "johndoe@example.com",
                "706087204"
            )

            if (roleRepository.findByName("ROLE_ADMIN") == null) {
                val adminRole = Role("ROLE_ADMIN", "Super user")
                roleRepository.save(adminRole)
                superUser.addRole(adminRole)
            }

            if (groupRepository.findByName("GROUP_ADMIN") == null) {
                val adminGroup = Group("GROUP_ADMIN", "Super users")
                groupRepository.save(adminGroup)
                superUser.addGroup(adminGroup)
            }

            controllerService.saveUserWithPassword(superUser, "password")
        }
    }
}