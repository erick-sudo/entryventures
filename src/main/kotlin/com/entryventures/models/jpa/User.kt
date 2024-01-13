package com.entryventures.models.jpa

import com.entryventures.security.PasswordService
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.hibernate.annotations.GenericGenerator
import org.springframework.security.core.GrantedAuthority

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
class User() {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String;

    @Column(nullable = false)
    lateinit var firstName: String

    @Column(nullable = true)
    lateinit var middleName: String

    @Column(nullable = false)
    lateinit var lastName: String

    @Column(nullable = false)
    lateinit var userName: String

    @Column(nullable = false)
    lateinit var email: String

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