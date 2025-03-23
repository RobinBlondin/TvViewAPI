package com.example.tvviewapi.service

import com.example.tvviewapi.configuration.CustomWebSocketHandler
import org.springframework.stereotype.Service

@Service
class WebSocketService(private val webSocketHandler: CustomWebSocketHandler) {
      fun sendSignalToAllClients() {
            webSocketHandler.sendMessageToAll("refresh")
      }
}