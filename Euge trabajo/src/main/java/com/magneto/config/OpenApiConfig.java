package com.magneto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mutantDetectionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mutant Detection API")
                        .description("API for detecting mutant DNA sequences. " +
                                     "Developed for Magneto's mutant recruitment program.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Magneto")
                                .email("magneto@xmen.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
