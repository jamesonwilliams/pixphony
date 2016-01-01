/*
 * Copyright 2015 Nagaraj Bhat
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.util.BleUtils;

import org.nosemaj.pixphony.R;
import org.nosemaj.pixphony.ble.PixmobConnectionManager;
import org.nosemaj.pixphony.ble.PixmobDeviceListener;
import org.nosemaj.pixphony.music.Instruments;
import org.nosemaj.pixphony.music.SoundPlayer;
import org.nosemaj.pixphony.util.ButtonLayout;
import org.nosemaj.pixphony.util.InstrumentLayout;

import java.util.ArrayList;

public class PianoActivity extends Activity {
    private static final String TAG = PianoActivity.class.getName();

    private PixmobConnectionManager mConnectionManager = null;
    private SoundPlayer mSoundPlayer = null;

    private boolean mBleInitialized = false;

    private InstrumentLayout pianoInstrument = null;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_piano);

        setupKeys();
        setupButtons();

        if (checkForBle()) {
            mBleInitialized = false;
            setupBleMidi();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupSoundPlayer();
    }

    private void setupSoundPlayer() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mSoundPlayer = ((PixphonyApplication)getApplicationContext()).getSoundPlayer();
        mSoundPlayer.setMappedInstrument(Instruments.get(Instruments.PIANO));

        setPreferredSample();
    }

    private void setPreferredSample() {
        SharedPreferences sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String preferredSample =
            sharedPreferences.getString("sample_preference", null); 

        mSoundPlayer.setSample(preferredSample);
    }


    private void setupBleMidi() {
        mConnectionManager = ((PixphonyApplication)getApplicationContext()).getConnectionManager();

        if (!mBleInitialized) {
            mConnectionManager.setListener(mPixmobListener);
            mConnectionManager.init();
            mConnectionManager.startScan();
            mBleInitialized = true;
        }
    }

    private boolean checkForBle() {
        if (!BleUtils.isBluetoothEnabled(this)) {
            BleUtils.enableBluetooth(this);
            return false;
        }

        if (!BleUtils.isBleSupported(this)) {
            alertBleNotSupported();
            return false;
        }

        return true;
    }

    private void setupButtons() {
        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() settings button");
                Intent i = new Intent(PianoActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });
    }

    private void setupKeys() {
        pianoInstrument = new InstrumentLayout(
            new ButtonLayout[] {
                new ButtonLayout(0, ButtonLayout.KeyUnPressed, R.drawable.white, R.drawable.white_pressed, (ImageButton) findViewById(R.id.white1_f_53)),
                new ButtonLayout(2, ButtonLayout.KeyUnPressed, R.drawable.white, R.drawable.white_pressed, (ImageButton) findViewById(R.id.white2_g_55)),
                new ButtonLayout(4, ButtonLayout.KeyUnPressed, R.drawable.white, R.drawable.white_pressed, (ImageButton) findViewById(R.id.white3_a_57)),
                new ButtonLayout(6, ButtonLayout.KeyUnPressed, R.drawable.white, R.drawable.white_pressed, (ImageButton) findViewById(R.id.white4_b_59)),
                new ButtonLayout(7, ButtonLayout.KeyUnPressed, R.drawable.white, R.drawable.white_pressed, (ImageButton) findViewById(R.id.white5_c_60)),
                new ButtonLayout(9, ButtonLayout.KeyUnPressed, R.drawable.white, R.drawable.white_pressed, (ImageButton) findViewById(R.id.white6_g_62)),
                new ButtonLayout(11, ButtonLayout.KeyUnPressed, R.drawable.white, R.drawable.white_pressed, (ImageButton) findViewById(R.id.white7_e_64)),
                new ButtonLayout(12, ButtonLayout.KeyUnPressed, R.drawable.white, R.drawable.white_pressed, (ImageButton) findViewById(R.id.white8_f_65)),
                new ButtonLayout(14, ButtonLayout.KeyUnPressed, R.drawable.white, R.drawable.white_pressed, (ImageButton) findViewById(R.id.white9_g_67)),
                new ButtonLayout(16, ButtonLayout.KeyUnPressed, R.drawable.white, R.drawable.white_pressed, (ImageButton) findViewById(R.id.white10_a_69)),
                new ButtonLayout(18, ButtonLayout.KeyUnPressed, R.drawable.white, R.drawable.white_pressed, (ImageButton) findViewById(R.id.white11_b_71)),
                new ButtonLayout(1, ButtonLayout.KeyUnPressed, R.drawable.black, R.drawable.black_pressed, (ImageButton) findViewById(R.id.black1_f_gb_54)),
                new ButtonLayout(3, ButtonLayout.KeyUnPressed, R.drawable.black, R.drawable.black_pressed, (ImageButton) findViewById(R.id.black2_g_ab_56)),
                new ButtonLayout(5, ButtonLayout.KeyUnPressed, R.drawable.black, R.drawable.black_pressed, (ImageButton) findViewById(R.id.black3_a_bb_58)),
                new ButtonLayout(8, ButtonLayout.KeyUnPressed, R.drawable.black, R.drawable.black_pressed, (ImageButton) findViewById(R.id.black4_c_db_61)),
                new ButtonLayout(10, ButtonLayout.KeyUnPressed, R.drawable.black, R.drawable.black_pressed, (ImageButton) findViewById(R.id.black5_d_eb_63)),
                new ButtonLayout(13, ButtonLayout.KeyUnPressed, R.drawable.black, R.drawable.black_pressed, (ImageButton) findViewById(R.id.black6_f_gb_66)),
                new ButtonLayout(15, ButtonLayout.KeyUnPressed, R.drawable.black, R.drawable.black_pressed, (ImageButton) findViewById(R.id.black7_g_ab_68)),
                new ButtonLayout(17, ButtonLayout.KeyUnPressed, R.drawable.black, R.drawable.black_pressed, (ImageButton) findViewById(R.id.black8_a_bb_70))
            });

        LinearLayout keypadLayout = (LinearLayout) findViewById(R.id.keypadLayout);
        int currentWhiteKeys = 11;
        keypadLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int btnWidth = metrics.widthPixels / (currentWhiteKeys);
        int btnHeight = metrics.heightPixels;
        int btnMinWidth = 80;

        for (int i = 0; i < pianoInstrument.buttonCollections.length && i < currentWhiteKeys; i++) {
            pianoInstrument.buttonCollections[i].keyButton.setOnTouchListener(mToneTouchListener);
            pianoInstrument.buttonCollections[i].keyButton.setTag(pianoInstrument.buttonCollections[i].idToButtonMap);
            ViewGroup.LayoutParams lp = pianoInstrument.buttonCollections[i].keyButton.getLayoutParams();
            lp.width = btnWidth;
            pianoInstrument.buttonCollections[i].keyButton.setLayoutParams(lp);
        }

        int blackBtnWidth = (btnWidth * 2) / 3;
        int blackBtnHeight = (btnHeight * 4) / 10;

        // black keys on top.
        for (int i = currentWhiteKeys; i < pianoInstrument.buttonCollections.length; i++) {
            pianoInstrument.buttonCollections[i].keyButton.setOnTouchListener(mToneTouchListener);
            pianoInstrument.buttonCollections[i].keyButton.setTag(pianoInstrument.buttonCollections[i].idToButtonMap);

            RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) pianoInstrument.buttonCollections[i].keyButton.getLayoutParams();
            lp1.height = blackBtnHeight;
            lp1.width = blackBtnWidth;
            lp1.setMargins(-(blackBtnWidth / 2), 0, 0, 0);
            pianoInstrument.buttonCollections[i].keyButton.setLayoutParams(lp1);
        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        int lastPressedKey = pianoInstrument.HandleSwipeEventsInLayout(event);

        if( lastPressedKey != -1 && lastPressedKey >= 0 && lastPressedKey < pianoInstrument.buttonCollections.length ) {
            int noteBase = Instruments.PIXMOB_PIANO_LOWEST_MIDI_NOTE;
            /* TODO: May be improve this to get base note from an API */
            mSoundPlayer.stop();
            mSoundPlayer.playMidiNote(pianoInstrument.buttonCollections[lastPressedKey].idToButtonMap + noteBase);
        }
        return super.dispatchTouchEvent(event);
    }

    View.OnTouchListener mToneTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!(v instanceof ImageButton)) {
                return false;
            }

            ImageButton key = (ImageButton) v;
            Object tag = key.getTag();

            if (tag == null || !(tag instanceof Integer)) {
                return false; 
            }

            int index = (Integer) tag;
            int note = 53 +  index;

            Log.d(TAG, "onTouch() with note = " + note);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mSoundPlayer.stop();
                    mSoundPlayer.playMidiNote(note);
                    pianoInstrument.ShowPressKeyForTone(index);
                    break;
    
                default:
                    // do nothing.
                    break;
            }

            return false;
        }
    };

    PixmobDeviceListener mPixmobListener = new PixmobDeviceListener() {
        @Override
        public void onDevicePlayed(@NonNull MidiInputDevice sender, int channel, int note, int velocity, boolean noteOn) {
            super.onDevicePlayed(sender, channel, note, velocity, noteOn);
            mSoundPlayer.playMidiNote(note);
        }

        @Override
        public void onLog(String text) {
            Log.d(TAG, text);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult()");

        if (requestCode == BleUtils.REQUEST_CODE_BLUETOOTH_ENABLE) {
            if (!BleUtils.isBluetoothEnabled(this)) {
                // User selected NOT to use Bluetooth.
                // do nothing
                return;
            }

            if (!BleUtils.isBleSupported(this)) {
                alertBleNotSupported();
                return;
            }

            setupBleMidi();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    private void alertBleNotSupported() {
        alert(true,
              "Not supported",
              "Bluetooth LE is not supported on this device. The app will exit.");
    }

    private void alert(final boolean isError, final String title, final String message) {
        // display alert and exit
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isError) {
                    finish();
                }
            }
        });
        alertDialog.show();
    }
}

