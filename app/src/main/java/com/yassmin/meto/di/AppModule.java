package com.yassmin.meto.di;

import android.content.Context;
import com.yassmin.meto.data.repository.WeatherRepository;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {
    @Provides
    @Singleton
    public WeatherRepository provideWeatherRepository(@ApplicationContext Context context) {
        return new WeatherRepository(context);
    }
}
