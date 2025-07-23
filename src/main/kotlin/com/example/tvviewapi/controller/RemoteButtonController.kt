package com.example.tvviewapi.controller

import com.example.tvviewapi.enums.SocketMessage
import com.example.tvviewapi.service.WebSocketService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/remote")
class RemoteButtonController(
      val webSocketService: WebSocketService
) {

      @GetMapping("{signal}")
      fun sendSignal(@PathVariable signal: SocketMessage): ResponseEntity<String> {
            webSocketService.sendSignalToAllClients(signal)
            return ResponseEntity.ok().body("Signal sent: ${signal.name}")
      }
}