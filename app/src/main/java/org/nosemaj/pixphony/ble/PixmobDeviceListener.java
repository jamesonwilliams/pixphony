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

/*
 * For clients of the BleMidiConnectionManager, to get a summary
 * of the important changes.
 *
 * Do as a base class instead of an interface because someone might
 * actually really not care about connect and disconnect, and only want
 * to do stuff when note are played.
 */
public class PixmobDeviceListener {
    public void onDevicePlayed(@NonNull MidiInputDevice sender, int channel, int note, int velocity, boolean noteOn) {
        onLog("note played: " + sender.getDeviceName() + ": " + channel + ", " + note + ", " + velocity + ", " + noteOn);
    }

    public void onLog(final String text) {
    }
}

