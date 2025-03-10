package com.example.tvviewapi.configuration

import com.example.tvviewapi.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SecurityConfig(
      private val userService: UserService
) {

      @Bean
      fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
            http
                  .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
                  .csrf { csrf -> csrf.ignoringRequestMatchers("/auth/google") }
                  .authorizeHttpRequests { auth ->
                        auth
                              .requestMatchers("/auth/google", "/uploads/**").permitAll()
                              .anyRequest().authenticated()
                  }
                  .oauth2ResourceServer { oauth2 ->
                        oauth2.jwt { jwt ->
                              jwt.jwtAuthenticationConverter { token ->
                                    val claims = token.claims
                                    val email = claims["email"] as? String

                                    if (email != null && userService.isRegisteredUser(email)) {
                                          UsernamePasswordAuthenticationToken(
                                                email,
                                                token,
                                                listOf(SimpleGrantedAuthority("USER"))
                                          )
                                    } else {
                                          throw UsernameNotFoundException("Unauthorized user")
                                    }
                              }
                        }
                  }
                  .build()

      @Bean
      fun jwtDecoder(): JwtDecoder {
            return NimbusJwtDecoder.withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs").build()
      }

      @Bean
      fun corsConfigurationSource(): CorsConfigurationSource {
            val configuration = CorsConfiguration()
            configuration.allowedOrigins = listOf("http://localhost:5173")
            configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            configuration.allowedHeaders = listOf("*")
            configuration.allowCredentials = true

            val source: UrlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
            source.registerCorsConfiguration("/**", configuration)
            return source
      }

}
