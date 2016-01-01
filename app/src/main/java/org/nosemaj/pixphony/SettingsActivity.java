/*
 * Copyright 2015 Jameson Williams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nosemaj.pixphony;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import org.nosemaj.pixphony.R;
import org.nosemaj.pixphony.music.SoundPlayer;

/*
 * https://developer.android.com/guide/topics/ui/settings.html#Activity
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setupListPreferenceListener();
    }

    private void setupListPreferenceListener() {
        final ListPreference samplePreference = 
            (ListPreference) findPreference("sample_preference");
        samplePreference.setOnPreferenceChangeListener(mListener);
    }

    private OnPreferenceChangeListener mListener = new OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            SoundPlayer soundPlayer = 
                ((PixphonyApplication)getApplicationContext()).getSoundPlayer();

            soundPlayer.setSample((String)newValue);

            return true;
        }
    };
}

