package com.yassmin.meto.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.yassmin.meto.R;
import com.yassmin.meto.data.models.WeatherData;
import com.yassmin.meto.ui.main.adapter.ForecastAdapter;
import com.yassmin.meto.utils.PermissionUtils;
import com.yassmin.meto.utils.WeatherUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final long LOCATION_UPDATE_INTERVAL = 10000;
    private static final long FASTEST_LOCATION_UPDATE_INTERVAL = 5000;

    private MainViewModel viewModel;
    private EditText cityEditText;
    private TextView cityTextView, weatherConditionTextView, temperatureTextView;
    private ForecastAdapter forecastAdapter;
    private ProgressBar loadingIndicator;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isNetworkAvailable()) {
            showNetworkError();
            return;
        }

        initializeApp();
    }

    private void showNetworkError() {
        setContentView(R.layout.layout_error_state);
        findViewById(R.id.retryButton).setOnClickListener(v -> checkNetworkAndRestart());
    }

    private void checkNetworkAndRestart() {
        if (isNetworkAvailable()) {
            recreate();
        } else {
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeApp() {
        setContentView(R.layout.activity_main);
        setupViewModel();
        initViews();
        setupObservers();
        setupTouchListener();
        requestUserLocation();
    }

    private void setupTouchListener() {
        findViewById(R.id.mainScrollView).setOnTouchListener((v, event) -> {
            hideKeyboardAndClearFocus();
            v.performClick();
            return false;
        });
    }

    private void hideKeyboardAndClearFocus() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            currentFocus.clearFocus();
        }
    }

    private void showBackButton(boolean show) {
        findViewById(R.id.backButton).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void onBackToMainClick(View view) {
        hideKeyboardAndClearFocus();
        cityEditText.setText("");
        showBackButton(false);
        requestUserLocation();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (cityEditText.getText().toString().isEmpty()) {
                finish();
            } else {
                onBackToMainClick(null);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    private void initViews() {
        cityEditText = findViewById(R.id.cityEditText);
        cityTextView = findViewById(R.id.cityTextView);
        weatherConditionTextView = findViewById(R.id.weatherConditionTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        RecyclerView forecastRecyclerView = findViewById(R.id.forecastRecyclerView);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        ));

        forecastAdapter = new ForecastAdapter();
        forecastRecyclerView.setAdapter(forecastAdapter);

        cityEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onSearchClick(v);
                return true;
            }
            return false;
        });
    }

    private void setupObservers() {
        ImageView weatherIconImageView = findViewById(R.id.weatherIconImageView);
        TextView tempMinTextView = findViewById(R.id.tempMinTextView);
        TextView tempMaxTextView = findViewById(R.id.tempMaxTextView);
        TextView pressureTextView = findViewById(R.id.pressureTextView);
        TextView windTextView = findViewById(R.id.windTextView);
        TextView humidityTextView = findViewById(R.id.humidityTextView);
        TextView updatedAtTextView = findViewById(R.id.updatedAtTextView);

        updateDateTime(updatedAtTextView);

        viewModel.getWeatherData().observe(this, weatherData -> {
            hideLoading();

            if (weatherData != null) {
                boolean isSearchResult = !cityEditText.getText().toString().isEmpty();
                showBackButton(isSearchResult);

                updateWeatherUI(weatherData, weatherIconImageView, tempMinTextView,
                        tempMaxTextView, pressureTextView, windTextView,
                        humidityTextView, updatedAtTextView);
            } else {
                showWeatherDataError();
            }
        });

        viewModel.getForecastData().observe(this, forecast -> {
            if (forecast != null && !forecast.isEmpty()) {
                forecastAdapter.submitList(forecast);
            } else {
                showForecastError();
            }
        });
    }

    private void updateWeatherUI(WeatherData weatherData, ImageView weatherIconImageView,
                                 TextView tempMinTextView, TextView tempMaxTextView,
                                 TextView pressureTextView, TextView windTextView,
                                 TextView humidityTextView, TextView updatedAtTextView) {
        cityTextView.setText(weatherData.getCityName());

        boolean isNight = WeatherUtils.isNightTime();
        String adaptedCondition = WeatherUtils.adaptWeatherConditionForNight(
                weatherData.getWeatherCondition(), isNight);
        weatherConditionTextView.setText(adaptedCondition);

        temperatureTextView.setText(String.format(Locale.getDefault(),
                "%d°C", weatherData.getTemperature()));

        tempMinTextView.setText(String.format(Locale.getDefault(), "%d°", weatherData.getTempMin()));
        tempMaxTextView.setText(String.format(Locale.getDefault(), "%d°", weatherData.getTempMax()));
        pressureTextView.setText(String.format(Locale.getDefault(), "%d hPa", weatherData.getPressure()));
        windTextView.setText(String.format(Locale.getDefault(), "%.1f km/h", weatherData.getWindSpeed()));
        humidityTextView.setText(String.format(Locale.getDefault(), "%d%%", weatherData.getHumidity()));
        WeatherUtils.setWeatherIcon(weatherIconImageView, weatherData.getWeatherCondition(), isNight);
        updateDateTime(updatedAtTextView);
    }

    private void showWeatherDataError() {
        Toast.makeText(MainActivity.this,
                R.string.weather_data_error,
                Toast.LENGTH_LONG).show();
    }

    private void showForecastError() {
        Toast.makeText(MainActivity.this,
                R.string.forecast_error,
                Toast.LENGTH_SHORT).show();
    }

    private void updateDateTime(TextView updatedAtTextView) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        Date now = new Date();
        String day = dayFormat.format(now).toUpperCase();
        String time = timeFormat.format(now);

        String updatedText = getString(R.string.updated_at_first_line) + "\n" +
                getString(R.string.updated_at_second_line, day, time);
        updatedAtTextView.setText(updatedText);
    }

    private void showLoading() {
        loadingIndicator.setVisibility(View.VISIBLE);
        findViewById(R.id.mainContent).setVisibility(View.GONE);
    }

    private void hideLoading() {
        loadingIndicator.setVisibility(View.GONE);
        findViewById(R.id.mainContent).setVisibility(View.VISIBLE);
    }

    public void onSearchClick(View view) {
        String city = cityEditText.getText().toString().trim();
        if (!city.isEmpty()) {
            hideKeyboardAndClearFocus();
            showLoading();
            viewModel.searchWeather(city);
        }
    }

    private void requestUserLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (PermissionUtils.checkLocationPermission(this)) {
            checkLocationSettings(fusedLocationClient);
        } else {
            PermissionUtils.requestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void checkLocationSettings(FusedLocationProviderClient fusedLocationClient) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            promptUserToEnableGPS();
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                LOCATION_UPDATE_INTERVAL
        )
                .setMinUpdateIntervalMillis(FASTEST_LOCATION_UPDATE_INTERVAL)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates(locationRequest, fusedLocationClient);
            }
        });

        task.addOnFailureListener(this, e -> showLocationError());
    }

    private void promptUserToEnableGPS() {
        Toast.makeText(this, R.string.enable_gps_message, Toast.LENGTH_LONG).show();
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    private void startLocationUpdates(LocationRequest locationRequest, FusedLocationProviderClient fusedLocationClient) {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    viewModel.loadWeatherByLocation(lat, lon);
                    fusedLocationClient.removeLocationUpdates(this);
                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        } catch (SecurityException e) {
            showLocationError();
        }
    }

    private void showLocationError() {
        Toast.makeText(MainActivity.this,
                R.string.location_error,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationCallback != null) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                requestUserLocation();
            } else {
                Toast.makeText(this,
                        R.string.location_permission_denied,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}