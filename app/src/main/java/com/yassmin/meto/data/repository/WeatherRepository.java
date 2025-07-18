package com.yassmin.meto.data.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yassmin.meto.api.ApiClient;
import com.yassmin.meto.api.WeatherApiService;
import com.yassmin.meto.data.models.WeatherData;
import com.yassmin.meto.data.models.ForecastData;
import com.yassmin.meto.data.models.response.WeatherResponse;
import com.yassmin.meto.data.models.response.ForecastResponse;
import com.yassmin.meto.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.jetbrains.annotations.NotNull;

public class WeatherRepository {
    private static final String TAG = "WeatherRepository";
    private static final String API_KEY = "884cce19cc5181c38a8ed0e01e6380a8";
    private final WeatherApiService apiService;
    private final Gson gson = new Gson();
    private final String lang;
    private final Context context;

    public WeatherRepository(Context context) {
        this.context = context;
        apiService = ApiClient.getClient().create(WeatherApiService.class);
        this.lang = Locale.getDefault().getLanguage();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        android.net.Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (capabilities == null) {
            return false;
        }

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
    }

    public void getWeatherByLocation(double lat, double lon, MutableLiveData<WeatherData> weatherData) {
        if (isNetworkAvailable()) {
            apiService.getCurrentWeatherByLocation(lat, lon, API_KEY, "metric", lang)
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                processWeatherResponse(response.body(), weatherData, lat, lon);
                            } else {
                                handleApiError(response.code(), "weather by location", weatherData);
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                            handleApiFailure(t, "weather by location", weatherData);
                        }
                    });
        } else {
            weatherData.postValue(null);
        }
    }

    public void getForecastByLocation(double lat, double lon, MutableLiveData<List<ForecastData>> forecastData) {
        if (isNetworkAvailable()) {
            apiService.getForecastByLocation(lat, lon, API_KEY, "metric", lang)
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                processForecastResponse(response.body(), forecastData);
                            } else {
                                handleApiError(response.code(), "forecast by location", forecastData);
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                            handleApiFailure(t, "forecast by location", forecastData);
                        }
                    });
        } else {
            forecastData.postValue(null);
        }
    }

    public void searchWeatherByCity(String city, MutableLiveData<WeatherData> weatherData) {
        if (isNetworkAvailable()) {
            apiService.getCurrentWeather(city, API_KEY, "metric", lang)
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                processWeatherResponse(response.body(), weatherData, city);
                            } else if (response.code() == 404) {
                                Log.e(TAG, "City not found (404): " + city);
                                weatherData.postValue(null);
                            } else {
                                handleApiError(response.code(), "weather for city: " + city, weatherData);
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                            handleApiFailure(t, "weather for city: " + city, weatherData);
                        }
                    });
        } else {
            weatherData.postValue(null);
        }
    }

    public void getForecastByCity(String city, MutableLiveData<List<ForecastData>> forecastData) {
        if (isNetworkAvailable()) {
            apiService.getForecast(city, API_KEY, "metric", lang)
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                processForecastResponse(response.body(), forecastData);
                            } else {
                                handleApiError(response.code(), "forecast for city: " + city, forecastData);
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                            handleApiFailure(t, "forecast for city: " + city, forecastData);
                        }
                    });
        } else {
            forecastData.postValue(null);
        }
    }

    public String getCurrentFormattedDate() {
        return DateUtils.getDayFromDate(new Date()) + " " +
                new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private void processWeatherResponse(JsonObject responseBody, MutableLiveData<WeatherData> weatherData, double lat, double lon) {
        WeatherResponse body = gson.fromJson(responseBody, WeatherResponse.class);
        if (body.weather != null && body.weather.length > 0 && body.main != null) {
            // Fetch forecast data first to get accurate min/max for today
            apiService.getForecastByLocation(lat, lon, API_KEY, "metric", lang)
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                ForecastResponse forecastResponse = gson.fromJson(response.body(), ForecastResponse.class);
                                WeatherData data = createWeatherDataWithForecast(body, forecastResponse);
                                weatherData.postValue(data);
                            } else {
                                Log.e(TAG, "Failed to get forecast for current location: " + response.code());
                                WeatherData data = createWeatherData(body);
                                weatherData.postValue(data);
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                            Log.e(TAG, "Failed to get forecast for current location: " + t.getMessage());
                            WeatherData data = createWeatherData(body);
                            weatherData.postValue(data);
                        }
                    });
        } else {
            Log.e(TAG, "Incomplete weather data");
            weatherData.postValue(null);
        }
    }

    private void processWeatherResponse(JsonObject responseBody, MutableLiveData<WeatherData> weatherData, String city) {
        WeatherResponse body = gson.fromJson(responseBody, WeatherResponse.class);

        if (body.cityName == null || body.cityName.isEmpty()) {
            Log.e(TAG, "City not found: " + city);
            weatherData.postValue(null);
            return;
        }

        if (body.weather == null || body.weather.length == 0 || body.main == null) {
            Log.e(TAG, "Incomplete weather data for city: " + city);
            weatherData.postValue(null);
            return;
        }

        // Fetch forecast data first to get accurate min/max for today
        apiService.getForecast(city, API_KEY, "metric", lang)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ForecastResponse forecastResponse = gson.fromJson(response.body(), ForecastResponse.class);
                            WeatherData data = createWeatherDataWithForecast(body, forecastResponse);
                            weatherData.postValue(data);
                        } else {
                            Log.e(TAG, "Failed to get forecast for current city: " + response.code());
                            WeatherData data = createWeatherData(body);
                            weatherData.postValue(data);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                        Log.e(TAG, "Failed to get forecast for current city: " + t.getMessage());
                        WeatherData data = createWeatherData(body);
                        weatherData.postValue(data);
                    }
                });
    }

    private WeatherData createWeatherData(WeatherResponse body) {
        return new WeatherData(
                body.cityName,
                body.weather[0].description,
                Math.round(body.main.temp),
                Math.round(body.main.tempMin),
                Math.round(body.main.tempMax),
                body.main.pressure,
                body.wind != null ? body.wind.speed : 0,
                body.main.humidity,
                new ArrayList<>()
        );
    }

    private WeatherData createWeatherDataWithForecast(WeatherResponse weatherBody, ForecastResponse forecastBody) {
        int todayMinTemp = Math.round(weatherBody.main.tempMin);
        int todayMaxTemp = Math.round(weatherBody.main.tempMax);

        if (forecastBody != null && forecastBody.list != null && !forecastBody.list.isEmpty()) {
            String currentDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            float minTemp = Float.MAX_VALUE;
            float maxTemp = Float.MIN_VALUE;
            boolean foundToday = false;

            for (ForecastResponse.Item item : forecastBody.list) {
                String itemDate = item.date.substring(0, 10); // Extract YYYY-MM-DD
                if (itemDate.equals(currentDay)) {
                    minTemp = Math.min(minTemp, item.main.tempMin);
                    maxTemp = Math.max(maxTemp, item.main.tempMax);
                    foundToday = true;
                }
            }
            if (foundToday) {
                todayMinTemp = Math.round(minTemp);
                todayMaxTemp = Math.round(maxTemp);
            }
        }

        return new WeatherData(
                weatherBody.cityName,
                weatherBody.weather[0].description,
                Math.round(weatherBody.main.temp),
                todayMinTemp,
                todayMaxTemp,
                weatherBody.main.pressure,
                weatherBody.wind != null ? weatherBody.wind.speed : 0,
                weatherBody.main.humidity,
                new ArrayList<>()
        );
    }

    private void processForecastResponse(JsonObject responseBody, MutableLiveData<List<ForecastData>> forecastData) {
        ForecastResponse forecastResponse = gson.fromJson(responseBody, ForecastResponse.class);
        List<ForecastData> forecastList = new ArrayList<>();
        Map<String, float[]> dailyTemps = new HashMap<>();
        Map<String, ForecastResponse.Item> representativeItems = new HashMap<>();

        if (forecastResponse.list != null && !forecastResponse.list.isEmpty()) {
            String currentDayFormatted = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            for (ForecastResponse.Item item : forecastResponse.list) {
                if (item.date != null && item.main != null && item.weather != null) {
                    String day = item.date.substring(0, 10); // Extract YYYY-MM-DD
                    // Exclude current day's forecast from the 5-day forecast list
                    if (day.equals(currentDayFormatted)) {
                        continue;
                    }

                    float tempMin = item.main.tempMin;
                    float tempMax = item.main.tempMax;

                    if (!dailyTemps.containsKey(day)) {
                        dailyTemps.put(day, new float[]{tempMin, tempMax});
                        representativeItems.put(day, item); // Store the first item for the day as representative
                    } else {
                        float[] currentTemps = dailyTemps.get(day);
                        if (currentTemps != null) {
                            if (tempMin < currentTemps[0]) {
                                currentTemps[0] = tempMin;
                            }
                            if (tempMax > currentTemps[1]) {
                                currentTemps[1] = tempMax;
                            }
                            dailyTemps.put(day, currentTemps);
                        }
                    }
                }
            }

            // Now create ForecastData objects from aggregated daily temperatures
            for (Map.Entry<String, float[]> entry : dailyTemps.entrySet()) {
                String dayKey = entry.getKey();
                float[] temps = entry.getValue();
                ForecastResponse.Item representativeItem = representativeItems.get(dayKey);

                if (representativeItem != null) {
                    // Convert date to day name (e.g., LUNDI)
                    String dayName = DateUtils.getDayFromDate(representativeItem.date);

                    forecastList.add(new ForecastData(
                            dayName,
                            Math.round(temps[0]),
                            Math.round(temps[1]),
                            representativeItem.main.pressure,
                            representativeItem.wind != null ? representativeItem.wind.speed : 0,
                            representativeItem.main.humidity,
                            !representativeItem.weather.isEmpty() ? representativeItem.weather.get(0).description : ""
                    ));
                }
            }

            // Sort the forecastList by date using List.sort()
            forecastList.sort((o1, o2) -> {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
                    Date date1 = sdf.parse(o1.getDay());
                    Date date2 = sdf.parse(o2.getDay());
                    if (date1 == null || date2 == null) {
                        return 0;
                    }
                    return date1.compareTo(date2);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date for sorting: " + e.getMessage());
                    return 0;
                }
            });

            if (forecastList.isEmpty()) {
                Log.e(TAG, "No valid forecast data found");
                forecastData.postValue(null);
            } else {
                forecastData.postValue(forecastList);
            }
        } else {
            Log.e(TAG, "Empty or invalid forecast data");
            forecastData.postValue(null);
        }
    }

    private <T> void handleApiError(int errorCode, String operation, MutableLiveData<T> liveData) {
        Log.e(TAG, "API response not successful for " + operation + ": " + errorCode);
        liveData.postValue(null);
    }

    private <T> void handleApiFailure(Throwable t, String operation, MutableLiveData<T> liveData) {
        Log.e(TAG, "API call failed for " + operation + ": " + t.getMessage());
        liveData.postValue(null);
    }
}

