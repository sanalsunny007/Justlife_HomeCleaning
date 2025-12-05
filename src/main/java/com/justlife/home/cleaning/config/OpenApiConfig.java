package com.justlife.home.cleaning.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration for the Justlife Home Cleaning Booking API.
 *
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI justlifeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Justlife Home Cleaning Booking API")
                        .description("Automatically generated API documentation for the Justlife Home Cleaning Booking system.")
                        .version("1.0.0")
                        .contact(new Contact().name("Sanal Sunny")));
    }
}
