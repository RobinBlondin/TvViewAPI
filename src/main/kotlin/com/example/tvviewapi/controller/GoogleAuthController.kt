package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.GoogleAuthRequestDto
import com.example.tvviewapi.service.GoogleCalendarService
import com.example.tvviewapi.service.UserService
import io.github.cdimascio.dotenv.Dotenv
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.nio.charset.StandardCharsets
import java.util.*


@RestController
@RequestMapping("/auth")
class GoogleAuthController(
      private val userService: UserService,
      private val googleCalendarService: GoogleCalendarService
) {
      val dotenv: Dotenv? = Dotenv.configure().ignoreIfMissing().load()
      val secret: String? = dotenv?.get("JWT_SECRET")

      @PostMapping("/google")
      fun exchangeAuthCode(@RequestBody request: GoogleAuthRequestDto): ResponseEntity<Map<String, Any>> {
            val authCode = request.code
            val clientId = request.clientId
            val clientSecret = request.clientSecret
            val redirectUri = request.redirectUri

            val restTemplate = RestTemplate()

            // Step 1: Exchange auth code for access token (Google requires form-encoded data)
            val tokenParams = LinkedMultiValueMap<String, String>().apply {
                  add("client_id", clientId)
                  add("client_secret", clientSecret)
                  add("code", authCode)
                  add("grant_type", "authorization_code")
                  add("redirect_uri", redirectUri)
            }

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

            val tokenRequestEntity = HttpEntity(tokenParams, headers)

            val tokenResponse = restTemplate.exchange(
                  "https://oauth2.googleapis.com/token",
                  HttpMethod.POST,
                  tokenRequestEntity,
                  Map::class.java
            )

            if (!tokenResponse.statusCode.is2xxSuccessful) {
                  return ResponseEntity.status(tokenResponse.statusCode)
                        .body(mapOf("error" to "Failed to get access token"))
            }
            println("tokenResponse: ${tokenResponse.body}")
            val accessToken = tokenResponse.body?.get("access_token") as? String
                  ?: return ResponseEntity.badRequest().body(mapOf("error" to "Invalid token response"))

            val refreshToken = tokenResponse.body?.get("refresh_token") as? String

            val idToken = tokenResponse.body?.get("id_token") as? String
                  ?: return ResponseEntity.badRequest().body(mapOf("error" to "Invalid token response"))

            // Step 2: Use access token to get user info
            val userInfoHeaders = HttpHeaders()
            userInfoHeaders.setBearerAuth(accessToken)
            val userInfoRequestEntity = HttpEntity<Void>(userInfoHeaders)
            val userInfoResponse = restTemplate.exchange(
                  "https://www.googleapis.com/oauth2/v2/userinfo",
                  HttpMethod.GET,
                  userInfoRequestEntity,
                  Map::class.java
            )

            if (!userInfoResponse.statusCode.is2xxSuccessful) {
                  return ResponseEntity.status(userInfoResponse.statusCode)
                        .body(mapOf("error" to "Failed to fetch user info"))
            }

            val userInfo = userInfoResponse.body!!
            val email = userInfo["email"] as? String ?: return ResponseEntity.badRequest()
                  .body(mapOf("error" to "No email found"))
            val name = userInfo["name"] as? String ?: "Unknown"
            val picture = userInfo["picture"] as? String ?: ""

            // Step 3: Check if user exists
            if (!userService.isRegisteredUser(email)) {
                  return ResponseEntity.status(403).body(mapOf("error" to "Unauthorized user"))
            }

            val user = userService.findUserByEmail(email)
                  .orElseThrow { RuntimeException("User not found: $email") }

            if(user.refreshToken.isEmpty() || user.refreshToken.isBlank()) {
                  user.refreshToken = refreshToken ?: ""
            }

            val isTvViewRequest = request.isTvView
            val secretKey = Keys.hmacShaKeyFor(secret?.toByteArray(StandardCharsets.UTF_8))
            val tvToken = if (isTvViewRequest) Jwts.builder()
                  .subject(email)
                  .issuedAt(Date())
                  .expiration(Date(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000))
                  .claim("is_tv_token", true)
                  .signWith(secretKey, Jwts.SIG.HS512)
                  .compact() else ""

            googleCalendarService.startWatchingCalendar(accessToken)

            return ResponseEntity.ok(
                  mapOf(
                        "access_token" to accessToken,
                        "id_token" to idToken,
                        "email" to email,
                        "name" to name,
                        "picture" to picture,
                        "tv_token" to tvToken,
                  )
            )
      }
}
