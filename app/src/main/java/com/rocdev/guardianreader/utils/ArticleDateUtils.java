package com.rocdev.guardianreader.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by piet on 20-11-17.
 */

public class ArticleDateUtils {


    private static final String DATE_FORMAT_IN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DATE_FORMAT_OUT = "d MMMM yyyy HH:mm";
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
        return sdfOut.format(dateIn);
    }


}
