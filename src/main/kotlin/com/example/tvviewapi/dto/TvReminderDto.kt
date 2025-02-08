package com.example.tvviewapi.dto

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class TvReminderDto(
    var id: UUID? = null,
    var title: String = "",
    var description: String = "",
    var startDate: LocalDateTime = LocalDateTime.now(),
    var expiryDate: LocalDateTime = startDate.plusDays(1)
    )
