package com.example.tvviewapi.dto

import java.time.LocalDateTime
import java.util.*

data class TvReminderDto(
    var id: UUID? = null,
    var description: String = "",
    var expiryDate: LocalDateTime = LocalDateTime.now().plusDays(1)
    )
