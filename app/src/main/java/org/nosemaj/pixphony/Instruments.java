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

public class Instruments {
    public static final int PANFLUTE = 1;
    public static final int PIXMOB_LOWEST_MIDI_NOTE = 55;

    private static Instrument mPanflute = new Instrument(R.raw.panflute) {
        @Override
        public float getPlaybackRate(int midiNote) {
            return FrequencyTable.get(midiNote - PIXMOB_LOWEST_MIDI_NOTE);
        }
    };

    public static Instrument get(int instrument) {
        switch (instrument) {
            case PANFLUTE:
                return mPanflute;
            default:
                return null;
        }
    }
}

