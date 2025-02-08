package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.CalendarEventDto
import com.example.tvviewapi.service.GoogleCalendarService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/calendar")
class GoogleCalendarController(
      val service: GoogleCalendarService
) {
      @GetMapping("events")
      fun getCalendarEvents(): ResponseEntity<List<CalendarEventDto>> = ResponseEntity.ok().body(service.getCalendarEvents())
}