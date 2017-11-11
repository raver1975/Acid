package com.acid;

import com.badlogic.gdx.Gdx;
import synth.BasslineSynthesizer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Paul on 1/8/2017.
 */
public class KnobImpl {
    static double[][] knobs = new double[16][8];

    static {
        for (int i = 0; i < 16; i++) {
            knobs[i] = getControls();
        }
    }

    static boolean[] touched = new boolean[8];

    static boolean isTouched() {
        for (int i = 0; i < 8; i++) {
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
                rotation = (float) ((val * Gdx.graphics.getWidth()) / 20.0f) - 100f;
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
                        .getTrack(0)).decay)) * Gdx.graphics.getWidth()) / 20.0f) - 100f;
                break;

            case 5:
                rotation = (float) ((BasslineSynthesizer) Statics.output.getTrack(0)).accent * 360f;
                break;
            case 6:
                //accent
                rotation = (float) Statics.output.getSequencer().bpm;
                break;
            case 7:
                rotation = (float) Statics.output.getVolume() * 360f;
                break;
        }
        return rotation;
    }

    //    public static int[] knobVals=new int[8];
    public static void touchDragged(int id, float offset) {

        int cc = (int) (127f / 2f - offset);

        switch (id) {
            case 0:
                // tune
                Statics.synth.controlChange(33, cc);
                break;
            case 1:
                //cutoff
                Statics.synth.controlChange(34, cc);
                break;

            case 2:
                //resonance
                Statics.synth.controlChange(35, cc);
                break;

            case 3:
                //envelope
                Statics.synth.controlChange(36, cc);
                break;

            case 4:
                //decay
                Statics.synth.controlChange(37, cc);
                break;

            case 5:
                //accent
                Statics.synth.controlChange(38, cc);
                break;
            case 6:
                //bpm
                if (cc >= -100 & cc <= 260) {
                    Statics.output.getSequencer().setBpm(cc + 100);
//                    knobVals[id]=cc+100;
                }
                break;
            case 7:
                //volume
                Statics.synth.controlChange(39, cc);
        }
//        System.out.println(Arrays.toString(knobVals));
//        Statics.synth.
        System.out.println(Arrays.toString(getControls()));
    }

    public static void setControl(int step, int id) {
        if (Statics.free){
            for (int i=0;i<16;i++){
                knobs[i % 16][id] = getControls()[id];
            }
        }
        else {
            knobs[step % 16][id] = getControls()[id];
        }
    }

    public static double[] getControl(int step) {
        return knobs[step % 16];
    }

    public static double[] getControls() {
        double[] vals = new double[8];
        vals[0] = Statics.synth.tune;
        vals[1] = Statics.synth.cutoff.getValue();
        vals[2] = Statics.synth.resonance.getValue();
        vals[3] = Statics.synth.envMod;
        vals[4] = Statics.synth.decay;
        vals[5] = Statics.synth.accent;
        vals[6] = Statics.output.getSequencer().bpm;
        vals[7] = Statics.output.volume;
        return vals;
    }

    public static void setControls(double[] vals) {
        Statics.synth.tune = vals[0];
        Statics.synth.cutoff.setValue(vals[1]);
        Statics.synth.resonance.setValue(vals[2]);
        Statics.synth.envMod = vals[3];
        Statics.synth.decay = vals[4];
        Statics.synth.accent = vals[5];
        //Statics.output.getSequencer().setBpm(vals[6]);
        //Statics.output.volume=vals[7];
    }

    public static void setControls(double vals, int id) {
        if (id == 0) Statics.synth.tune = vals;
        if (id == 1) Statics.synth.cutoff.setValue(vals);
        if (id == 2) Statics.synth.resonance.setValue(vals);
        if (id == 3) Statics.synth.envMod = vals;
        if (id == 4) Statics.synth.decay = vals;
        if (id == 5) Statics.synth.accent = vals;
        //Statics.output.getSequencer().setBpm(vals[6]);
        //Statics.output.volume=vals[7];
    }


    static int idd = -4;
    static float max = Float.MIN_VALUE;
    static float min = Float.MAX_VALUE;

    public static float percent(int id, float val) {
        if (id == idd) {
            max = Math.max(max, val);
            min = Math.min(min, val);
            System.out.println("knob:" + id + "\t" + val + "\t" + min + "\t" + max);
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
        }
        return (val - dx) / (dy - dx);
    }

    public static void touchReleased(int id) {
        touched[id] = false;
        new KnobData();
    }

    public static void touchDown(int id) {
        touched[id] = true;
    }
}
