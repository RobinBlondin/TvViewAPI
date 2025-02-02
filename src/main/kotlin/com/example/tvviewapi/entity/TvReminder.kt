package com.example.tvviewapi.entity

import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
class TvReminder(
    private var title: String = "",
    private var description: String = "",
    private val expiryDate: LocalDateTime = LocalDateTime.now().plusDays(1)
): BaseEntity()