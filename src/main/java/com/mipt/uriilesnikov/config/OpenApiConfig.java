package com.mipt.uriilesnikov.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * OpenAPI metadata configuration.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI todoOpenApi(@Value("${app.api.version}") String apiVersion) {
        return new OpenAPI().info(
                new Info()
                        .title("To-Do List API")
                        .version(apiVersion)
                        .description("REST API for task management with attachments, favorites, and preferences")
                        .contact(new Contact()
                                .name("To-Do List Team")
                                .email("todo-api@example.com"))
        );
    }
}
