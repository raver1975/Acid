package com.acid;

import com.badlogic.gdx.Gdx;
import synth.BasslineSynthesizer;
import synth.Output;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Paul on 1/8/2017.
 */
public class KnobImpl {
    static double[][] knobs = new double[16][10];

    static {
        for (int i = 0; i < 16; i++) {
            knobs[i] = getControls();
        }
    }

    static boolean[] touched = new boolean[10];

    static boolean isTouched() {
        for (int i = 0; i < 6; i++) {
            if (touched[i]) return true;
        }
        return false;
    }

    public static float getRotation(int id, double val) {
        float rotation = 0f;
        switch (id) {
            case 0:
                rotation = (int) (((val - .5f) * 400.0) / 1.5f);
                break;
            case 1:
                rotation = (float) ((val - 1200) * 5.0f) / 50f;
                break;

            case 2:
                rotation = (float) (val * 500f) - 100f;
                break;

            case 3:
                rotation = (int) ((val * 500) - 100);
                break;

            case 4:
                rotation = (float) (((20 - val) * 640) / 20.0f) - 100f;
                break;

            case 5:
                rotation = (float) val * 360f;
                break;
            case 6:
                //accent
                rotation = (float) val;
                break;
            case 7:
                rotation = (float) val * 360f;
                break;
            case 8:

                rotation = (float)val/44100f * 360f;

                break;
            case 9:
                rotation = (float) val * 360f;
                break;
        }
        return rotation;
    }


    public static float getRotation(int id) {
        float rotation = 0f;
        switch (id) {
            case 0:
                rotation = (int) (((((BasslineSynthesizer) Statics.output
                        .getTrack(0)).tune - .5f) * 400.0) / 1.5f);

                break;
            case 1:
                rotation = (float) ((((BasslineSynthesizer) Statics.output.getTrack(0)).cutoff
                        .getValue() - 1200) * 5.0f) / 50f;
                break;

            case 2:
                rotation = (float) (((BasslineSynthesizer) Statics.output.getTrack(0)).resonance
                        .getValue() * 500f) - 100f;
                break;

            case 3:
                rotation = (int) ((((BasslineSynthesizer) Statics.output
                        .getTrack(0)).envMod * 500) - 100);
                break;

            case 4:
                rotation = (float) ((((20 - ((BasslineSynthesizer) Statics.output
                        .getTrack(0)).decay)) * 640) / 20.0f) - 100f;
                break;

            case 5:
                //accent
                rotation = (float) ((BasslineSynthesizer) Statics.output.getTrack(0)).accent * 360f;
                break;
            case 6:

                rotation = (float) Statics.output.getSequencer1().bpm;
                break;
            case 7:
                rotation = (float) Statics.output.getVolume() * 360f;
                break;
            case 8:
                rotation = (float) Output.getDelay().getTime()/44100f * 360f;
                break;
            case 9:
                rotation = (float) Output.getDelay().getFeedback() * 360f;
                break;

        }
        return rotation;
    }

    //    public static int[] knobVals=new int[8];
    public static void touchDragged(int id, float offset) {
        int cc = (int) (127f / 2f - offset);
        BasslineSynthesizer synth=Statics.synth1;
        if (Acid.drumsSelected==1){
            synth=Statics.synth2;
        }
        switch (id) {
            case 0:
                // tune
                synth.controlChange(33, cc);
                break;
            case 1:
                //cutoff
                synth.controlChange(34, cc);
                break;

            case 2:
                //resonance
                synth.controlChange(35, cc);
                break;

            case 3:
                //envelope
                synth.controlChange(36, cc);
                break;

            case 4:
                //decay
                synth.controlChange(37, cc);
                break;

            case 5:
                //accent
                synth.controlChange(38, cc);
                break;
            case 6:
                //bpm
                if (cc >= -100 & cc <= 260) {
                    Statics.output.getSequencer1().setBpm(cc + 100);
                    Statics.output.getSequencer2().setBpm(cc + 100);
//                    knobVals[id]=cc+100;
                }
                break;
            case 7:
                //volume
                synth.controlChange(39, cc);
                break;
            case 8:
                //Delay time
                Output.getDelay().controlChange(40, cc);
                break;
            case 9:
                Output.getDelay().controlChange(41, cc);
        }
        KnobData.factory();
    }

