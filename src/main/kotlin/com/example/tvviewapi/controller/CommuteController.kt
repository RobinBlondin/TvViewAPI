package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.DepartureDto
import com.example.tvviewapi.service.CommuteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/commute")
class CommuteController(
      val service: CommuteService
) {

      @GetMapping("bus")
      fun getBusDepartures(): ResponseEntity<List<DepartureDto>> {
            val departures = service.getDepartures().filter { it.name.contains("buss", true) }
            return ResponseEntity.ok().body(departures)
      }

      @GetMapping("train")
      fun getTrainDepartures(): ResponseEntity<List<DepartureDto>> {
            val departures = service.getDepartures().filter { it.name.contains("tåg", true) }
            return ResponseEntity.ok().body(departures)
      }

      @GetMapping("line/{line}")
      fun getDepartureByLine(@PathVariable line: String): ResponseEntity<List<DepartureDto>> {
            val departures = service.getDepartures().filter { it.name.contains(line,  true) }
            return ResponseEntity.ok().body(departures)
      }

      @GetMapping("direction/{direction}")
      fun getDepartureByDirection(@PathVariable direction: String): ResponseEntity<List<DepartureDto>> {
            val departures = service.getDepartures().filter { it.direction.contains(direction,  true) }
            return ResponseEntity.ok().body(departures)
      }

      @GetMapping("line-direction/{line}/{direction}")
      fun getDepartureByLineAndDirection(
            @PathVariable line: String,
            @PathVariable direction: String
      ): ResponseEntity<List<DepartureDto>> {
            val departures = service.getDepartures().filter { it.name.contains(line,  true) && it.direction.contains(direction,  true)}
            return ResponseEntity.ok().body(departures)
      }
}