package com.example.tvviewapi.service

import com.example.tvviewapi.dto.PlannedOpeningDto
import org.springframework.stereotype.Service

@Service
class HighwayBridgeOpeningService(
      val jsonService: JsonService
) {
      val url = "https://api.sodertalje.se/getBridgeplanned"

      fun getBridgeOpenings(): List<PlannedOpeningDto> {
            val json = jsonService.fetch(url)
            return jsonService.parse<List<PlannedOpeningDto>>(json)
      }

}