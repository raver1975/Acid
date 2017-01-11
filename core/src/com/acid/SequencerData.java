package com.acid;

import synth.AcidSequencer;
import synth.Sequencer;

/**
 * Created by Paul on 1/10/2017.
 */
public class SequencerData {
    private final byte[] note = new byte[16];
    private final boolean[] pause = new boolean[16];
    private final boolean[] slide = new boolean[16];
    private final boolean[] accent = new boolean[16];
    public final SequencerData parent;
    public SequencerData child;

    public SequencerData() {

        for (int x1 = 0; x1 < 16; x1++) {
            note[x1] = Statics.output.getSequencer().bassline.note[x1];
            pause[x1] = Statics.output.getSequencer().bassline.pause[x1];
            slide[x1] = Statics.output.getSequencer().bassline.slide[x1];
            accent[x1] = Statics.output.getSequencer().bassline.accent[x1];
        }
        System.out.println("copying " + this);
        this.parent = Acid.currentSequence;
        if (this.parent != null) this.parent.child = this;
        Acid.currentSequence = this;
    }

    public void refresh() {
        for (int x1 = 0; x1 < 16; x1++) {
            Statics.output.getSequencer().bassline.note[x1] = note[x1];
            Statics.output.getSequencer().bassline.pause[x1] = pause[x1];
            Statics.output.getSequencer().bassline.slide[x1] = slide[x1];
            Statics.output.getSequencer().bassline.accent[x1] = accent[x1];
        }
        System.out.println("restoring " + this);
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < 16; i++) {
            s += note[i] + (pause[i] ? "p" : "") + (slide[i] ? "s" : "") + (accent[i] ? "a" : "") + " ";
        }
        return s;
    }

}
