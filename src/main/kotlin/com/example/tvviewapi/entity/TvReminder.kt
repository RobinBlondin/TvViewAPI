package com.example.tvviewapi.entity

import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
class TvReminder(
    var title: String = "",
    var description: String = "",
    val expiryDate: LocalDateTime = LocalDateTime.now().plusDays(1)
): BaseEntity()