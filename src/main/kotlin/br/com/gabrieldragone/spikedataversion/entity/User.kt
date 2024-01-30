package br.com.gabrieldragone.spikedataversion.entity

import jakarta.persistence.*

@Entity
@Table(name = "[users]")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    var email: String,

    @Version
    val version: Long = 1
)