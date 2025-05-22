package com.example.tvviewapi.dto

import java.util.UUID

data class UserDto(
    var id: UUID? = null,
    var email: String = "",
    var displayName: String = "",
    var refreshToken: String = ""
)