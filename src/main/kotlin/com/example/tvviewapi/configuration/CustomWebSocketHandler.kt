package com.example.tvviewapi.configuration

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList

@Component
class CustomWebSocketHandler : TextWebSocketHandler() {
      private val sessions = CopyOnWriteArrayList<WebSocketSession>()

      override fun afterConnectionEstablished(session: WebSocketSession) {
            sessions.add(session)
      }

      override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
            sessions.remove(session)
      }

      fun sendMessageToAll(message: String) {
            for (session in sessions) {
                  try {
                        if (session.isOpen) {
                              session.sendMessage(TextMessage(message))
                        }
                  } catch (e: IOException) {
                        e.printStackTrace()
                  }
            }
      }
}