package com.example.tvviewapi.configuration

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException

@Component
class CustomWebSocketHandler : TextWebSocketHandler() {
      private val sessions = mutableListOf<WebSocketSession>()

      override fun afterConnectionEstablished(session: WebSocketSession) {
            println("Connection established with session: $session")

            sessions.add(session)
            println("sessions: ${sessions.size}")
      }

      override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
            println("Connection closed with session: $session")
            sessions.remove(session)
      }

      fun sendMessageToAll(message: String) {
            println("Sending message to all sessions, size: ${sessions.size}")
            for (session in sessions) {
                  println("Sending message to session: $session")
                  try {
                        if (session.isOpen) {
                              println("Session is open")
                              session.sendMessage(TextMessage(message))
                        }
                  } catch (e: IOException) {
                        e.printStackTrace()
                  }
            }
      }
}