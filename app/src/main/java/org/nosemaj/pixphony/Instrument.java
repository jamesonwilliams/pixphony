package org.nosemaj.pixphony;

public abstract class Instrument {
    private int mSample;

    public int getSample() {
        return mSample;
    }

    public Instrument(int sample) {
        mSample = sample;
    }

    public abstract float getPlaybackRate(int midiNote);
}
