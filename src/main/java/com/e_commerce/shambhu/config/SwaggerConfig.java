package in.gov.egramswaraj.user_service.config;

import java.util.Arrays;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swaggerCustomConfig() {
        return new OpenAPI().info(
                        new Info().title("eGramSwaraj 2.0 APIs")
                ).servers(Arrays.asList(new Server().url("http://localhost:9092/demo-reports/user"),
                        new Server().url("https://egramswaraj.gov.in/demo-reports/user")))
                .components(new Components().addSecuritySchemes(
                        "bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name(HttpHeaders.AUTHORIZATION)
                )).addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
