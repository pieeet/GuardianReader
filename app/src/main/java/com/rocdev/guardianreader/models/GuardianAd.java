package com.rocdev.guardianreader.models;

/**
 * Created by piet on 04-02-18.
 */

public class GuardianAd {

    public static final int TYPE_BECOME_A_SUPPORTER = 0;
    public static final int TYPE_DOWNLOAD_APP = 1;
    private static final String TEXT_SUPPORTER = "Become a Guardian supporter";
    private static final String TEXT_DOWNLOAD_APP = "Install the official Guardian App";
    public static final String URL_SUPPORTER_PAGE = "https://membership.theguardian.com/supporter";
    public static final String URL_PLAY_STORE = "https://play.google.com/store/apps/details?id=com.guardian";


    private String adText;
    private String url;

    public GuardianAd(int type) {
        if (type == TYPE_BECOME_A_SUPPORTER) {
            adText = TEXT_SUPPORTER;
            url = URL_SUPPORTER_PAGE;
        }
        else if (type == TYPE_DOWNLOAD_APP) {
            adText = TEXT_DOWNLOAD_APP;
            url = URL_PLAY_STORE;
        }
    }

    public String getAdText() {
        return adText;
    }

    public String getUrl() {
        return url;
    }
}
