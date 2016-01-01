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

import java.util.HashMap;

public abstract class Instrument {
    private HashMap<Integer,Integer> mSoundIdMap = new HashMap<Integer,Integer>();
    private HashMap<Integer,Integer> mStreamIdMap = new HashMap<Integer, Integer>();

    private boolean mLoaded = false;
    private int mSample;

    public int getDefaultSample() {
        return mSample;
    }

    public Instrument(int sample) {
        mSample = sample;
    }

    public abstract float getPlaybackRate(int midiNote);

    public boolean isLoaded() {
        return mLoaded;
    }

    public void setLoaded(boolean loaded) {
        mLoaded = loaded;
    }

    public HashMap<Integer,Integer> getSoundIdMap() {
        return mSoundIdMap;
    }

    public HashMap<Integer,Integer> getStreamIdMap() {
        return mStreamIdMap;
    }
}
