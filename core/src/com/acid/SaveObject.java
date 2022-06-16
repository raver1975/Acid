package com.acid;

import java.io.Serializable;
import java.util.Collections;

import com.badlogic.gdx.utils.Array;
import synth.Output;

public class SaveObject implements Serializable {

    private static final long serialVersionUID = -1216569043511623845L;
    private Array<SequencerData> sequencerDataArrayList = new Array<SequencerData>();
    private Array<DrumData> drumDataArrayList = new Array<DrumData>();
    private Array<KnobData> knobsArrayList1 = new Array<KnobData>();
    private Array<KnobData> knobsArrayList2 = new Array<KnobData>();
    private int songPosition = 0;
    private int maxSongPosition = 0;
    private int minSongPosition = 0;
    private double bpm = 120;
    private double vol = 1f;
    private double delayTime = 44100 / 10f;
    private double delayFeedback = .1f;
    private Array<SequencerData> sequencerStack = new Array<>();
    private Array<KnobData> knobStack = new Array<>();
    private Array<DrumData> drumStack = new Array<>();

    SaveObject(){}

    SaveObject(Acid acid) {
        this.sequencerDataArrayList = acid.sequencerDataArrayList;
        this.drumDataArrayList = acid.drumDataArrayList;
        this.knobsArrayList1 = acid.knobsArrayList1;
        this.knobsArrayList2 = acid.knobsArrayList2;
//        this.knobsArrayList2 = acid.knobsArrayList2;
        this.sequencerStack = new Array<>(SequencerData.sequences);
        this.drumStack = new Array<>(DrumData.sequences);
        this.knobStack = new Array<>(KnobData.sequences);
        this.songPosition = acid.songPosition;
        this.maxSongPosition = acid.maxSongPosition;
        this.minSongPosition = acid.minSongPosition;
        this.bpm = Statics.output.getSequencer1().bpm;
        this.vol = (float) Output.getVolume();
        this.delayTime = Output.getDelay().getTime();
        this.delayFeedback = Output.getDelay().getFeedback();
    }

    public void restore(Acid acid) {
        SequencerData.sequences = new Array<>();
        if (sequencerStack != null) {
            for (InstrumentData data : sequencerStack) {
                data.refresh();
                SequencerData.sequences.add(new SequencerData());
            }
        }
        DrumData.sequences = new Array<>();
        if (drumStack != null) {
            for (InstrumentData data : drumStack) {
                data.refresh();
                DrumData.sequences.add(new DrumData());
            }
        }
        KnobData.sequences = new Array<>();
        if (knobStack != null) {
            for (InstrumentData data : knobStack) {
                data.refresh();
                KnobData.sequences.add(new KnobData());
            }
        }
        acid.swapPattern(acid.songPosition, acid.songPosition);
        if (sequencerDataArrayList==null){
            sequencerDataArrayList=new Array<>();
        }
        if (knobsArrayList1==null){
            knobsArrayList1=new Array<>();
        }
        if (knobsArrayList2==null){
            knobsArrayList2=new Array<>();
        }
        acid.sequencerDataArrayList = sequencerDataArrayList;
        acid.drumDataArrayList = drumDataArrayList;
        acid.knobsArrayList1 = knobsArrayList1;
        acid.knobsArrayList2 = knobsArrayList2;
        acid.songPosition = songPosition;
        acid.maxSongPosition = maxSongPosition;
        acid.minSongPosition = minSongPosition;
        acid.swapPattern(songPosition, songPosition);
        Statics.output.getSequencer1().setBpm(bpm);
        Statics.output.getSequencer2().setBpm(bpm);
        Output.volume = vol;
        Output.getDelay().setTime(delayTime);
        Output.getDelay().setFeedback(delayFeedback);
        if (Acid.drumsSelected==0) {
            if (acid.knobsArrayList1.size > 0) {
                acid.knobsArrayList1.get(0).refresh();
            }
        }
        else {
            if (acid.knobsArrayList2.size > 0) {
                acid.knobsArrayList2.get(0).refresh();
            }
        }
        KnobData.currentSequence = new KnobData();
    }
}