    public static void refill() {
        double[] contrls = KnobImpl.getControls();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 6; j++) {
                KnobImpl.knobs[i][j] = contrls[j];
            }
        }
        KnobData.factory();
    }

    public static void setControl(int step, int id) {
        if (Statics.free) {
            for (int i = 0; i < 16; i++) {
                knobs[i % 16][id] = getControls()[id];
            }
        } else {
            knobs[step % 16][id] = getControls()[id];
        }
    }

    public static double[] getControl(int step) {
        return knobs[step % 16];
    }

    public static double[] getControls() {
        BasslineSynthesizer synth=Statics.synth1;
        if (Acid.drumsSelected==1){
            synth=Statics.synth2;
        }
        double[] vals = new double[10];
        vals[0] = synth.tune;
        vals[1] = synth.cutoff.getValue();
        vals[2] = synth.resonance.getValue();
        vals[3] = synth.envMod;
        vals[4] = synth.decay;
        vals[5] = synth.accent;
        vals[6] = Statics.output.getSequencer1().bpm;
        vals[7] = Output.volume;
        vals[8] = Output.getDelay().getTime();
        vals[9] = Output.getDelay().getFeedback();
        return vals;
    }

    public static void setControls(double[] vals) {
        BasslineSynthesizer synth=Statics.synth1;
        if (Acid.drumsSelected==1){
            synth=Statics.synth2;
        }
        synth.tune = vals[0];
        synth.cutoff.setValue(vals[1]);
        synth.resonance.setValue(vals[2]);
        synth.envMod = vals[3];
        synth.decay = vals[4];
        synth.accent = vals[5];
        //Statics.output.getSequencer().setBpm(vals[6]);
        //Statics.output.volume=vals[7];
    }

    public static void setControls(double vals, int id) {
        BasslineSynthesizer synth=Statics.synth1;
        if (Acid.drumsSelected==1){
            synth=Statics.synth2;
        }
        if (id == 0) synth.tune = vals;
        if (id == 1) synth.cutoff.setValue(vals);
        if (id == 2) synth.resonance.setValue(vals);
        if (id == 3) synth.envMod = vals;
        if (id == 4) synth.decay = vals;
        if (id == 5) synth.accent = vals;
        //Statics.output.getSequencer().setBpm(vals[6]);
        //Statics.output.volume=vals[7];
    }


    static int idd = 8;
    static float max = Float.MIN_VALUE;
    static float min = Float.MAX_VALUE;

    public static float percent(int id, float val) {
        if (id == idd) {
            max = Math.max(max, val);
            min = Math.min(min, val);
           // System.out.println("knob:id=" + id + "\tval=" + val + "\tmin=" + min + "\tmax=" + max);
        }
        float dx = 0;
        float dy = 0;
        switch (id) {
            default:
                return 0;

            case 0:
                dx = 10f;
                dy = 4110f;
                break;
            case 1:
                dx = -118.89f;
                dy = 277.95f;
                break;
            case 2:
                dx = -198.42f;
                dy = 400f;
                break;
            case 3:
                dx = -182f;
                dy = 541f;
                break;
            case 4:
                dx = -94f;
                dy = 525f;
//                dx=-77.46f;
//               dy=2739.46f;
                break;
            case 5:
                dx = 0f;
                dy = 360f;
                break;
            case 6:
                dx = 0f;
                dy = 360f;
                break;
            case 7:
                dx = 0f;
                dy = 720f;
                break;
            case 8:
                dx = 0f;
                dy = 360f;
                break;
            case 9:
                dx = 0f;
                dy = 360f;
                break;
        }

        return (val - dx) / (dy - dx);
    }

    public static void touchReleased(int id) {
        touched[id] = false;
        KnobData.factory();
    }

    public static void touchDown(int id) {
        touched[id] = true;
        KnobData.factory();
    }
}
