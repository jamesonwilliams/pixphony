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

import android.support.annotation.NonNull;

import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.device.MidiOutputDevice;
import jp.kshoji.blemidi.listener.OnMidiDeviceAttachedListener;
import jp.kshoji.blemidi.listener.OnMidiDeviceDetachedListener;

public class BleConnectionListener implements 
        OnMidiDeviceAttachedListener, OnMidiDeviceDetachedListener {

    /*
     * By default, don't log. Override me to log somewhere.
     */
    public void onLog(String logText) {
    }

    @Override
    public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
        onLog("onMidiInputDeviceAttached(" + midiInputDevice.getDeviceName() + ", " + midiInputDevice.getDeviceAddress() + ")");
    }

    @Override
    public void onMidiOutputDeviceAttached(@NonNull MidiOutputDevice midiOutputDevice) {
        onLog("onMidiOutputDeviceAttached(" + midiOutputDevice.getDeviceName() + ")");
    }

    @Override
    public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
        onLog("onMidiInputDeviceDetached(" + midiInputDevice.getDeviceName() + ", " + midiInputDevice.getDeviceAddress() +")");
    }

    @Override
    public void onMidiOutputDeviceDetached(@NonNull MidiOutputDevice midiOutputDevice) {
        onLog("onMidiOutputDeviceDetached(" + midiOutputDevice.getDeviceName() + ")");
    }
}
