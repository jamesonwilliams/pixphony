package org.nosemaj.pixphony;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/*
 * https://en.wikipedia.org/wiki/Singleton_pattern
 */ 
public class SoundPlayer {
    private static final int MAX_STREAMS = 5;

    private static volatile SoundPlayer sInstance = null;
    private static Context sContext = null;

    private SoundPool mSoundPool = null;
    private int mSoundId = 0;
    private int mStreamId = 0;

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
        mStreamId = mSoundPool.play(mSoundId, 1f, 1f, 1, 0, 
                        mMappedInstrument.getPlaybackRate(midiNote));
    }

    public void stop() {
        if (0 != mStreamId) {
            mSoundPool.stop(mStreamId);
            mStreamId = 0;
        }
    }

    public void setMappedInstrument(Instrument instrument) {
        mMappedInstrument = instrument;
        mSoundPool.unload(mSoundId);
        mSoundId = mSoundPool.load(sContext, mMappedInstrument.getSample(), 1);
    }
}
