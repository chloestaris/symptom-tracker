package com.healthtracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server()
                .url("http://localhost:8081")
                .description("Development server");

        Contact contact = new Contact()
                .name("Health Tracker Team")
                .email("support@healthtacker.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Health Symptom Tracker API")
                .version("1.0.0")
                .contact(contact)
                .description("This API exposes endpoints to manage symptoms, medications, and user profiles. Authentication is handled through GitHub OAuth.")
                .license(mitLicense);

        // Define OAuth2 security scheme
        SecurityScheme oauth2Scheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                                .authorizationUrl("/oauth2/authorization/github")
                                .tokenUrl("/login/oauth2/code/github")));

        // Add security scheme to components
        Components components = new Components()
                .addSecuritySchemes("oauth2", oauth2Scheme);

        // Add global security requirement
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("oauth2");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer))
                .components(components)
                .addSecurityItem(securityRequirement);
    }
} 