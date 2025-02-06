package com.example.tvviewapi.entity

import jakarta.persistence.*
import lombok.Data
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDateTime
import java.util.*

@MappedSuperclass
abstract class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var created: LocalDateTime = LocalDateTime.now(),
    var createdBy: String = "System",
    var updated: LocalDateTime = LocalDateTime.now(),
    var updatedBy: String = "System"
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
