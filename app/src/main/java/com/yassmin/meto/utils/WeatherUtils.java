package com.yassmin.meto.utils;

import android.widget.ImageView;
import com.yassmin.meto.R;
import java.util.Locale;
import java.util.Calendar;

public class WeatherUtils {
    public static void setWeatherIcon(ImageView imageView, String condition, boolean isNight) {
        int iconResId;
        if (condition == null || condition.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_sun);
            return;
        }
        String normalizedCondition = condition.toLowerCase(Locale.FRANCE);

        if (normalizedCondition.contains("ensoleillé") ||
                normalizedCondition.contains("clair") ||
                normalizedCondition.contains("ciel dégagé") ||
                normalizedCondition.contains("soleil")) {
            iconResId = isNight ? R.drawable.ic_moon : R.drawable.ic_sun;
        } else if (normalizedCondition.contains("nuageux") ||
                normalizedCondition.contains("couvert")) {
            iconResId = R.drawable.ic_cloud;
        } else if (normalizedCondition.contains("partiellement")) {
            iconResId = isNight ? R.drawable.nuit_nuage : R.drawable.ic_few_clouds;
        } else if (normalizedCondition.contains("pluie") ||
                normalizedCondition.contains("pluvieux") ||
                normalizedCondition.contains("averse")) {
            iconResId = R.drawable.ic_rain;
        } else if (normalizedCondition.contains("orage") ||
                normalizedCondition.contains("tonnerre")) {
            iconResId = R.drawable.orage;
        } else if (normalizedCondition.contains("neige") ||
                normalizedCondition.contains("neigeux")) {
            iconResId = R.drawable.glace;
        } else if (normalizedCondition.contains("vent") ||
                normalizedCondition.contains("venteux")) {
            iconResId = R.drawable.nuage_vente;
        } else if (normalizedCondition.contains("brouillard") ||
                normalizedCondition.contains("brume")) {
            iconResId = R.drawable.humidity;
        } else {
            iconResId = isNight ? R.drawable.ic_moon  : R.drawable.ic_sun;
        }

        imageView.setImageResource(iconResId);
    }

    public static boolean isNightTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour < 6 || hour >= 18;
    }

    /**
     * Adapte la description météorologique pour la nuit
     * @param condition La condition météorologique originale
     * @param isNight Indique si c\'est la nuit
     * @return La description adaptée
     */
    public static String adaptWeatherConditionForNight(String condition, boolean isNight) {
        if (condition == null || condition.isEmpty() || !isNight) {
            return condition;
        }

        String normalizedCondition = condition.toLowerCase(Locale.FRANCE);

        // Adaptations pour les conditions nocturnes
        if (normalizedCondition.contains("ciel dégagé") ||
                normalizedCondition.contains("ensoleillé") ||
                normalizedCondition.contains("clair")) {
            return "Nuit claire";
        } else if (normalizedCondition.contains("partiellement nuageux") ||
                normalizedCondition.contains("quelques nuages")) {
            return "Nuit partiellement nuageuse";
        } else if (normalizedCondition.contains("nuageux") ||
                normalizedCondition.contains("couvert")) {
            return "Nuit nuageuse";
        } else if (normalizedCondition.contains("brouillard") ||
                normalizedCondition.contains("brume")) {
            return "Nuit brumeuse";
        }

        // Pour les autres conditions (pluie, orage, neige), on garde la description originale
        return condition;
    }
}
