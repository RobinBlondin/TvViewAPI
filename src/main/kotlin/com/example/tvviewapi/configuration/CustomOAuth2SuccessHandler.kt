package com.example.tvviewapi.configuration

import com.example.tvviewapi.service.UserService
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Service
import java.io.IOException


@Service
class CustomOAuth2SuccessHandler(
      val userService: UserService
) : AuthenticationSuccessHandler {

      @Throws(IOException::class, ServletException::class)
      override fun onAuthenticationSuccess(
            request: HttpServletRequest?,
            response: HttpServletResponse,
            authentication: Authentication
      ) {
            val allowedEmails = userService.getUserEmails()
            val oidcUser = authentication.principal as OidcUser
            val email = oidcUser.email

            if (!allowedEmails.contains(email)) {
                  response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized email")
                  return
            }
            response.sendRedirect("/api/users/success")
      }
}