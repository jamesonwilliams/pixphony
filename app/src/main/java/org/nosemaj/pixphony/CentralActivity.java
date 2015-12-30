/*
 * Copyright 2015 Kaoru Shoji
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
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.util.BleUtils;

/**
 * Activity for BLE MIDI Central Application
 *
 * @author K.Shoji
 */
public class CentralActivity extends Activity {
    private static final String TAG = CentralActivity.class.getName();

    private static final int WHITE_KEY_COLOR = 0xFFFFFFFF;
    private static final int BLACK_KEY_COLOR = 0xFF808080;

    boolean isScanning = false;

    ArrayAdapter<String> midiInputEventAdapter;
    Spinner deviceSpinner;
    ArrayAdapter<MidiInputDevice> connectedDevicesAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView midiInputEventListView = (ListView) findViewById(R.id.midiInputEventListView);
        midiInputEventAdapter = new ArrayAdapter<>(this, R.layout.midi_event, R.id.midiEventDescriptionTextView);
        midiInputEventListView.setAdapter(midiInputEventAdapter);

        deviceSpinner = (Spinner) findViewById(R.id.deviceNameSpinner);
        connectedDevicesAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_spinner_dropdown_item, android.R.id.text1, new ArrayList<MidiInputDevice>());
        deviceSpinner.setAdapter(connectedDevicesAdapter);

        View.OnTouchListener onToneButtonTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int note = 60 + Integer.parseInt((String) v.getTag());

                Log.d(TAG, "onTouch() with note = " + note);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    default:
                        // do nothing.
                        break;
                }
                return false;
            }
        };

        findViewById(R.id.buttonC).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonCis).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonD).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonDis).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonE).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonF).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonFis).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonG).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonGis).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonA).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonAis).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonB).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonC2).setOnTouchListener(onToneButtonTouchListener);

        findViewById(R.id.buttonC).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonCis).getBackground().setColorFilter(BLACK_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonD).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonDis).getBackground().setColorFilter(BLACK_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonE).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonF).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonFis).getBackground().setColorFilter(BLACK_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonG).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonGis).getBackground().setColorFilter(BLACK_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonA).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonAis).getBackground().setColorFilter(BLACK_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonB).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonC2).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);

        Button disconnectButton = (Button) findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() disconnect button");
                MidiInputDevice bleMidiDeviceFromSpinner = null; //getBleMidiDeviceFromSpinner();
                if (bleMidiDeviceFromSpinner != null) {
                    // disconnect
                }
            }
        });

        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() settings button");
                Intent i = new Intent(CentralActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        if (!BleUtils.isBluetoothEnabled(this)) {
            BleUtils.enableBluetooth(this);
            return;
        }

        if (!BleUtils.isBleSupported(this)) {
            alertBleNotSupported();
        } else {
            alertBleOk();
        }

        PixphonyApplication app = (PixphonyApplication)getApplicationContext();
        Log.d(TAG, "the app says " + app.getConnectionManager());
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
                alertBleOk();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();

        // cleanup ble?
    }

    private void alertBleNotSupported() {
        alert(true,
              "Not supported",
              "Bluetooth LE is not supported on this device. The app will exit.");
    }

    private void alertBleOk() {
        /*
         * TODO: wtf?
         */
        alert(false,
              "Kewel BeanZ",
              "You're good to go. Enjoy the app.");
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
