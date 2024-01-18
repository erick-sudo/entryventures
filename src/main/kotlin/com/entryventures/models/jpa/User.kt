package com.entryventures.models.jpa

import com.entryventures.apis.mpesa.RegisteredMpesaClient
import com.entryventures.security.PasswordService
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.GenericGenerator
import org.springframework.security.core.GrantedAuthority

@Entity
@Table(name = "_user")
class User() : RegisteredMpesaClient {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String

    @Column(nullable = false)
    @NotNull
    lateinit var firstName: String

    @Column(nullable = true)
    @NotNull
    lateinit var middleName: String

    @Column(nullable = false)
    @NotNull
    lateinit var lastName: String

    @Column(nullable = false)
    @NotNull
    lateinit var userName: String

    @Column(nullable = false)
    @NotNull
    lateinit var email: String

    @Column(nullable = false)
    @NotNull
    override lateinit var phone: String

    /**
     * Secondary constructor
     * @param firstName First name of the user
     * @param middleName Middle name of the user
     * @param lastName Last name of the user
     * @param userName User's username
     * @param email User's email address
     */
    constructor(
        firstName: String,
        middleName: String,
        lastName: String,
        userName: String,
        email: String
    ) : this() {
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.userName = userName
        this.email = email
    }

    @Column(nullable = false)
    @JsonIgnore
    lateinit var passwordDigest: String

    // Many-to-Many relationship with Role
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    private var roles: MutableList<Role> = mutableListOf();


    // Many-to-Many relationship with Group
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_groups",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "group_id")]
    )
    private var groups: MutableList<Group> = mutableListOf()

    fun getRoles() = roles
    fun getGroups() = groups


    /**
     * Get users authorities
     * Concatenates the roles and groups into a unique set
     */
    val authorities: List<GrantedAuthority>
        get() = roles + groups

    fun addRole(role: Role) {
        roles.add(role);
    }

    fun addGroup(group: Group) {
        groups.add(group);
    }

    fun authenticate(password: String): Boolean {
        return PasswordService.verifyPassword(password, this.passwordDigest);
    }
}