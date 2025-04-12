package com.example.tvviewapi.dto

import kotlinx.serialization.Serializable

@Serializable
data class HighwayBridgeOpenings(
      val plannedOpenings: List<PlannedOpeningDto>
)
