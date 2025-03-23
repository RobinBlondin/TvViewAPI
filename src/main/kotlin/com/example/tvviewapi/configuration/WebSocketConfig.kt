package com.example.tvviewapi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {

      override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
            registry.addHandler(myWebSocketHandler(), "/ws").setAllowedOrigins("*")
      }

      @Bean
      fun myWebSocketHandler(): WebSocketHandler {
            return CustomWebSocketHandler()
      }
}