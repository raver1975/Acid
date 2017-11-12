package com.acid;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveObject implements Serializable {
    private ArrayList<SequencerData> sequencerDataArrayList = new ArrayList<SequencerData>();
    private ArrayList<DrumData> drumDataArrayList = new ArrayList<DrumData>();
    private ArrayList<KnobData> knobsArrayList = new ArrayList<KnobData>();
    private int songPosition = 0;
    private int maxSongPosition = 0;
    private int minSongPosition = 0;

    SaveObject(Acid acid) {
        this.sequencerDataArrayList = acid.sequencerDataArrayList;
        this.drumDataArrayList = acid.drumDataArrayList;
        this.knobsArrayList = acid.knobsArrayList;
        this.songPosition = acid.songPosition;
        this.maxSongPosition = acid.maxSongPosition;
        this.minSongPosition = acid.minSongPosition;
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
        System.out.println("restored Song");
    }
}
