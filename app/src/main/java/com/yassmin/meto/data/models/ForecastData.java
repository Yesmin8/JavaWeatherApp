package com.yassmin.meto.data.models;

import java.io.Serializable;

public final class ForecastData implements Serializable {
    private final String day;
    private final int tempMin;
    private final int tempMax;
    private final int pressure;
    private final double windSpeed;
    private final int humidity;
    private final String weatherCondition;

    public ForecastData(String day, int tempMin, int tempMax,
                        int pressure, double windSpeed, int humidity,
                        String weatherCondition) {
        this.day = day;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.weatherCondition = weatherCondition;
    }

    // Getters
    public String getDay() { return day; }
    public int getTempMin() { return tempMin; }
    public int getTempMax() { return tempMax; }
    public int getPressure() { return pressure; }
    public double getWindSpeed() { return windSpeed; }
    public int getHumidity() { return humidity; }
    public String getWeatherCondition() { return weatherCondition; }
}
