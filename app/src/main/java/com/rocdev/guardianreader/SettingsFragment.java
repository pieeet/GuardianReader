package com.rocdev.guardianreader;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by piet on 19-05-17.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
