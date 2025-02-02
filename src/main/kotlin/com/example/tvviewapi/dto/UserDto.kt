package com.example.tvviewapi.dto

import java.util.UUID

data class UserDto(
    private var id: UUID? = null,
    private var email: String = "",
    private var displayName: String = ""
)