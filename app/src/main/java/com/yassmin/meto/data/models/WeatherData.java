package com.yassmin.meto.data.models;

import java.io.Serializable;
import java.util.List;

public class WeatherData implements Serializable {
    private final String cityName;
    private final String weatherCondition;
    private final int temperature;
    private final int tempMin;
    private final int tempMax;
    private final int pressure;
    private final double windSpeed;
    private final int humidity;
    private final List<ForecastData> forecast;

    public WeatherData(String cityName, String weatherCondition, int temperature,
                       int tempMin, int tempMax, int pressure,
                       double windSpeed, int humidity, List<ForecastData> forecast) {
        this.cityName = cityName;
        this.weatherCondition = weatherCondition;
        this.temperature = temperature;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.forecast = forecast;
    }

    // Getters
    public String getCityName() { return cityName; }
    public String getWeatherCondition() { return weatherCondition; }
    public int getTemperature() { return temperature; }
    public int getTempMin() { return tempMin; }
    public int getTempMax() { return tempMax; }
    public int getPressure() { return pressure; }
    public double getWindSpeed() { return windSpeed; }
    public int getHumidity() { return humidity; }
    public List<ForecastData> getForecast() { return forecast; }
}