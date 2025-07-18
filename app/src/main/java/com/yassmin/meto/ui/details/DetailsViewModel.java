package com.yassmin.meto.ui.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.yassmin.meto.data.models.WeatherData;

// Classe conservée au cas où elle serait utilisée plus tard
public class DetailsViewModel extends ViewModel {
    private final MutableLiveData<WeatherData> weatherData = new MutableLiveData<>();

    public LiveData<WeatherData> getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherData data) {
        weatherData.setValue(data);
    }
}