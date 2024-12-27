package com.example.weather_app.service.impl;

import com.example.weather_app.apiResponse.GeoLocationResponse;
import com.example.weather_app.apiResponse.WeatherResponse;
import com.example.weather_app.dto.WeatherSummaryDTO;
import com.example.weather_app.service.GeoLocationService;
import com.example.weather_app.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherServiceImpl implements WeatherService {

    private GeoLocationService geoLocationService;

    private final WebClient webClient;

    public WeatherServiceImpl(WebClient weatherWebClient, GeoLocationService geoLocationService){
        this.webClient = weatherWebClient;
        this.geoLocationService = geoLocationService;
    }

    @Cacheable(value = "weatherSummaryCache", key = "#city", sync = true)
    @Override
    public Mono<WeatherSummaryDTO> getWeatherData(String city) {
        System.out.println("getWeatherData called with - " + city);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        GeoLocationResponse geoLocationResponse = geoLocationService.getGeoLocation(city).block();
        Mono<WeatherResponse> weatherAPIResponseMono = webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/archive")
                        .queryParam("latitude", geoLocationResponse.results.getFirst().latitude)
                        .queryParam("longitude", geoLocationResponse.results.getFirst().longitude)
                        .queryParam("start_date", currentDate.minusDays(7).format(formatter))
                        .queryParam("end_date", currentDate.format(formatter))
                        .queryParam("daily", "temperature_2m_mean")
                        .queryParam("timezone", "auto")
                        .build()).retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return Mono.error(new RuntimeException("Client error: " + clientResponse.statusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    return Mono.error(new RuntimeException("Server error: " + clientResponse.statusCode()));
                })
                .bodyToMono(WeatherResponse.class)
                .flatMap(response -> {
                    if (response == null || response.daily == null) {
                        // Propagate error if the response is invalid or empty
                        return Mono.error(new RuntimeException("Invalid or empty weather data received."));
                    }
                    return Mono.just(response); // Return valid response
                })
                .doOnError(error -> {
                    System.err.println("Error: " + error.getMessage());
                });

        return weatherAPIResponseMono.map(weatherAPIResponse -> {

            double maxTemp = weatherAPIResponse.daily.temperature_2m_mean.getFirst();
            int maxTempIndex = 0;
            double minTemp = weatherAPIResponse.daily.temperature_2m_mean.getFirst();
            int minTempIndex = 0;
            double averageTemp = 0;
            int tempAvailCount = 0;

            for (int i = 0; i < weatherAPIResponse.daily.temperature_2m_mean.size(); i++) {
                if (weatherAPIResponse.daily.temperature_2m_mean.get(i) == null) continue;
                if (weatherAPIResponse.daily.temperature_2m_mean.get(i) > maxTemp) {
                    maxTemp = weatherAPIResponse.daily.temperature_2m_mean.get(i);
                    maxTempIndex = i;
                } else if (weatherAPIResponse.daily.temperature_2m_mean.get(i) < minTemp) {
                    minTemp = weatherAPIResponse.daily.temperature_2m_mean.get(i);
                    minTempIndex = i;
                }
                averageTemp += weatherAPIResponse.daily.temperature_2m_mean.get(i);
                tempAvailCount++;
            }
            if (tempAvailCount != 0) averageTemp = averageTemp / tempAvailCount;

            WeatherSummaryDTO summary = new WeatherSummaryDTO();
            summary.setCity(city);
            summary.setAverageTemperature(averageTemp);
            summary.setHottestDay(weatherAPIResponse.daily.time.get(maxTempIndex));
            summary.setColdestDay(weatherAPIResponse.daily.time.get(minTempIndex));
            return summary;
        });

    }

}

