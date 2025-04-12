package com.example.tvviewapi.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlannedOpeningDto(
      @SerialName("Alias")
      val alias: String,
      @SerialName("Result")
      val result: String,
      @SerialName("StartTime")
      val startTime: String,
      @SerialName("EndTime")
      val endTime: String,
      @SerialName("MsgId")
      val msgId: String
)