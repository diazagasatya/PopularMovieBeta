package com.example.android.popularmoviebeta;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Add the preference xml to the Settings Fragment
        addPreferencesFromResource(R.xml.pref_settings);
    }
}
