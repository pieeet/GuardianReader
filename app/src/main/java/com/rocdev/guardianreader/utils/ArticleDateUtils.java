package com.rocdev.guardianreader.utils;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by piet on 20-11-17.
 *
 */

public class ArticleDateUtils {


    private static final String DATE_FORMAT_IN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DATE_FORMAT_OUT = "d MMMM HH:mm";
    private static final String DATE_FORMAT_TIME_ONLY = "HH:mm";
    private static final String TIME_ZONE_IN = "UTC";

    public static String formatDateTime(String input) {
        SimpleDateFormat sdfIn = new SimpleDateFormat(DATE_FORMAT_IN);
        sdfIn.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_IN));
        Date dateIn = new Date();
        try {
            dateIn = sdfIn.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdfOut = new SimpleDateFormat(DATE_FORMAT_OUT, Locale.getDefault());
        if  (DateUtils.isToday(dateIn.getTime())) {
            return "today " + new SimpleDateFormat(DATE_FORMAT_TIME_ONLY).format(dateIn);
        }
        if (DateUtils.isToday(dateIn.getTime() + DateUtils.DAY_IN_MILLIS)) {
            return "yesterday " + new SimpleDateFormat(DATE_FORMAT_TIME_ONLY).format(dateIn);
        }
        return sdfOut.format(dateIn);
    }

}
