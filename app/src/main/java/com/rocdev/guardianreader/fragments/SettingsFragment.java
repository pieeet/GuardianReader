package com.rocdev.guardianreader.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.rocdev.guardianreader.R;


/**
 * Created by piet on 19-05-17.
 *
 */

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    private static final String PREF_KEY_DEFAULT_EDITION = "pref_default_edition";
    private static final String PREF_KEY_DEFAULT_BROWSER = "pref_default_browser";


    private ListPreference defaultEditionListPreference;
    private ListPreference defaultBrowserListPreference;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        defaultEditionListPreference = (ListPreference) findPreference(
                getString(R.string.pref_key_default_edition));
        defaultBrowserListPreference = (ListPreference) findPreference(getString(R.string.pref_key_default_browser));
        mSharedPreferences = getPreferenceManager().getSharedPreferences();
        setSummaries();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mSharedPreferences = sharedPreferences;
        if (key.equals(PREF_KEY_DEFAULT_EDITION) || key.equals(PREF_KEY_DEFAULT_BROWSER)) {
            setSummaries();
        }

    }

    private void setSummaries() {
        int editionValue = Integer.parseInt(mSharedPreferences.getString(getString(
                R.string.pref_key_default_edition), "3"));
        String[] editionEntries = getResources().getStringArray(R.array.default_entries);
        defaultEditionListPreference.setSummary(editionEntries[editionValue]);
        int browserValue = Integer.parseInt(mSharedPreferences.getString(getString(
                R.string.pref_key_default_browser),"0"));
        String[] browserEntries = getResources().getStringArray(R.array.default_browser_entries);
        defaultBrowserListPreference.setSummary(browserEntries[browserValue]);

    }
}
