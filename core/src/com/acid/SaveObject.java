package com.acid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import synth.Output;

public class SaveObject implements Serializable {

    private static final long serialVersionUID = -1216569043511623845L;
    private ArrayList<SequencerData> sequencerDataArrayList = new ArrayList<SequencerData>();
    private ArrayList<DrumData> drumDataArrayList = new ArrayList<DrumData>();
    private ArrayList<KnobData> knobsArrayList = new ArrayList<KnobData>();
    private int songPosition = 0;
    private int maxSongPosition = 0;
    private int minSongPosition = 0;
    private double bpm = 120;
    private double vol = 1f;
    private double delayTime = 44100 / 10f;
    private double delayFeedback = .1f;
    private ArrayList<SequencerData> sequencerStack = new ArrayList<>();
    private ArrayList<KnobData> knobStack = new ArrayList<>();
    private ArrayList<DrumData> drumStack = new ArrayList<>();

    SaveObject(Acid acid) {
        this.sequencerDataArrayList = acid.sequencerDataArrayList;
        this.drumDataArrayList = acid.drumDataArrayList;
        this.knobsArrayList = acid.knobsArrayList;
        this.sequencerStack = new ArrayList<>(Collections.list(SequencerData.sequences.elements()));
        this.drumStack = new ArrayList<>(Collections.list(DrumData.sequences.elements()));
        this.knobStack = new ArrayList<>(Collections.list(KnobData.sequences.elements()));
        this.songPosition = acid.songPosition;
        this.maxSongPosition = acid.maxSongPosition;
        this.minSongPosition = acid.minSongPosition;
        this.bpm = Statics.output.getSequencer().bpm;
        this.vol = (float) Output.getVolume();
        this.delayTime = Output.getDelay().getTime();
        this.delayFeedback = Output.getDelay().getFeedback();
    }

    public void restore(Acid acid) {
        SequencerData.sequences = new Stack<>();
        for (InstrumentData data : sequencerStack) {
            data.refresh();
            SequencerData.sequences.add(new SequencerData());
        }
        DrumData.sequences = new Stack<>();
        for (InstrumentData data : drumStack) {
            data.refresh();
            DrumData.sequences.add(new DrumData());
        }
        KnobData.sequences = new Stack<>();
        for (InstrumentData data : knobStack) {
            data.refresh();
            KnobData.sequences.add(new KnobData());
        }
        acid.swapPattern(acid.songPosition, acid.songPosition);
        acid.sequencerDataArrayList = sequencerDataArrayList;
        acid.drumDataArrayList = drumDataArrayList;
        acid.knobsArrayList = knobsArrayList;
        acid.songPosition = songPosition;
        acid.maxSongPosition = maxSongPosition;
        acid.minSongPosition = minSongPosition;
        acid.swapPattern(songPosition, songPosition);
        Statics.output.getSequencer().setBpm(bpm);
        Output.volume = vol;
        Output.getDelay().setTime(delayTime);
        Output.getDelay().setFeedback(delayFeedback);
        if (acid.knobsArrayList.size()>0){
            acid.knobsArrayList.get(0).refresh();
        }
        KnobData.currentSequence = new KnobData();
    }
}
