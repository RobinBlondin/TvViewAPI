package com.example.tvviewapi.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.time.LocalDateTime
import java.util.UUID

@Entity
@NoArgsConstructor
@AllArgsConstructor
class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private val id: UUID,
    private val created: LocalDateTime,
    private val createdBy: String,
    private val updated: LocalDateTime,
    private val updatedBy: String
)