package com.example.tvviewapi.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DepartureBoard(
      @SerialName("Departure")
      val departures: List<DepartureDto> = ArrayList()
)
