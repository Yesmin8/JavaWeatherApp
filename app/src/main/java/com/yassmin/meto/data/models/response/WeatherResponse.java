package com.yassmin.meto.data.models.response;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("name")
    public String cityName;

    @SerializedName("weather")
    public Weather[] weather;

    @SerializedName("main")
    public Main main;

    @SerializedName("wind")
    public Wind wind;

    public static class Weather {
        @SerializedName("description")
        public String description;
    }

    public static class Main {
        @SerializedName("temp")
        public float temp;
        @SerializedName("temp_min")
        public float tempMin;
        @SerializedName("temp_max")
        public float tempMax;
        @SerializedName("pressure")
        public int pressure;
        @SerializedName("humidity")
        public int humidity;
    }

    public static class Wind {
        @SerializedName("speed")
        public double speed;
    }
}
