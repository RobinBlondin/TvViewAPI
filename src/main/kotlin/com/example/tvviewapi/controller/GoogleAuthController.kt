package com.example.tvviewapi.controller

import com.example.tvviewapi.service.UserService
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class GoogleAuthController(private val userService: UserService) {

      @PostMapping("/google")
      fun exchangeAuthCode(@RequestBody request: Map<String, String>): ResponseEntity<Map<String, Any>> {
            val authCode = request["code"] ?: return ResponseEntity.badRequest().body(mapOf("error" to "Authorization code is required"))
            val clientId = request["clientId"] ?: return ResponseEntity.badRequest().body(mapOf("error" to "Client ID is required"))
            val clientSecret = request["clientSecret"] ?: return ResponseEntity.badRequest().body(mapOf("error" to "Client Secret is required"))
            val redirectUri = request["redirectUri"] ?: return ResponseEntity.badRequest().body(mapOf("error" to "Redirect URI is required"))

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
                  return ResponseEntity.status(tokenResponse.statusCode).body(mapOf("error" to "Failed to get access token"))
            }

            val accessToken = tokenResponse.body?.get("access_token") as? String
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
                  return ResponseEntity.status(userInfoResponse.statusCode).body(mapOf("error" to "Failed to fetch user info"))
            }

            val userInfo = userInfoResponse.body!!
            val email = userInfo["email"] as? String ?: return ResponseEntity.badRequest().body(mapOf("error" to "No email found"))
            val name = userInfo["name"] as? String ?: "Unknown"
            val picture = userInfo["picture"] as? String ?: ""

            // Step 3: Check if user exists
            if (!userService.isRegisteredUser(email)) {
                  return ResponseEntity.status(403).body(mapOf("error" to "Unauthorized user"))
            }

            // Step 4: Return user info and access token if valid
            return ResponseEntity.ok(
                  mapOf(
                        "access_token" to accessToken,
                        "email" to email,
                        "name" to name,
                        "picture" to picture
                  )
            )
      }
}
