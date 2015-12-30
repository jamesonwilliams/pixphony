package org.nosemaj.pixphony;

public class Instruments {
    public static final int PANFLUTE = 1;
    private static final int PIXMOB_LOWEST_NOTE = 55;

    private static Instrument mPanflute = new Instrument(R.raw.panflute) {
        @Override
        public float getPlaybackRate(int midiNote) {
            return FrequencyTable.get(midiNote - PIXMOB_LOWEST_NOTE);
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

