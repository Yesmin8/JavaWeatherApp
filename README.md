# JavaWeatherApp

**JavaWeatherApp** is an Android weather application built with **Java**.  
It uses the [OpenWeatherMap API](https://openweathermap.org/api) to display **current weather conditions** and **5-day forecasts**.

The app features a **modern and intuitive interface**, with **city search** and **automatic location detection via GPS**.

---

## Features

- Display of current weather conditions (temperature, humidity, wind, etc.)
- 5-day forecast with horizontal scrolling (RecyclerView)
- Search functionality by city name
- Automatic location detection using GPS (FusedLocationProvider)
- Responsive and adaptive UI (multi-screen support)
- Permissions handling (location, network)
- Smooth transitions with a custom theme (icons, colors)
- Multi-screen compatibility

---

## Technical Architecture

The project follows **modern Android development practices**, including:

- **MVVM (Model-View-ViewModel)** architecture for separation of concerns
- **Hilt** for dependency injection
- **Retrofit + Gson** for network requests and JSON parsing
- **LiveData / MutableLiveData** for UI data observation
- **FusedLocationProviderClient** for retrieving the device's location

---

## Project Structure

```bash
JavaWeatherApp/
│
├── api/
│   ├── ApiClient.java
│   └── WeatherApiService.java
│
├── data/
│   └── model/
│       ├── WeatherData.java
│       ├── ForecastData.java
│       └── response/
│           ├── WeatherResponse.java
│           └── ForecastResponse.java
│
├── repository/
│   └── WeatherRepository.java
│
├── di/
│   └── AppModule.java
│
├── ui/
│   ├── main/
│   │   ├── MainActivity.java
│   │   ├── MainViewModel.java
│   │   └── adapter/
│   │       └── ForecastAdapter.java
│   │   └── utils/
│   │       ├── DateUtils.java
│   │       ├── PermissionUtils.java
│   │       └── WeatherUtils.java
│   └── details/
│       └── DetailsViewModel.java
│
└── MetoApplication.java
```

---

## Gradle Configuration

```groovy
compileSdk = 35
minSdk = 24
targetSdk = 35

compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
```

---

## Requirements

- Android Studio **Hedgehog** or newer
- JDK **11 or higher**
- Internet connection
- A personal **API key** from [OpenWeatherMap](https://openweathermap.org/api)

---

## Installation

1. Clone the repository:

```bash
git clone https://github.com/Yesmin8/JavaWeatherApp.git
cd JavaWeatherApp
```

2. Open the project in **Android Studio**.

3. Replace the placeholder API key in `WeatherRepository.java`:

```java
private static final String API_KEY = "YOUR_API_KEY_HERE";
```

4. Sync the Gradle project.

5. Run the app on an **emulator** or a **physical device**.

---

## Screenshots

Preview of the Meto application in action:

![App demo](assets/demo.gif)


---

## Future Improvements

This project is still evolving and can be further improved with additional features and refinements.  
Possible enhancements include:

- Adding weather alerts and notifications  
- Supporting dark mode and theming options  
- Integrating a weather map with radar animations  
- Adding hourly forecast support  
- Implementing offline caching of recent data  
- Enhancing error handling and user feedback  
- Supporting more languages (internationalization)

> Contributions, suggestions, and feature requests are welcome!
