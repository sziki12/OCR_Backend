package app.ocr_backend.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Value

@Configuration
class SwaggerConfig {

    @Value("\${api.version}")
    private lateinit var apiVersion: String

    @Value("\${api.name}")
    private lateinit var apiName: String

    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("public-apis")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun customOpenApi(): OpenAPI {
        return OpenAPI()
            .info(Info().title(apiName).version(apiVersion))
            .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearerAuth", SecurityScheme()
                            .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                    )
            )
    }
}