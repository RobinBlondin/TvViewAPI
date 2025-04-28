package com.example.tvviewapi.configuration

import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

@Component
class CustomJwtDecoder : JwtDecoder {

      private val googleJwtDecoder: NimbusJwtDecoder =
            NimbusJwtDecoder.withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs").build()

      private val customSecret = "this-is-a-very-long-random-secret-key-for-hs512-testing-1234567890".toByteArray(
            StandardCharsets.UTF_8)

      private val customSecretKey = SecretKeySpec(customSecret, "HmacSHA512")
      private val customJwtDecoder: NimbusJwtDecoder = NimbusJwtDecoder
            .withSecretKey(customSecretKey)
            .macAlgorithm(MacAlgorithm.HS512)
            .build()

      override fun decode(token: String): Jwt {
            return try {
                  googleJwtDecoder.decode(token)
            } catch (ex: JwtException) {
                  customJwtDecoder.decode(token)
            }
      }
}