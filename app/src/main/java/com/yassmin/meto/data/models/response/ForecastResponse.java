package com.yassmin.meto.data.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {
    @SerializedName("list")
    public List<Item> list;

    public static class Item {
        @SerializedName("dt_txt")
        public String date;

        @SerializedName("main")
        public Main main;

        @SerializedName("weather")
        public List<Weather> weather;
        @SerializedName("wind")
        public Wind wind;
    }

    public static class Main {
        @SerializedName("temp_min")
        public float tempMin;
        @SerializedName("temp_max")
        public float tempMax;
        @SerializedName("pressure")
        public int pressure;
        @SerializedName("humidity")
        public int humidity;
    }

    public static class Weather {
        @SerializedName("description")
        public String description;
    }

    public static class Wind {
        @SerializedName("speed")
        public double speed;
    }
}