package com.example.tvviewapi.service

import com.example.tvviewapi.dto.DepartureBoard
import com.example.tvviewapi.dto.DepartureDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CommuteService(
      val jsonService: JsonService,
      @Value("\${COMMUTE_STOP_ID}") val commuteStopId: String?,
      @Value("\${COMMUTE_API_KEY}") val commuteApiKey: String?
) {


      fun getDepartures(): List<DepartureDto> {
            val json = jsonService.fetch("https://api.resrobot.se/v2.1/departureBoard?id=${commuteStopId}&format=json&accessId=${commuteApiKey}")
            return  jsonService.parse<DepartureBoard>(json).departures
      }

}