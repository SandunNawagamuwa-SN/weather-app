package com.example.weather_app.dto;

public class WeatherSummaryDTO {

    private String city;
    private double averageTemperature;
    private String hottestDay;
    private String coldestDay;

    public WeatherSummaryDTO(String city, double averageTemperature, String hottestDay, String coldestDay) {
        this.city = city;
        this.averageTemperature = averageTemperature;
        this.hottestDay = hottestDay;
        this.coldestDay = coldestDay;
    }

    public WeatherSummaryDTO() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public String getHottestDay() {
        return hottestDay;
    }

    public void setHottestDay(String hottestDay) {
        this.hottestDay = hottestDay;
    }

    public String getColdestDay() {
        return coldestDay;
    }

    public void setColdestDay(String coldestDay) {
        this.coldestDay = coldestDay;
    }

}
