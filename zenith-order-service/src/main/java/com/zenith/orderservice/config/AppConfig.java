package com.zenith.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public RestClient storeRestClient(@Value("${store-service.url:http://localhost:8082}") String storeUrl) {
        return RestClient.builder().baseUrl(storeUrl).build();
    }

    @Bean
    public RestClient inventoryRestClient(@Value("${inventory-service.url:http://localhost:8084}") String inventoryUrl) {
        return RestClient.builder().baseUrl(inventoryUrl).build();
    }
}
