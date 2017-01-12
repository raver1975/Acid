package com.acid;

/**
 * Created by Paul on 1/10/2017.
 */
public class DrumData {
    private final int[][] rhythm = new int[7][16];
    public final DrumData parent;
    public DrumData child;

    public static DrumData currentSequence;

    public DrumData() {
        for (int y1 = 0; y1 < 7; y1++) {
            for (int x1 = 0; x1 < 16; x1++) {
                rhythm[y1][x1] = Statics.output.getSequencer().rhythm[y1][x1];
            }
        }
        System.out.println("copying " + this);
        this.parent = currentSequence;
        if (this.parent != null) this.parent.child = this;
        currentSequence = this;
    }

    public void refresh() {
        for (int y1 = 0; y1 < 7; y1++) {
            for (int x1 = 0; x1 < 16; x1++) {
                Statics.output.getSequencer().rhythm[y1][x1] = rhythm[y1][x1];
            }
        }
        System.out.println("restoring " + this);
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < 16; i++) {
            for (int j= 0; j < 7; j++) {
                if (Statics.output.getSequencer().rhythm[j][i]==0){

                }
                else{
                    s+=j+"";
                }
            }
            s+=" ";
        }
        return s;
    }

    public static void undo() {
        if (currentSequence != null && currentSequence.parent != null) {
            currentSequence = currentSequence.parent;
            currentSequence.refresh();
        }
    }

    public static void redo() {
        if (currentSequence != null && currentSequence.child != null) {
            currentSequence = currentSequence.child;
            currentSequence.refresh();
        }
    }
}
