package com.example.tvviewapi.service

import com.example.tvviewapi.configuration.CustomWebSocketHandler
import org.springframework.stereotype.Service

@Service
class WebSocketService(private val webSocketHandler: CustomWebSocketHandler) {
      fun sendSignalToAllClients() {
            println("Socket service sending message to all clients")
            webSocketHandler.sendMessageToAll("refresh")
      }
}