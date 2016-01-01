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

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioAttributes.Builder;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;

/*
 * https://en.wikipedia.org/wiki/Singleton_pattern
 */ 
public class SoundPlayer {
    public static final String TAG = SoundPlayer.class.getName();

    private static final int MAX_STREAMS = 20;

    private static volatile SoundPlayer sInstance = null;
    private static Context sContext = null;

    private SoundPool mSoundPool = null;
    private HashMap<Integer,Integer> mSoundIdMap = new HashMap<Integer,Integer>();
    private HashMap<Integer,Integer> mStreamIdMap = new HashMap<Integer, Integer>();
    private boolean isLoaded = false;

    private Instrument mMappedInstrument = null;

    private SoundPlayer(Context context) {
        sContext = context;
        mSoundPool = createSoundPool();
    }

    public static synchronized SoundPlayer getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SoundPlayer(context);
        }

        return sInstance;
    }

    private SoundPool createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
    
            return new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(MAX_STREAMS)
                .build();
        } else {
            return new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }
    }

    public void playMidiNote(int midiNote) {
        Log.d(TAG, "playMidiNote(" + midiNote + ")");
        if (!isLoaded) {
            return;
        }

        /*
         * One at a time, boys.
         */
        stop(midiNote);
        final int soundId = getIntSafe(mSoundIdMap, midiNote, 0);
        final int streamId = mSoundPool.play(soundId, 1f, 1f, 1, 0, 
                                mMappedInstrument.getPlaybackRate(midiNote));
        mStreamIdMap.put(midiNote, streamId);
    }

    private int getIntSafe(HashMap<Integer,Integer> map, int key, int defaultValue) {
        final Integer value = map.get(key);
        return (value == null) ? defaultValue : value.intValue();
    }

    public void stop(int midiNote) {
        final int streamId = getIntSafe(mStreamIdMap, midiNote, 0);
        if (0 != streamId) {
            mSoundPool.stop(streamId);
            mStreamIdMap.put(midiNote, 0);
        }
    }

    public void stop() {
        int streamId = 0;
        for (int i = 53; i <= 71; i++) {
            streamId = getIntSafe(mStreamIdMap, i, 0);
            if (0 != streamId) { 
                mSoundPool.stop(streamId);
                mStreamIdMap.put(i, 0);
            }
        }
    }

    public synchronized void load(int resourceId) {
        if (isLoaded) {
            for (int soundId : mSoundIdMap.values()) {
                mSoundPool.unload(soundId);
            }

            isLoaded = false;
        }

        for (int i = 53; i <= 71; i++) {
            int soundId = mSoundPool.load(sContext, resourceId, 1);
            mSoundIdMap.put(i, soundId);
        }

        isLoaded = true;
    }

    public void setMappedInstrument(Instrument instrument) {
        mMappedInstrument = instrument;
        load(mMappedInstrument.getDefaultSample());
    }

    public void setSample(String sampleName) {
        if (sampleName == null) {
            return;
        }

        final int sampleId = lookupSampleId(sampleName);

        if (sampleId != 0) {
            load(sampleId);
        }
    }

    /*
     * Bug in Android, can't get int type back from a list preference.
     * So need to do all this craziness. Since anyone who wants to load
     * a sample has to do it, it made most sense to put behind an API in
     * the SoundPlayer, but jesus h. christ. See
     *
     * https://code.google.com/p/android/issues/detail?id=2096
     */
    private int lookupSampleId(String name) {
        Resources resources = sContext.getResources();
        return resources.getIdentifier(name, "raw", sContext.getPackageName());
    }
}

