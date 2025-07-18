package com.yassmin.meto.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String getDayFromDate(String dateStr) {
        if (dateStr == null) {
            return "JOUR";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            if (date == null) {
                return "JOUR";
            }
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(date).toUpperCase();
        } catch (Exception e) {
            return "JOUR";
        }
    }

    public static String getDayFromDate(Date date) {
        if (date == null) {
            return "JOUR";
        }
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        return dayFormat.format(date).toUpperCase();
    }
}
