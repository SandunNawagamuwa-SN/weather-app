package com.example.weather_app.service;

import com.example.weather_app.dto.WeatherSummaryDTO;
import reactor.core.publisher.Mono;

public interface WeatherService {

    Mono<WeatherSummaryDTO> getWeatherData(String city);

}
