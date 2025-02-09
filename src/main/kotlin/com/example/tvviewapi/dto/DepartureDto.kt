package com.example.tvviewapi.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DepartureDto(
      @SerialName("name")
      var name: String  = "",
      @SerialName("time")
      var time: String = "",
      @SerialName("date")
      var date: String = "",
      @SerialName("direction")
      var direction: String = ""
)