package com.entryventures.models.jpa;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "groups")
@NoArgsConstructor
class Group(name: String, description: String) : GrantedAuthority {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2",  strategy = "org.hibernate.id.UUIDGenerator" )
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String;

    @Column(name = "name", unique = true)
    @JsonProperty("name")
    @NotBlank
    var name: String

    @JsonProperty("description")
    @NotBlank
    var description: String

    // Many-to-Many relationship with Roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "group_roles",
            joinColumns = [JoinColumn(name = "group_id", columnDefinition = "varchar")],
            inverseJoinColumns = [JoinColumn(name = "role_id", columnDefinition = "varchar")]
    )
    @JsonProperty("roles")
    private var roles = mutableListOf<Role>()

    // Many-to-Many relationship with User
    @ManyToMany(mappedBy = "groups")
    private var groups = mutableListOf<Group>()

    init {
        this.name = name;
        this.description = description;
    }

    fun addRole(role: Role) = this.roles.add(role)

    fun getRoles(): List<Role> = roles.toList()

    fun containsRole(roleName: String): Boolean = this.roles.map { it.toString() }.contains(roleName)



    override fun getAuthority(): String = this.name

    override fun toString(): String = this.name
}
