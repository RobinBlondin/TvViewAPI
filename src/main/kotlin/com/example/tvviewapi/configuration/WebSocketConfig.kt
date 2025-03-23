package com.example.tvviewapi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(private val customWebSocketHandler: CustomWebSocketHandler) : WebSocketConfigurer {

      override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
            registry.addHandler(customWebSocketHandler, "/ws").setAllowedOrigins("*")
      }
}