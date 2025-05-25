package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.CalendarEventDto
import com.example.tvviewapi.enums.SocketMessage
import com.example.tvviewapi.service.GoogleCalendarService
import com.example.tvviewapi.service.WebSocketService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/calendar")
class GoogleCalendarController(
      val service: GoogleCalendarService,
      val webSocketService: WebSocketService
) {
      @GetMapping("events")
      fun getCalendarEvents(): ResponseEntity<List<CalendarEventDto>> = ResponseEntity.ok().body(service.getCalendarEvents())

      @PostMapping("/notifications")
      fun receiveNotification(): ResponseEntity<Void> {
            println("Update received from Google Calendar")
            webSocketService.sendSignalToAllClients(SocketMessage.CALENDAR)
            return ResponseEntity.ok().build()
      }
}