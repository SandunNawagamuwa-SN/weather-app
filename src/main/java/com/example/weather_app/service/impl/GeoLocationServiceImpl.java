package com.example.weather_app.service.impl;

import com.example.weather_app.apiResponse.GeoLocationResponse;
import com.example.weather_app.service.GeoLocationService;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GeoLocationServiceImpl implements GeoLocationService {

    private final WebClient webClient;

    public GeoLocationServiceImpl(WebClient geoLocationWebClient){
        this.webClient = geoLocationWebClient;
    }

    @Override
    public Mono<GeoLocationResponse> getGeoLocation(String city) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("name", city)
                        .queryParam("count", "1")
                        .queryParam("language", "en")
                        .queryParam("format", "json")
                        .build()).retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    // Handle 4xx errors
                    return Mono.error(new RuntimeException("Client error: " + clientResponse.statusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    // Handle 5xx errors
                    return Mono.error(new RuntimeException("Server error: " + clientResponse.statusCode()));
                })
                .bodyToMono(GeoLocationResponse.class)
                .flatMap(response -> {
                    if (response == null || response.results == null) {
                        // Propagate error if the response is invalid or empty
                        return Mono.error(new RuntimeException("Invalid or empty geo location data received."));
                    }
                    return Mono.just(response); // Return valid response
                })
                .doOnError(error -> {
                    // Handle error response
                    System.err.println("Error: " + error.getMessage());
                });
    }
}

