package com.example.tvviewapi.entity

import java.time.LocalDateTime

class TvReminder(
    private var title: String = "",
    private var description: String = "",
    private val expiryDate: LocalDateTime = LocalDateTime.now().plusDays(1)
): BaseEntity()