package com.example.tvviewapi.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class WebSocketService(
      private val messagingTemplate: SimpMessagingTemplate
) {

      fun sendRefreshSignal() {
            messagingTemplate.convertAndSend("/topic/refresh-slides", "refresh")
      }
}