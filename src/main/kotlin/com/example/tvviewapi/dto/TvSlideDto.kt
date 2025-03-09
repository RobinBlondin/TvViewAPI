package com.example.tvviewapi.dto

import java.util.*

data class TvSlideDto(
    var id: UUID? = null,
    var url: String = "",
    var created: Date? = null,
    var createdBy: String? = null
)

