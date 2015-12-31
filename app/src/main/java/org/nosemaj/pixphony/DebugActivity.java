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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;

import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.util.BleUtils;

import java.util.ArrayList;

public class DebugActivity extends Activity {
    private static final String TAG = DebugActivity.class.getName();

    private static final int WHITE_KEY_COLOR = 0xFFFFFFFF;
    private static final int BLACK_KEY_COLOR = 0xFF808080;

    private PixmobConnectionManager mConnectionManager = null;
    private SoundPlayer mSoundPlayer = null;

    private boolean mBleInitialized = false;

    private ArrayAdapter<String> mEventLogAdapter;

    private ArrayList<MidiInputDevice> mDeviceList = new ArrayList<MidiInputDevice>();
    private ArrayAdapter<MidiInputDevice> mDeviceAdapter;
    private Spinner mDeviceSpinner;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupListView();
        setupSpinner();
        setupKeys();
        setupButtons();

        setupSoundPlayer();

        if (checkForBle()) {
            mBleInitialized = false;
            setupBleMidi();
        }
    }

    private void setupSoundPlayer() {
        mSoundPlayer = ((PixphonyApplication)getApplicationContext()).getSoundPlayer();
        mSoundPlayer.setMappedInstrument(Instruments.get(Instruments.PANFLUTE));
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

    private void setupListView() {
        ListView eventLogView = 
            (ListView) findViewById(R.id.midiInputEventListView);
        mEventLogAdapter = 
            new ArrayAdapter<>(this, R.layout.midi_event, R.id.midiEventDescriptionTextView);
        eventLogView.setAdapter(mEventLogAdapter);
    }

    private void setupButtons() {
        Button disconnectButton = (Button) findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() disconnect button");
                MidiInputDevice device = getDeviceFromSpinner();
                if (device != null) {
                    mConnectionManager.disconnectDevice(device);
                }
            }
        });

        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() settings button");
                Intent i = new Intent(DebugActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });
    }

    private void setupKeys() {
        final int whiteKeys[] = {
            R.id.buttonC, R.id.buttonD, R.id.buttonE, R.id.buttonF,
            R.id.buttonG, R.id.buttonA, R.id.buttonB, R.id.buttonC2
        };

        final int blackKeys[] = {
            R.id.buttonCis, R.id.buttonDis, R.id.buttonFis, R.id.buttonGis,
            R.id.buttonAis
        };

        for (int id : whiteKeys) {
            Button b = (Button) findViewById(id);
            b.setOnTouchListener(mToneTouchListener);
            b.getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        }

        for (int id : blackKeys) {
            Button b = (Button) findViewById(id);
            b.setOnTouchListener(mToneTouchListener);
            b.getBackground().setColorFilter(BLACK_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        }
    }

    View.OnTouchListener mToneTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int note = 60 + Integer.parseInt((String) v.getTag());

            Log.d(TAG, "onTouch() with note = " + note);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mSoundPlayer.playMidiNote(note);
                    break;
                case MotionEvent.ACTION_UP:
                    mSoundPlayer.stop();
                    break;
                default:
                    // do nothing.
                    break;
            }

            return false;
        }
    };

    private void setupSpinner() {
        mDeviceSpinner = (Spinner) findViewById(R.id.deviceNameSpinner);
        mDeviceAdapter = 
            new ArrayAdapter<>(getApplicationContext(), R.layout.simple_spinner_dropdown_item,
                               android.R.id.text1, mDeviceList);
        mDeviceSpinner.setAdapter(mDeviceAdapter);
        mDeviceSpinner.setOnItemSelectedListener(mSpinnerListener);
    }

    OnItemSelectedListener mSpinnerListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Log.d(TAG, mDeviceSpinner.getItemAtPosition(arg2).toString());
            mConnectionManager.attachListeners();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
            MidiInputDevice d = getDeviceFromSpinner();
            if (d == null) {
                return;
            }

            Log.d(TAG, d.toString());
            mConnectionManager.attachListeners();
        }
    }; 

    PixmobDeviceListener mPixmobListener = new PixmobDeviceListener() {
        @Override
        public void onDevicePlayed(@NonNull MidiInputDevice sender, int channel, int note, int velocity, boolean noteOn) {
            super.onDevicePlayed(sender, channel, note, velocity, noteOn);
            mSoundPlayer.playMidiNote(note);
        }

        @Override
        public void onDeviceConnected(@NonNull MidiInputDevice midiInputDevice) {
            super.onDeviceConnected(midiInputDevice);
            updateDevicesAdapter(midiInputDevice, true);
        }

        @Override
        public void onDeviceDisconnected(@NonNull MidiInputDevice midiInputDevice) {
            super.onDeviceDisconnected(midiInputDevice);
            updateDevicesAdapter(midiInputDevice, false);
        }

        @Override
        public void onLog(String text) {
            updateListAdapter(text);
        }
    };

    private void updateListAdapter(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEventLogAdapter.add(text);
            }
        });
    }

    private void updateDevicesAdapter(final MidiInputDevice device, final boolean isConnected) {
        Log.d(TAG, "updateDevicesAdapter() is called.");
        runOnUiThread(new Runnable() {
             @Override
             public void run() {
                /*
                 * Try to remove even if we are adding, to make sure it isn't there twice.
                 */
                mDeviceAdapter.remove(device);

                if (isConnected) {
                    mDeviceAdapter.add(device);
                }

                mDeviceAdapter.notifyDataSetChanged();
            }
        });
    }

    MidiInputDevice getDeviceFromSpinner() {
        if (mDeviceSpinner != null && 
            mDeviceSpinner.getSelectedItemPosition() >= 0 && 
            mDeviceAdapter != null &&
            !mDeviceAdapter.isEmpty()) {

            MidiInputDevice device = mDeviceAdapter.getItem(mDeviceSpinner.getSelectedItemPosition());
            if (device != null) {
                return device;
            }
        }

        return null;
    }

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
            } else {
                setupBleMidi();
            }
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

