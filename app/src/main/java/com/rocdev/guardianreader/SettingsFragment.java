package com.rocdev.guardianreader;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;


/**
 * Created by piet on 19-05-17.
 *
 */

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    ListPreference defaultEditionListPreference;
    SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        defaultEditionListPreference = (ListPreference) findPreference(
                getString(R.string.pref_key_default_edition));
        mSharedPreferences = getPreferenceManager().getSharedPreferences();
        setSummary();
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        mSharedPreferences = sharedPreferences;
        setSummary();
    }

    private void setSummary() {
        int value = Integer.parseInt(mSharedPreferences.getString(getString(
                R.string.pref_key_default_edition), "3"));
        String[] entries = getResources().getStringArray(R.array.default_entries);
        defaultEditionListPreference.setSummary(entries[value]);
    }
}
