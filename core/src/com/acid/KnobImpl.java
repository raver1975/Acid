package com.acid;

import com.badlogic.gdx.Gdx;
import synth.BasslineSynthesizer;

/**
 * Created by Paul on 1/8/2017.
 */
public class KnobImpl {
    public static float getRotation(int id){
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
                rotation=(float)Statics.output.getVolume()*360f;
                break;
        }
        return rotation;
    }

    public static void touchDragged(int id,float rotation,float offset) {
        int cc = (int) ((int) ((rotation * (127f / 360f) + 127 + 127 / 2) % 127) - offset);

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
                if (cc>-100) {
                    Statics.output.getSequencer().setBpm(cc + 100);
                }
                break;
            case 7:
                Statics.synth.controlChange(39,cc);
        }
    }
}
