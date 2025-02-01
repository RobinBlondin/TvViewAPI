package com.example.tvviewapi.entity

import jakarta.persistence.*
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDateTime
import java.util.*

@MappedSuperclass
abstract class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private val id: UUID = UUID.randomUUID(),
    private var created: LocalDateTime = LocalDateTime.now(),
    private var createdBy: String = "System",
    private var updated: LocalDateTime = LocalDateTime.now(),
    private var updatedBy: String = "System"
) {
    @PrePersist
    fun onCreate() {
        this.created = LocalDateTime.now()
        createdBy = getLoggedInUser() ?: "System"
        updated = LocalDateTime.now()
        updatedBy = getLoggedInUser() ?: "System"
    }

    @PreUpdate
    fun onUpdate() {
        updated = LocalDateTime.now()
        updatedBy = getLoggedInUser() ?: "System"
    }

    private fun getLoggedInUser(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication != null && authentication.isAuthenticated) {
            authentication.name
        } else {
            null
        }
    }
}
