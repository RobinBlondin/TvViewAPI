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

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userService: UserService,
    private val customOAuth2SuccessHandler: CustomOAuth2SuccessHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/users/test"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login{
                it.successHandler(customOAuth2SuccessHandler)
            }

            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter { token ->
                        val claims = token.claims
                        val email = claims["email"] as? String

                        println("Email: $email")

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
}
