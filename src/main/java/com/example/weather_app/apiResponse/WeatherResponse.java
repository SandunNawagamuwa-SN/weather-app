package com.example.weather_app.apiResponse;

import com.example.weather_app.pojo.Daily;
import com.example.weather_app.pojo.DailyUnits;

public class WeatherResponse {
    public double latitude;
    public double longitude;
    public double generationtime_ms;
    public int utc_offset_seconds;
    public String timezone;
    public String timezone_abbreviation;
    public double elevation;
    public DailyUnits daily_units;
    public Daily daily;
}
