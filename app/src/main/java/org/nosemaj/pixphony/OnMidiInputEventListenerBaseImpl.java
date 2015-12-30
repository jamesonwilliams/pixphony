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
import android.util.Log;

import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.listener.OnMidiInputEventListener;

import java.util.Arrays;

/*
 * The generic interface has lots of stuff we don't need, so add a base
 * implementation layer to get rid of the stuff we don't need.
 */
public class OnMidiInputEventListenerBaseImpl implements OnMidiInputEventListener {
    private static final String TAG = OnMidiInputEventListenerBaseImpl.class.getName();

    private void log(String logText) {
        Log.d(TAG, logText);
    }

    @Override
    public void onMidiSystemExclusive(@NonNull MidiInputDevice sender, @NonNull byte[] systemExclusive) {
        log("SystemExclusive from: " + sender.getDeviceName() + ", data:" + Arrays.toString(systemExclusive));
    }

    @Override
    public void onMidiNoteOff(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
        log("NoteOff from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", velocity: " + velocity);
    }

    @Override
    public void onMidiNoteOn(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
        log("NoteOn from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", velocity: " + velocity);
    }

    @Override
    public void onMidiPolyphonicAftertouch(@NonNull MidiInputDevice sender, int channel, int note, int pressure) {
        log("PolyphonicAftertouch  from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", pressure: " + pressure);
    }

    @Override
    public void onMidiControlChange(@NonNull MidiInputDevice sender, int channel, int function, int value) {
        log("ControlChange from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value);
    }

    @Override
    public void onMidiProgramChange(@NonNull MidiInputDevice sender, int channel, int program) {
        log("ProgramChange from: " + sender.getDeviceName() + ", channel: " + channel + ", program: " + program);
    }

    @Override
    public void onMidiChannelAftertouch(@NonNull MidiInputDevice sender, int channel, int pressure) {
        log("ChannelAftertouch from: " + sender.getDeviceName() + ", channel: " + channel + ", pressure: " + pressure);
    }

    @Override
    public void onMidiPitchWheel(@NonNull MidiInputDevice sender, int channel, int amount) {
        log("PitchWheel from: " + sender.getDeviceName() + ", channel: " + channel + ", amount: " + amount);
    }

    @Override
    public void onMidiTimeCodeQuarterFrame(@NonNull MidiInputDevice sender, int timing) {
        log("TimeCodeQuarterFrame from: " + sender.getDeviceName() + ", timing: " + timing);
    }

    @Override
    public void onMidiSongSelect(@NonNull MidiInputDevice sender, int song) {
        log("SongSelect from: " + sender.getDeviceName() + ", song: " + song);
    }

    @Override
    public void onMidiSongPositionPointer(@NonNull MidiInputDevice sender, int position) {
        log("SongPositionPointer from: " + sender.getDeviceName() + ", position: " + position);
    }

    @Override
    public void onMidiTuneRequest(@NonNull MidiInputDevice sender) {
        log("TuneRequest from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiTimingClock(@NonNull MidiInputDevice sender) {
        log("TimingClock from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiStart(@NonNull MidiInputDevice sender) {
        log("Start from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiContinue(@NonNull MidiInputDevice sender) {
        log("Continue from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiStop(@NonNull MidiInputDevice sender) {
        log("Stop from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiActiveSensing(@NonNull MidiInputDevice sender) {
        log("ActiveSensing from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiReset(@NonNull MidiInputDevice sender) {
        log("Reset from: " + sender.getDeviceName());
    }

    @Override
    public void onRPNMessage(@NonNull MidiInputDevice sender, int channel, int function, int value) {
        log("RPN message from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value);
    }

    @Override
    public void onNRPNMessage(@NonNull MidiInputDevice sender, int channel, int function, int value) {
        log("NRPN message from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value);
    }
}

