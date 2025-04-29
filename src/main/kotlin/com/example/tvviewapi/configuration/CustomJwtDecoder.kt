package com.example.tvviewapi.configuration

import io.github.cdimascio.dotenv.Dotenv
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
      private val googleJwtDecoder: NimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs").build()
      private val dotenv: Dotenv? = Dotenv.configure().ignoreIfMissing().load()
      private val secret: String? = dotenv?.get("JWT_SECRET")
      private val customSecret = secret?.toByteArray(StandardCharsets.UTF_8)
      private val customSecretKey = SecretKeySpec(customSecret, "HmacSHA512")

      private val customJwtDecoder: NimbusJwtDecoder = NimbusJwtDecoder
            .withSecretKey(customSecretKey)
            .macAlgorithm(MacAlgorithm.HS512)
            .build()

      override fun decode(token: String): Jwt {
            return try {
                  googleJwtDecoder.decode(token)
            } catch (ex: JwtException) {
                  println("Failed to decode JWT with Google decoder, trying custom decoder" + ex.message)
                  customJwtDecoder.decode(token)
            }
      }
}