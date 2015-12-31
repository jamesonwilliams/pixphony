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

import jp.kshoji.blemidi.central.BleMidiCentralProvider;
import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.device.MidiOutputDevice;
import jp.kshoji.blemidi.listener.OnMidiDeviceAttachedListener;
import jp.kshoji.blemidi.listener.OnMidiDeviceDetachedListener;
import jp.kshoji.blemidi.listener.OnMidiScanStatusListener;

import java.util.ArrayList;
import java.util.Set;

public class PixmobConnectionManager {
    private static final String TAG = PixmobConnectionManager.class.getName();
    /*
     * 0 scans forever -- TODO: good for testing, but release?
     */
    private static final int BLE_SCAN_TIME_MS = 0;

    private BleMidiCentralProvider mProvider;

    private ArrayList<MidiInputDevice> mConnectedDevices = new ArrayList<MidiInputDevice>();

    private static volatile PixmobConnectionManager sInstance = null;
    private static Context sContext = null;
    private PixmobConnectionManager() {}

    private PixmobConnectionManager(Context context) {
        Log.d(TAG, "PixmobConnectionManager(context)");
        sContext = context;
        setupCentralProvider();
    }

    public static synchronized PixmobConnectionManager getInstance(Context context) {
        Log.d(TAG, "getInstance()");
        if (sInstance == null) {
            sInstance = new PixmobConnectionManager(context);
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

    private PixmobDeviceListener mListener = null;

    public void setListener(PixmobDeviceListener l) {
        mListener = l;
    }

    BleMidiEventListener mBleMidiEventListener = new BleMidiEventListener() {
        @Override
        public void onMidiNoteOn(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
            super.onMidiNoteOn(sender, channel, note, velocity);

            if (velocity <= 0) {
                return;
            }

            if (mListener != null) {
                mListener.onDevicePlayed(sender, channel, note, velocity, true);
            }
        }

        @Override
        public void onMidiNoteOff(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
            super.onMidiNoteOff(sender, channel, note, velocity);

            if (velocity <= 0) {
                return;
            }

            if (mListener != null) {
                mListener.onDevicePlayed(sender, channel, note, velocity, false);
            }
        }

        @Override
        public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
            super.onMidiInputDeviceAttached(midiInputDevice);

            if (mListener != null) {
                mListener.onDeviceConnected(midiInputDevice);
            }
        }

        @Override
        public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
            super.onMidiInputDeviceDetached(midiInputDevice);

            if (mListener != null) {
                mListener.onDeviceDisconnected(midiInputDevice);
            }
        }

        @Override
        public void onLog(String text) {
            Log.d(TAG, text);
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

        mProvider.setOnMidiDeviceAttachedListener(mBleMidiEventListener);
        mProvider.setOnMidiDeviceDetachedListener(mBleMidiEventListener);
        mProvider.setOnMidiScanStatusListener(mScanStatusListener);

        mProvider.startScanDevice(BLE_SCAN_TIME_MS);
    }

    public ArrayList<MidiInputDevice> getConnectedDevices() {
        return new ArrayList<MidiInputDevice>(mProvider.getMidiInputDevices());
    }

    public void disconnectDevice(MidiInputDevice device) {
        mProvider.disconnectDevice(device);
    }

    public void disconnectAllDevices() {
        for (MidiInputDevice d : mProvider.getMidiInputDevices()) {
            disconnectDevice(d);
        }
    }
}

