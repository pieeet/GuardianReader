package com.rocdev.guardianreader.utils;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.rocdev.guardianreader.models.Section;

/**
 * Created by piet on 19-11-17.
 *
 */

public class UriBuilder {

    //TODO uncomment before building release-apk
//    private static final String PARAM_VALUE_API_KEY = Secret.getApiKey();
    //TODO comment out before building release-apk
    private static final String PARAM_VALUE_API_KEY = "test";
    private static final String PARAM_NAME_API_KEY = "api-key";
    private static final String PARAM_NAME_SHOW_FIELDS = "show-fields";
    private static final String PARAM_VALUE_SHOW_FIELDS = "thumbnail";
    private static final String PARAM_NAME_EDITOR_PICKS = "show-editors-picks";
    private static final String PARAM_VALUE_EDITOR_PICKS = "true";
    private static final String PARAM_NAME_PAGE = "page";
    private static final String PARAM_NAME_QUERY = "q";


    public static Uri buildUriWithParams(int currentPage, int currentSection,
                                         @Nullable String searchQuery) {
        Section section = Section.values()[currentSection];
        Uri baseUri = Uri.parse(section.getUrl());
        boolean isEditorsPicks = currentSection <= Section.HEADLINES_INT.ordinal();
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(PARAM_NAME_API_KEY, PARAM_VALUE_API_KEY);
        uriBuilder.appendQueryParameter(PARAM_NAME_SHOW_FIELDS, PARAM_VALUE_SHOW_FIELDS);
        if (isEditorsPicks) {
            uriBuilder.appendQueryParameter(PARAM_NAME_EDITOR_PICKS, PARAM_VALUE_EDITOR_PICKS);
        } else {
            uriBuilder.appendQueryParameter(PARAM_NAME_PAGE, String.valueOf(currentPage));
            if (currentSection == Section.SEARCH.ordinal()) {
                uriBuilder.appendQueryParameter(PARAM_NAME_QUERY, searchQuery);
            }
        }
        return uriBuilder.build();
    }



}
