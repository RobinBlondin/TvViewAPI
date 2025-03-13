package com.example.tvviewapi.dto

import java.util.*

data class TvReminderReceiverDto(
      var id: UUID? = null,
      var description: String = "",
      var expiryDate: String = ""
)
