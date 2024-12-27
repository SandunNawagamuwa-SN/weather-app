package com.example.weather_app.service;

import com.example.weather_app.apiResponse.GeoLocationResponse;
import reactor.core.publisher.Mono;

public interface GeoLocationService {

    Mono<GeoLocationResponse> getGeoLocation(String city);

}
