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

import android.support.annotation.NonNull;

import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.listener.OnMidiInputEventListener;

public abstract class OnPixmobEventListener
        extends OnMidiInputEventListenerBaseImpl {
    public abstract void onMidiNote(@NonNull MidiInputDevice sender, int channel, int note, int velocity, boolean isOn);

    @Override
    public void onMidiNoteOff(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
        super.onMidiNoteOff(sender, channel, note, velocity);
        onMidiNote(sender, channel, note, velocity, false);
    }

    @Override
    public void onMidiNoteOn(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
        super.onMidiNoteOn(sender, channel, note, velocity);
        onMidiNote(sender, channel, note, velocity, true);
    }
}
