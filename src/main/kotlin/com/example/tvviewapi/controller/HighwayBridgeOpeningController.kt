package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.PlannedOpeningDto
import com.example.tvviewapi.service.HighwayBridgeOpeningService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/highway-bridge-opening")
class HighwayBridgeOpeningController(
      val service: HighwayBridgeOpeningService
) {
      @GetMapping("all")
      fun getAllBridgeOpenings():ResponseEntity<List<PlannedOpeningDto>> = ResponseEntity.ok(service.getBridgeOpenings())

}