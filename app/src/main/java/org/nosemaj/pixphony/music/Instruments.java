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

package org.nosemaj.pixphony.music;

import org.nosemaj.pixphony.R;

public class Instruments {
    public static final int PANFLUTE = 1;
    public static final int PIANO = 2;
    public static final int SYNTH = 3;
    public static final int BASS_SYNTH = 4;

    public static final int PIXMOB_LOWEST_MIDI_NOTE = 53;

    private static Instrument mPanflute = new Instrument(R.raw.panflute_d) {
        @Override
        public float getPlaybackRate(int midiNote) {
            return FrequencyTable.get(midiNote - PIXMOB_LOWEST_MIDI_NOTE);
        }
    };

    private static Instrument mPiano = new Instrument(R.raw.piano_c) {
        @Override
        public float getPlaybackRate(int midiNote) {
            return FrequencyTable.get(midiNote - PIXMOB_LOWEST_MIDI_NOTE);
        }
    };

    private static Instrument mSynthesizer = new Instrument(R.raw.synth_c) {
        @Override
        public float getPlaybackRate(int midiNote) {
            return FrequencyTable.get(midiNote - PIXMOB_LOWEST_MIDI_NOTE);
        }
    };

    private static Instrument mBassSynth = new Instrument(R.raw.moog_e) {
        @Override
        public float getPlaybackRate(int midiNote) {
            return FrequencyTable.get(midiNote - PIXMOB_LOWEST_MIDI_NOTE);
        }
    };

    public static Instrument get(String sampleName) {
        switch (sampleName) {
            case "piano_c":
                return mPiano;
            case "panflute_d":
                return mPanflute;
            case "synth_c":
                return mSynthesizer;
            case "moog_e":
                return mBassSynth;
            default:
                return null;
        }
    }

    public static Instrument get(int instrumentType) {
        switch (instrumentType) {
            case PIANO:
                return mPiano;
            case PANFLUTE:
                return mPanflute;
            case SYNTH:
                return mSynthesizer;
            case BASS_SYNTH:
                return mBassSynth;
            default:
                return null;
        }
    }
}

