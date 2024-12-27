package com.example.weather_app.controller;

import com.example.weather_app.dto.WeatherSummaryDTO;
import com.example.weather_app.service.impl.WeatherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    @Autowired
    private WeatherServiceImpl weatherService;

    @GetMapping("/weather")
    public ResponseEntity<WeatherSummaryDTO> getWeatherByCity(@RequestParam String city) {
        return ResponseEntity.ok(weatherService.getWeatherData(city).block());
    }

}
