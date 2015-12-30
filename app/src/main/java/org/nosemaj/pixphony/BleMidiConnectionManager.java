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

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

/* TODO: remove these after testing */
import android.os.Handler;
import android.os.Message;

import jp.kshoji.blemidi.central.BleMidiCentralProvider;
import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.device.MidiOutputDevice;
import jp.kshoji.blemidi.listener.OnMidiDeviceAttachedListener;
import jp.kshoji.blemidi.listener.OnMidiDeviceDetachedListener;
import jp.kshoji.blemidi.listener.OnMidiScanStatusListener;

public class BleMidiConnectionManager {
    private static final String TAG = BleMidiConnectionManager.class.getName();
    /*
     * 0 scans forever -- TODO: good for testing, but release?
     */
    private static final int BLE_SCAN_TIME_MS = 0;

    private BleMidiCentralProvider mProvider;
    private MidiInputDevice mConnectedDevice = null;

    private static volatile BleMidiConnectionManager sInstance = null;
    private static Context sContext = null;

    SoundPlayer soundPlayer;

    private BleMidiConnectionManager() {}

    private BleMidiConnectionManager(Context context) {
        Log.d(TAG, "BleMidiConnectionManager(context)");
        sContext = context;
        setupCentralProvider();

        soundPlayer = SoundPlayer.getInstance(sContext);
        soundPlayer.setMappedInstrument(Instruments.get(Instruments.PANFLUTE));
    }

    public static synchronized BleMidiConnectionManager getInstance(Context context) {
        Log.d(TAG, "getInstance()");
        if (sInstance == null) {
            sInstance = new BleMidiConnectionManager(context);
        }

        return sInstance;
    }

    public void startScan() {
        Log.d(TAG, "startScan()");
        mProvider.startScanDevice(BLE_SCAN_TIME_MS);
    }

    public void stopScan() {
        Log.d(TAG, "stopScan()");
        mProvider.stopScanDevice();
    }

    public void terminiate() {
        Log.d(TAG, "terminate()");
        mProvider.terminate();
    }

    OnPixmobEventListener mPixmobEventListener = new OnPixmobEventListener() {
        @Override
        public void onMidiNote(@NonNull MidiInputDevice sender, int channel, int note, int velocity, boolean isOn) {
            if (velocity > 0) {
                soundPlayer.playMidiNote(note);
            }
        }
    };

    private OnMidiDeviceAttachedListener mDeviceAttachedListener =
            new OnMidiDeviceAttachedListener() {
        @Override
        public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
            // device attached
            Log.d(TAG, "onMidiInputDeviceAttached(" + midiInputDevice.getDeviceName() + ")");
            midiInputDevice.setOnMidiInputEventListener(mPixmobEventListener);
        }

        @Override
        public void onMidiOutputDeviceAttached(@NonNull MidiOutputDevice midiOutputDevice) {
            Log.d(TAG, "onMidiOutputDeviceAttached(" + midiOutputDevice.getDeviceName() + ")");
        }
    };

    private OnMidiDeviceDetachedListener mDeviceDetachedListener =
            new OnMidiDeviceDetachedListener() {
        @Override
        public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
            // device dettached
            Log.d(TAG, "onMidiInputDeviceDetached(" + midiInputDevice.getDeviceName() + ")");
        }

        @Override
        public void onMidiOutputDeviceDetached(@NonNull MidiOutputDevice midiOutputDevice) {
            Log.d(TAG, "onMidiOutputDeviceDetached(" + midiOutputDevice.getDeviceName() + ")");
        }
    };

    private OnMidiScanStatusListener mScanStatusListener =
            new OnMidiScanStatusListener() {
        @Override
        public void onMidiScanStatusChanged(boolean isScanning) {
            // scan state has changed
            Log.d(TAG, "onMidiScanStatusChanged(" + isScanning +")");
        }
    };

    /**
     * Configure BleMidiCentralProvider instance
     */
    private void setupCentralProvider() {
        Log.d(TAG, "setupCentralProvider()");
        mProvider = new BleMidiCentralProvider(sContext);

        mProvider.setOnMidiDeviceAttachedListener(mDeviceAttachedListener);
        mProvider.setOnMidiDeviceDetachedListener(mDeviceDetachedListener);
        mProvider.setOnMidiScanStatusListener(mScanStatusListener);

        mProvider.startScanDevice(BLE_SCAN_TIME_MS);
    }

    public void disconnectDevice() {
        Log.d(TAG, "disconnectDevice()");
        mProvider.disconnectDevice(mConnectedDevice);
    }
}

