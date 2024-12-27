package com.example.weather_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${geolocation.api.base-url}")
    private String geo_location_baseUrl;

    @Value("${weather.api.base-url}")
    private String weather_baseUrl;

    @Bean
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }

    @Bean
    public WebClient geoLocationWebClient(){
        return webClientBuilder()
                .baseUrl(geo_location_baseUrl)
                .build();
    }

    @Bean
    public WebClient weatherWebClient(){
        return webClientBuilder()
                .baseUrl(weather_baseUrl)
                .build();
    }

}
