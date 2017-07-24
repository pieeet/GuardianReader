package com.rocdev.guardianreader.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.rocdev.guardianreader.R;

/**
 * Created by piet on 23-07-17.
 *
 */

public class Settings2Fragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String PREF_KEY_DEFAULT_EDITION = "pref_default_edition";
    private static final String PREF_KEY_DEFAULT_BROWSER = "pref_default_browser";


    private ListPreference defaultEditionListPreference;
    private ListPreference defaultBrowserListPreference;
    private SharedPreferences mSharedPreferences;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        defaultEditionListPreference = (ListPreference) findPreference(
                getString(R.string.pref_key_default_edition));
        defaultBrowserListPreference = (ListPreference) findPreference(getString(R.string.pref_key_default_browser));
        mSharedPreferences = getPreferenceManager().getSharedPreferences();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        setDefaultBrowserSummary();
        setDefaultEditionSummary();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mSharedPreferences = sharedPreferences;
        switch (key) {
            case PREF_KEY_DEFAULT_EDITION:
                setDefaultEditionSummary();
                break;
            case PREF_KEY_DEFAULT_BROWSER:
                setDefaultBrowserSummary();
                break;
            default:
                // do nothing
        }
    }

    private void setDefaultEditionSummary() {
        int editionValue = Integer.parseInt(mSharedPreferences.getString(getString(
                R.string.pref_key_default_edition), "3"));
        String[] editionEntries = getResources().getStringArray(R.array.default_entries);
        defaultEditionListPreference.setSummary(editionEntries[editionValue]);
    }

    private void setDefaultBrowserSummary() {
        int browserValue = Integer.parseInt(mSharedPreferences.getString(getString(
                R.string.pref_key_default_browser),"0"));
        String[] browserEntries = getResources().getStringArray(R.array.default_browser_entries);
        defaultBrowserListPreference.setSummary(browserEntries[browserValue]);
    }
}
