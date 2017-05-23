package com.rocdev.guardianreader.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rocdev.guardianreader.fragments.SettingsFragment;

/**
 * Created by piet on 19-05-17.
 *
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
