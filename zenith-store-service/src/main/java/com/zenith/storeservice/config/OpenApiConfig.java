package com.zenith.storeservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI storeServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Zenith Store Service API")
                        .version("0.0.1"));
    }
}
