package com.appedidos.parseinnovaapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jvargas on 10/7/15.
 */
public class Utils {
    private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm a";
    private static final String TIME_FORMAT = "HH:mm";
    private static final String DAY_FORMAT = "dd.MM";

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

    public static Date stringToDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

    public static String getDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }
}
