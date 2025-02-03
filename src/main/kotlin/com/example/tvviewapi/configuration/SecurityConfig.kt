package com.example.tvviewapi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/users/**").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login {

            }
            .build()
}
