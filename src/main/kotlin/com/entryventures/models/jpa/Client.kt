package com.entryventures.models.jpa

import com.entryventures.extensions.isValidEmail
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = "clients")
class Client  {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String

    // A client has one loan
    // One-to-one relationship with Loan
    @OneToOne(mappedBy = "client")
    @JsonIgnore
    lateinit var loan: Loan

    @Column(name = "name", nullable = false)
    @JsonProperty("name")
    @NotBlank
    lateinit var name: String

    @Column(name = "email", nullable = false, unique = true)
    @JsonProperty("email")
    @NotBlank
    @Email
    lateinit var email: String

    @Column(name = "address", nullable = false)
    @JsonProperty("address")
    @NotBlank
    lateinit var address: String

    @Column(name = "phone", nullable = false)
    @JsonProperty("phone")
    @NotNull
    lateinit var phone: String
}