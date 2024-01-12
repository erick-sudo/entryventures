package com.entryventures.models.jpa

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.hibernate.annotations.GenericGenerator

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    lateinit var id: String;

    @Column(nullable = false)
    lateinit var firstName: String

    lateinit var middleName: String

    @Column(nullable = false)
    lateinit var lastName: String

    @Column(nullable = false)
    lateinit var userName: String

    @Column(nullable = false)
    lateinit var email: String

    @Column(nullable = false)
    @JsonIgnore
    lateinit var passwordDigest: String
}