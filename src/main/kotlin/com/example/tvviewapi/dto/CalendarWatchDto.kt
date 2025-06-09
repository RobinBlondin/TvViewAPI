package com.example.tvviewapi.dto

import java.util.UUID

data class CalendarWatchDto(
      var id: UUID? = null,
      var channelId: String? = null,
      var resourceId: String? = null,
)
