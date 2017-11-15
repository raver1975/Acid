package com.acid;

import synth.Output;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveObject implements Serializable {

    private static final long serialVersionUID = -1216569043511623845L;
    private ArrayList<SequencerData> sequencerDataArrayList = new ArrayList<SequencerData>();
    private ArrayList<DrumData> drumDataArrayList = new ArrayList<DrumData>();
    private ArrayList<KnobData> knobsArrayList = new ArrayList<KnobData>();
    private int songPosition = 0;
    private int maxSongPosition = 0;
    private int minSongPosition = 0;
    private double bpm=120;
    private double vol=1f;
    private double delayTime=44100/10f;
    private double delayFeedback=.1f;

    SaveObject(Acid acid) {
        this.sequencerDataArrayList = acid.sequencerDataArrayList;
        this.drumDataArrayList = acid.drumDataArrayList;
        this.knobsArrayList = acid.knobsArrayList;
        this.songPosition = acid.songPosition;
        this.maxSongPosition = acid.maxSongPosition;
        this.minSongPosition = acid.minSongPosition;
        this.bpm=Statics.output.getSequencer().bpm;
        this.vol = (float) Statics.output.getVolume();
        this.delayTime=Output.getDelay().getTime();
        this.delayFeedback=Output.getDelay().getFeedback();
      }

    public void restore(Acid acid) {
        acid.swapPattern(acid.songPosition,acid.songPosition);
        acid.sequencerDataArrayList = sequencerDataArrayList;
        acid.drumDataArrayList = drumDataArrayList;
        acid.knobsArrayList = knobsArrayList;
        acid.swapPattern(songPosition,songPosition);

        acid.songPosition = songPosition;
        acid.maxSongPosition = maxSongPosition;
        acid.minSongPosition = minSongPosition;

        Statics.output.getSequencer().bpm=bpm;
        Statics.output.volume=vol;
        Output.getDelay().setTime(delayTime);
        Output.getDelay().setFeedback(delayFeedback);
    }
}
