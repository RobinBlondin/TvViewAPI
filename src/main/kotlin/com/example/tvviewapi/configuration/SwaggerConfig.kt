package com.example.tvviewapi.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .addServersItem(Server().url("http://localhost:8080").description("Local Server"))
            .info(
                Info()
                    .title("API Documentation")
                    .description("Description of the API")
                    .version("1.0")
            )
    }
}
