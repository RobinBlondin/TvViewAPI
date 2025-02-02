package com.example.tvviewapi.entity

import java.time.LocalDateTime

class TvReminder(
    private val title: String,
    private val description: String,
    private val expiryDate: LocalDateTime
): BaseEntity()