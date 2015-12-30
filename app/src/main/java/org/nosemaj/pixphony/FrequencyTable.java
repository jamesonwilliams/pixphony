package org.nosemaj.pixphony;

public class FrequencyTable {
    private static final float BASE_MODULATION = 1f;

    /*
     * Scaling taken from
     * http://en.wikipedia.org/wiki/Pythagorean_tuning#Method
     */
    private static final float UNISON           = BASE_MODULATION;
    private static final float MINOR_SECOND     = UNISON * (256f / 243);
    private static final float MAJOR_SECOND     = UNISON * (9f / 8);
    private static final float MINOR_THIRD      = UNISON * (32f / 27);
    private static final float MAJOR_THIRD      = UNISON * (81f / 64);
    private static final float PERFECT_FOURTH   = UNISON * (4f / 3);
    private static final float AUGMENTED_FOURTH = UNISON * (1024f / 729);
    private static final float DIMINISHED_FIFTH = AUGMENTED_FOURTH;
    private static final float PERFECT_FIFTH    = UNISON * (3f / 2);
    private static final float MINOR_SIXTH      = UNISON * (128f / 81);
    private static final float MAJOR_SIXTH      = UNISON * (27f / 16);
    private static final float MINOR_SEVENTH    = UNISON * (16f / 9);
    private static final float MAJOR_SEVENTH    = UNISON * (243f / 128);
    private static final float OCTAVE_UP        = UNISON * 2f;

    private static final float CHROMATIC_SCALE[] = {
        UNISON,
        MINOR_SECOND,
        MAJOR_SECOND,
        MINOR_THIRD,
        MAJOR_THIRD,
        PERFECT_FOURTH,
        DIMINISHED_FIFTH,
        PERFECT_FIFTH,
        MINOR_SIXTH,
        MAJOR_SIXTH,
        MINOR_SEVENTH,
        MAJOR_SEVENTH,
    };

    public static float get(int n) {
        final int which_note = n % CHROMATIC_SCALE.length;
        final int which_octave = n / CHROMATIC_SCALE.length;
        return CHROMATIC_SCALE[which_note] * (1 + which_octave);
    }
}

