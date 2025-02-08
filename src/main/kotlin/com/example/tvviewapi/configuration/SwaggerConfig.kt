package com.example.tvviewapi.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(Info().title("Google OAuth2 API").version("1.0"))
            .components(
                io.swagger.v3.oas.models.Components().addSecuritySchemes(
                    "google-oauth2",
                    SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(
                            OAuthFlows().authorizationCode(
                                OAuthFlow()
                                    .authorizationUrl("https://accounts.google.com/o/oauth2/auth") // Google Auth URL
                                    .tokenUrl("https://oauth2.googleapis.com/token") // Google Token URL
                                    .scopes(
                                        Scopes()
                                            .addString("openid", "OpenID Connect scope")
                                            .addString("email", "Access user email")
                                            .addString("profile", "Access user profile information")
                                    )
                            )
                        )
                )
            )
            .addSecurityItem(SecurityRequirement().addList("google-oauth2"))
    }
}
