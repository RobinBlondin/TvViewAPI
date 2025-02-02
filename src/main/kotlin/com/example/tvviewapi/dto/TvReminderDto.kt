package com.example.tvviewapi.dto

import java.time.LocalDateTime
import java.util.*

data class TvReminderDto(
    private var id: UUID? = null,
    private var title: String = "",
    private var description: String = "",
    private var expiryDate: LocalDateTime = LocalDateTime.now().plusDays(1)
    )
