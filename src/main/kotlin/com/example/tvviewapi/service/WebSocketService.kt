package com.example.tvviewapi.service

import com.example.tvviewapi.configuration.CustomWebSocketHandler
import com.example.tvviewapi.enums.SocketMessage
import org.springframework.stereotype.Service

@Service
class WebSocketService(private val webSocketHandler: CustomWebSocketHandler) {
      fun sendSignalToAllClients(message: SocketMessage) {
            webSocketHandler.sendMessageToAll(message)
      }
}