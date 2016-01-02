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

package org.nosemaj.pixphony.ble;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import jp.kshoji.blemidi.central.BleMidiCentralProvider;
import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.listener.OnMidiScanStatusListener;

import java.util.ArrayList;

public class PixmobConnectionManager {
    private static final String TAG = PixmobConnectionManager.class.getName();
    /*
     * 0 scans forever -- TODO: good for testing, but release?
     */
    private static final int BLE_SCAN_TIME_MS = 0;

    private BleMidiCentralProvider mProvider;
    private boolean mIsInitialized = false;

    private static volatile PixmobConnectionManager sInstance = null;
    private static Context sContext = null;
    private PixmobConnectionManager() {}

    private PixmobConnectionManager(Context context) {
        Log.d(TAG, "PixmobConnectionManager(context)");
        sContext = context;
    }

    public static synchronized PixmobConnectionManager getInstance(Context context) {
        Log.d(TAG, "getInstance()");
        if (sInstance == null) {
            sInstance = new PixmobConnectionManager(context);
        }

        return sInstance;
    }

    public boolean isInitialized() {
        return mIsInitialized;
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
        mIsInitialized = false;
    }

    private BleConnectionListener mClientConnectionListener = null;
    private PixmobDeviceListener mClientDeviceListener = null;

    public void setBleConnectionListener(BleConnectionListener l) {
        mClientConnectionListener = l;
    }

    public void setPixmobDeviceListener(PixmobDeviceListener l) {
        mClientDeviceListener = l;
    }

    BleMidiEventListener mBleMidiEventListener = new BleMidiEventListener() {
        @Override
        public void onMidiNoteOn(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
            super.onMidiNoteOn(sender, channel, note, velocity);

            if (velocity <= 0) {
                return;
            }

            if (mClientDeviceListener != null) {
                mClientDeviceListener.onDevicePlayed(sender, channel, note, velocity, true);
            } else {
                Log.d(TAG, "mClientDeviceListener is null.");
            }
        }

        @Override
        public void onMidiNoteOff(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
            super.onMidiNoteOff(sender, channel, note, velocity);

            if (velocity <= 0) {
                return;
            }

            if (mClientDeviceListener != null) {
                Log.d(TAG, "dispatching to listener " + mClientDeviceListener.toString());
                mClientDeviceListener.onDevicePlayed(sender, channel, note, velocity, false);
            } else {
                Log.d(TAG, "mClientDeviceListener is null.");
            }
        }

        @Override
        public void onLog(String text) {
            Log.d(TAG, text);
        }
    };

    BleConnectionListener mBleConnectionListener = new BleConnectionListener() {
        @Override
        public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
            for (MidiInputDevice d : getConnectedDevices()) {
                if (midiInputDevice.getDeviceAddress().equals(d.getDeviceAddress()) &&
                        d != midiInputDevice) {
                    Log.w(TAG, "GOT DUPLICATE DEVICE, DISCONNECTING THE OLD ONE!");
                    disconnectDevice(d);
                }
            }

            super.onMidiInputDeviceAttached(midiInputDevice);
            midiInputDevice.setOnMidiInputEventListener(mBleMidiEventListener);

            if (mClientConnectionListener != null) {
                Log.d(TAG, "dispatching to listener " + mClientConnectionListener.toString());
                mClientConnectionListener.onMidiInputDeviceAttached(midiInputDevice);
            } else {
                Log.d(TAG, "mClientConnectionListener is null.");
            }
        }

        @Override
        public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
            super.onMidiInputDeviceDetached(midiInputDevice);

            if (mClientConnectionListener != null) {
                Log.d(TAG, "dispatching to listener " + mClientConnectionListener.toString());
                mClientConnectionListener.onMidiInputDeviceDetached(midiInputDevice);
            } else {
                Log.d(TAG, "mClientConnectionListener is null.");
            }
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
    public void init() {
        Log.d(TAG, "setupCentralProvider()");

        if (!isInitialized()) {
            mProvider = new BleMidiCentralProvider(sContext);
            attachListeners();
            mIsInitialized = true;
        }
    }

    public void attachListeners() {
        mProvider.setOnMidiScanStatusListener(mScanStatusListener);
        mProvider.setOnMidiDeviceAttachedListener(mBleConnectionListener);
        mProvider.setOnMidiDeviceDetachedListener(mBleConnectionListener);
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

