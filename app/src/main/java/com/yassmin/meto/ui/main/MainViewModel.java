package com.yassmin.meto.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.yassmin.meto.data.models.WeatherData;
import com.yassmin.meto.data.models.ForecastData;
import com.yassmin.meto.data.repository.WeatherRepository;
import java.util.List;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private final WeatherRepository repository;
    private final MutableLiveData<WeatherData> weatherData = new MutableLiveData<>();
    private final MutableLiveData<List<ForecastData>> forecastData = new MutableLiveData<>();
    private final MutableLiveData<String> lastUpdated = new MutableLiveData<>();

    @Inject
    public MainViewModel(WeatherRepository repository) {
        this.repository = repository;
    }

    public LiveData<WeatherData> getWeatherData() {
        return weatherData;
    }

    public LiveData<List<ForecastData>> getForecastData() {
        return forecastData;
    }


    public void loadWeatherByLocation(double lat, double lon) {
        lastUpdated.postValue(repository.getCurrentFormattedDate());
        repository.getWeatherByLocation(lat, lon, weatherData);
        repository.getForecastByLocation(lat, lon, forecastData);
    }

    public void searchWeather(String city) {
        lastUpdated.postValue(repository.getCurrentFormattedDate());
        repository.searchWeatherByCity(city, weatherData);
        repository.getForecastByCity(city, forecastData);
    }
}
