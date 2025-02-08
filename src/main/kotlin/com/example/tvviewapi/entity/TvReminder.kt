package com.example.tvviewapi.entity

import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
class TvReminder(
    var title: String = "",
    var description: String = "",
    var startDate: LocalDateTime = LocalDateTime.now(),
    val expiryDate: LocalDateTime = startDate.plusDays(1)
): BaseEntity()