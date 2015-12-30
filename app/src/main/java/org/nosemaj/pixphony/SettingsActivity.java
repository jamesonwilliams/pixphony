package org.nosemaj.pixphony;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/*
 * https://developer.android.com/guide/topics/ui/settings.html#Activity
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
