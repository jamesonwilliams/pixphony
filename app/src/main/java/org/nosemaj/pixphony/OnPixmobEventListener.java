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

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.listener.OnMidiInputEventListener;

import java.util.Arrays;

/*
 * The generic interface has lots of stuff we don't need, so only handle
 * the things we actually do use.
 */
public class OnPixmobEventListener implements OnMidiInputEventListener {
    private Handler mEventHandler = null;

    public void setHandler(Handler midiInputEventHandler) {
        mEventHandler = midiInputEventHandler;
    }

    private void sendLogMessage(String logText) {
        final Message msg = 
            Message.obtain(mEventHandler, Pixmob.MSG_LOG_EVENT, logText);

        mEventHandler.sendMessage(msg);
    }

    private void sendMidiNote(int note) {
        final Integer noteObj = new Integer(note);
        final Message msg =
            Message.obtain(mEventHandler, Pixmob.MSG_PLAY_NOTE, noteObj);

        mEventHandler.sendMessage(msg);
    }

    @Override
    public void onMidiSystemExclusive(@NonNull MidiInputDevice sender, @NonNull byte[] systemExclusive) {
        sendLogMessage("SystemExclusive from: " + sender.getDeviceName() + ", data:" + Arrays.toString(systemExclusive));
    }

    @Override
    public void onMidiNoteOff(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
        sendLogMessage("NoteOff from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", velocity: " + velocity);

        if (velocity > 0) {
            sendMidiNote(note);
        }
    }

    @Override
    public void onMidiNoteOn(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
        sendLogMessage("NoteOn from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", velocity: " + velocity);

        if (velocity > 0) {
            sendMidiNote(note);
        }
    }

    @Override
    public void onMidiPolyphonicAftertouch(@NonNull MidiInputDevice sender, int channel, int note, int pressure) {
        sendLogMessage("PolyphonicAftertouch  from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", pressure: " + pressure);
    }

    @Override
    public void onMidiControlChange(@NonNull MidiInputDevice sender, int channel, int function, int value) {
        sendLogMessage("ControlChange from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value);
    }

    @Override
    public void onMidiProgramChange(@NonNull MidiInputDevice sender, int channel, int program) {
        sendLogMessage("ProgramChange from: " + sender.getDeviceName() + ", channel: " + channel + ", program: " + program);
    }

    @Override
    public void onMidiChannelAftertouch(@NonNull MidiInputDevice sender, int channel, int pressure) {
        sendLogMessage("ChannelAftertouch from: " + sender.getDeviceName() + ", channel: " + channel + ", pressure: " + pressure);
    }

    @Override
    public void onMidiPitchWheel(@NonNull MidiInputDevice sender, int channel, int amount) {
        sendLogMessage("PitchWheel from: " + sender.getDeviceName() + ", channel: " + channel + ", amount: " + amount);
    }

    @Override
    public void onMidiTimeCodeQuarterFrame(@NonNull MidiInputDevice sender, int timing) {
        sendLogMessage("TimeCodeQuarterFrame from: " + sender.getDeviceName() + ", timing: " + timing);
    }

    @Override
    public void onMidiSongSelect(@NonNull MidiInputDevice sender, int song) {
        sendLogMessage("SongSelect from: " + sender.getDeviceName() + ", song: " + song);
    }

    @Override
    public void onMidiSongPositionPointer(@NonNull MidiInputDevice sender, int position) {
        sendLogMessage("SongPositionPointer from: " + sender.getDeviceName() + ", position: " + position);
    }

    @Override
    public void onMidiTuneRequest(@NonNull MidiInputDevice sender) {
        sendLogMessage("TuneRequest from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiTimingClock(@NonNull MidiInputDevice sender) {
        sendLogMessage("TimingClock from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiStart(@NonNull MidiInputDevice sender) {
        sendLogMessage("Start from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiContinue(@NonNull MidiInputDevice sender) {
        sendLogMessage("Continue from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiStop(@NonNull MidiInputDevice sender) {
        sendLogMessage("Stop from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiActiveSensing(@NonNull MidiInputDevice sender) {
        sendLogMessage("ActiveSensing from: " + sender.getDeviceName());
    }

    @Override
    public void onMidiReset(@NonNull MidiInputDevice sender) {
        sendLogMessage("Reset from: " + sender.getDeviceName());
    }

    @Override
    public void onRPNMessage(@NonNull MidiInputDevice sender, int channel, int function, int value) {
        sendLogMessage("RPN message from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value);
    }

    @Override
    public void onNRPNMessage(@NonNull MidiInputDevice sender, int channel, int function, int value) {
        sendLogMessage("NRPN message from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value);
    }
}

