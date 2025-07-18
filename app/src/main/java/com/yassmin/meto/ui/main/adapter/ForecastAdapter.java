package com.yassmin.meto.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.yassmin.meto.R;
import com.yassmin.meto.data.models.ForecastData;
import com.yassmin.meto.utils.WeatherUtils;

import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private final AsyncListDiffer<ForecastData> differ = new AsyncListDiffer<>(this, DIFF_CALLBACK);

    public void submitList(List<ForecastData> newList) {
        differ.submitList(newList);
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastData data = differ.getCurrentList().get(position);
        holder.dayTextView.setText(data.getDay());
        holder.tempMinTextView.setText(String.format(Locale.getDefault(), "%d°", data.getTempMin()));
        holder.tempMaxTextView.setText(String.format(Locale.getDefault(), "%d°", data.getTempMax()));
        holder.pressureTextView.setText(String.format(Locale.getDefault(), "%d hPa", data.getPressure()));
        holder.windTextView.setText(String.format(Locale.getDefault(), "%.1f km/h", data.getWindSpeed()));
        holder.humidityTextView.setText(String.format(Locale.getDefault(), "%d%%", data.getHumidity()));

        // For forecasts, we\'ll assume it\'s daytime (second parameter false)
        WeatherUtils.setWeatherIcon(holder.weatherIconImageView, data.getWeatherCondition(), false);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        final TextView dayTextView;
        final TextView tempMinTextView;
        final TextView tempMaxTextView;
        final TextView pressureTextView;
        final TextView windTextView;
        final TextView humidityTextView;
        final ImageView weatherIconImageView;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            tempMinTextView = itemView.findViewById(R.id.tempMinTextView);
            tempMaxTextView = itemView.findViewById(R.id.tempMaxTextView);
            pressureTextView = itemView.findViewById(R.id.pressureTextView);
            windTextView = itemView.findViewById(R.id.windTextView);
            humidityTextView = itemView.findViewById(R.id.humidityTextView);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView);
        }
    }

    private static final DiffUtil.ItemCallback<ForecastData> DIFF_CALLBACK = new DiffUtil.ItemCallback<ForecastData>() {
        @Override
        public boolean areItemsTheSame(@NonNull ForecastData oldItem, @NonNull ForecastData newItem) {
            return oldItem.getDay().equals(newItem.getDay());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ForecastData oldItem, @NonNull ForecastData newItem) {
            return oldItem.getDay().equals(newItem.getDay()) &&
                    oldItem.getTempMin() == newItem.getTempMin() &&
                    oldItem.getTempMax() == newItem.getTempMax() &&
                    oldItem.getPressure() == newItem.getPressure() &&
                    oldItem.getWindSpeed() == newItem.getWindSpeed() &&
                    oldItem.getHumidity() == newItem.getHumidity() &&
                    oldItem.getWeatherCondition().equals(newItem.getWeatherCondition());
        }
    };
}
