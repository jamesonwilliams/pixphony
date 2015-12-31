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
import jp.kshoji.blemidi.device.MidiOutputDevice;
import jp.kshoji.blemidi.listener.OnMidiInputEventListener;
import jp.kshoji.blemidi.listener.OnMidiDeviceAttachedListener;
import jp.kshoji.blemidi.listener.OnMidiDeviceDetachedListener;

import java.util.Arrays;

/*
 * The generic interfaces have lots of stuff we don't need, so add a base
 * implementation layer so we don't have to implement all of these
 * elsewhere in our main logic.
 */
public class BleMidiEventListener implements OnMidiInputEventListener, 
       OnMidiDeviceAttachedListener, OnMidiDeviceDetachedListener {

    /*
     * By default, don't log. Override me to log somewhere.
     */
    public void onLog(String logText) {
    }

    @Override
    public void onMidiSystemExclusive(@NonNull MidiInputDevice sender, @NonNull byte[] systemExclusive) {
        onLog("SystemExclusive from: " + sender.getDeviceName() + ", data:" + Arrays.toString(systemExclusive));
    }

    @Override
    public void onMidiNoteOff(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
        onLog("NoteOff from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", velocity: " + velocity);
    }

    @Override
    public void onMidiNoteOn(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
        onLog("NoteOn from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", velocity: " + velocity);
    }

    @Override
    public void onMidiPolyphonicAftertouch(@NonNull MidiInputDevice sender, int channel, int note, int pressure) {
        onLog("PolyphonicAftertouch  from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", pressure: " + pressure);
    }

    @Override
    public void onMidiControlChange(@NonNull MidiInputDevice sender, int channel, int function, int value) {
        onLog("ControlChange from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value);
    }

    @Override
    public void onMidiProgramChange(@NonNull MidiInputDevice sender, int channel, int program) {
        onLog("ProgramChange from: " + sender.getDeviceName() + ", channel: " + channel + ", program: " + program);
    }

    @Override
    public void onMidiChannelAftertouch(@NonNull MidiInputDevice sender, int channel, int pressure) {
        onLog("ChannelAftertouch from: " + sender.getDeviceName() + ", channel: " + channel + ", pressure: " + pressure);
    }

    @Override
    public void onMidiPitchWheel(@NonNull MidiInputDevice sender, int channel, int amount) {
        onLog("PitchWheel from: " + sender.getDeviceName() + ", channel: " + channel + ", amount: " + amount);
    }

    @Override
    public void onMidiTimeCodeQuarterFrame(@NonNull MidiInputDevice sender, int timing) {
        onLog("TimeCodeQuarterFrame from: " + sender.getDeviceName() + ", timing: " + timing);
    }

    @Override
    public void onMidiSongSelect(@NonNull MidiInputDevice sender, int song) {
        onLog("SongSelect from: " + sender.getDeviceName() + ", song: " + song);
    }

    @Override
    public void onMidiSongPositionPointer(@NonNull MidiInputDevice sender, int position) {
        onLog("SongPositionPointer from: " + sender.getDeviceName() + ", position: " + position);
    }

    @Override
    public void onMidiTuneRequest(@NonNull MidiInputDevice sender) {
        onLog("TuneRequest from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiTimingClock(@NonNull MidiInputDevice sender) {
        onLog("TimingClock from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiStart(@NonNull MidiInputDevice sender) {
        onLog("Start from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiContinue(@NonNull MidiInputDevice sender) {
        onLog("Continue from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiStop(@NonNull MidiInputDevice sender) {
        onLog("Stop from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiActiveSensing(@NonNull MidiInputDevice sender) {
        onLog("ActiveSensing from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiReset(@NonNull MidiInputDevice sender) {
        onLog("Reset from: " + sender.getDeviceName());
    }

    @Override
    public void onRPNMessage(@NonNull MidiInputDevice sender, int channel, int function, int value) {
        onLog("RPN message from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value);
    }

    @Override
    public void onNRPNMessage(@NonNull MidiInputDevice sender, int channel, int function, int value) {
        onLog("NRPN message from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value);
    }

    @Override
    public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
        onLog("onMidiInputDeviceAttached(" + midiInputDevice.getDeviceName() + ")");
    }

    @Override
    public void onMidiOutputDeviceAttached(@NonNull MidiOutputDevice midiOutputDevice) {
        onLog("onMidiOutputDeviceAttached(" + midiOutputDevice.getDeviceName() + ")");
    }

    @Override
    public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
        onLog("onMidiInputDeviceDetached(" + midiInputDevice.getDeviceName() + ")");
    }

    @Override
    public void onMidiOutputDeviceDetached(@NonNull MidiOutputDevice midiOutputDevice) {
        onLog("onMidiOutputDeviceDetached(" + midiOutputDevice.getDeviceName() + ")");
    }
}

