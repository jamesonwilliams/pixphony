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
import android.graphics.Rect;
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
import android.widget.RelativeLayout;

import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.util.BleUtils;

import org.nosemaj.pixphony.R;
import org.nosemaj.pixphony.ble.PixmobConnectionManager;
import org.nosemaj.pixphony.ble.PixmobDeviceListener;
import org.nosemaj.pixphony.music.Instruments;
import org.nosemaj.pixphony.music.SoundPlayer;

import java.util.ArrayList;
import java.util.HashMap;

public class PianoActivity extends Activity {
    private static final String TAG = PianoActivity.class.getName();

    private PixmobConnectionManager mConnectionManager = null;
    private SoundPlayer mSoundPlayer = null;

    private boolean mBleInitialized = false;

    private ArrayList<ImageButton> mWhiteKeys = 
        new ArrayList<ImageButton>();
    private ArrayList<ImageButton> mBlackKeys = 
        new ArrayList<ImageButton>();

    /*
     * To process touch events on a key.
     */
    private Rect mViewCoordinates = new Rect();
    private int[] mViewLocation = new int[2];

    /*
     * Support a couple of play modes on the piano.
     */
    private boolean mSustain = true;
    private boolean mMonophonic = false;

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
        setKeyboardPreferences();
    }

    private void setupSoundPlayer() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mSoundPlayer = ((PixphonyApplication)getApplicationContext()).getSoundPlayer();
        mSoundPlayer.setMappedInstrument(Instruments.get(Instruments.PIANO));
    }

    private void setKeyboardPreferences() {
        SharedPreferences sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        final String preferredSample =
            sharedPreferences.getString("sample_preference", null); 
        mSoundPlayer.setSample(preferredSample);

        final boolean monophonicPreference = 
            sharedPreferences.getBoolean("monophonic_touch_preference", false);
        mMonophonic = monophonicPreference;

        final boolean sustainPreference = 
            sharedPreferences.getBoolean("sustain_notes_preference", true);
        mSustain = sustainPreference;
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
        final int whiteKeys[] = {
            R.id.white1_f_53, R.id.white2_g_55, R.id.white3_a_57,
            R.id.white4_b_59, R.id.white5_c_60, R.id.white6_g_62,
            R.id.white7_e_64, R.id.white8_f_65, R.id.white9_g_67,
            R.id.white10_a_69, R.id.white11_b_71,
        };

        final int blackKeys[] = {
            R.id.black1_f_gb_54, R.id.black2_g_ab_56,
            R.id.black3_a_bb_58, R.id.black4_c_db_61,
            R.id.black5_d_eb_63, R.id.black6_f_gb_66,
            R.id.black7_g_ab_68, R.id.black8_a_bb_70,
        };

        for (int id : whiteKeys) {
            ImageButton b = (ImageButton) findViewById(id);
            mWhiteKeys.add(b);
            b.setOnTouchListener(mToneTouchListener);
        }

        for (int id : blackKeys) {
            ImageButton b = (ImageButton) findViewById(id);
            mBlackKeys.add(b);
            b.setOnTouchListener(mToneTouchListener);
        }
    }

    private boolean isInView(View view, MotionEvent ev) {
        view.getDrawingRect(mViewCoordinates);
        view.getLocationOnScreen(mViewLocation);
        mViewCoordinates.offset(mViewLocation[0], mViewLocation[1]);
        return mViewCoordinates.contains((int)ev.getX(), (int)ev.getY());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (ImageButton b : mBlackKeys) {
            if (isInView(b, ev)) {
                return b.dispatchTouchEvent(ev);
            }
        }

        for (ImageButton b : mWhiteKeys) {
            if (isInView(b, ev)) {
                return b.dispatchTouchEvent(ev);
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private void setKeyPressed(ImageButton b) {
        if (mWhiteKeys.contains(b)) {
            b.setImageResource(R.drawable.white_pressed);
        } else {
            b.setImageResource(R.drawable.black_pressed);
        }
    }

    private void unsetKeyPressed(ImageButton b) {
        if (mWhiteKeys.contains(b)) {
            b.setImageResource(R.drawable.white);
        } else {
            b.setImageResource(R.drawable.black);
        }
    }

    private void releaseAllKeys() {
        for (ImageButton b : mWhiteKeys) {
            b.setImageResource(R.drawable.white);
        }

        for (ImageButton b : mBlackKeys) {
            b.setImageResource(R.drawable.black);
        }
    }

    View.OnTouchListener mToneTouchListener = new View.OnTouchListener() {
        private HashMap<Integer,Integer> mLastNoteForPointer =
                new HashMap<Integer,Integer>();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // get pointer index from the event object
            int pointerIndex = event.getActionIndex();

            // get pointer ID
            int pointerId = event.getPointerId(pointerIndex);

            // get masked (not specific to a pointer) action
            int maskedAction = event.getActionMasked();

            int note = Integer.parseInt((String)v.getTag());

            final Integer lastNoteInteger = mLastNoteForPointer.get(pointerId);
            final int lastNote = (lastNoteInteger == null) ? -1 : lastNoteInteger.intValue();

            if (note != lastNote) {
                releaseAllKeys();
            }

            if (maskedAction == MotionEvent.ACTION_DOWN ||
                   maskedAction == MotionEvent.ACTION_POINTER_DOWN ||
                   lastNote != note) {

                setKeyPressed((ImageButton)v);

                if (mMonophonic || !mSustain) {
                    mSoundPlayer.stop(lastNote);
                }

                mSoundPlayer.playMidiNote(note);
                mLastNoteForPointer.put(pointerId, note);
            }
            
            if (maskedAction == MotionEvent.ACTION_POINTER_UP ||
                   maskedAction == MotionEvent.ACTION_UP ||
                   maskedAction == MotionEvent.ACTION_CANCEL) {

                unsetKeyPressed((ImageButton)v);

                if (!mSustain) {
                    mSoundPlayer.stop(note);
                }
            }

            return true;
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

