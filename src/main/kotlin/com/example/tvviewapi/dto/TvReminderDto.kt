package com.example.tvviewapi.dto

import java.util.*

data class TvReminderDto(
    var id: UUID? = null,
    var description: String = "",
    var done: Boolean = false
    )
