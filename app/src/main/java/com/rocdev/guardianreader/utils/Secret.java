package com.rocdev.guardianreader.utils;

/**
 * Created by piet on 30-04-17.
 *
 */

public class Secret {

    private static final String API_KEY = "cc2f8fb0-4e6d-46f3-9588-2cdb8072943d";

    /**
     * private Constructor to avoid instanciating (i.e. static only)
     */
    private Secret() {}

    static String getApiKey() {
        return API_KEY;
    }
}
