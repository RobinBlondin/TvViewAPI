package com.example.tvviewapi.entity

import jakarta.persistence.Entity

@Entity
class TvReminder(
    var description: String = "",
    var done: Boolean = false
): BaseEntity()