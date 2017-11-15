package com.acid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import synth.Sequencer;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Paul on 1/10/2017.
 */
public class SequencerData extends InstrumentData {
    private final byte[] note = new byte[16];
    private final boolean[] pause = new boolean[16];
    private final boolean[] slide = new boolean[16];
    private final boolean[] accent = new boolean[16];
//    public SequencerData parent;
//    public SequencerData child;
//    public static SequencerData currentSequence;


    public SequencerData() {

        for (int x1 = 0; x1 < 16; x1++) {
            note[x1] = Statics.output.getSequencer().bassline.note[x1];
            pause[x1] = Statics.output.getSequencer().bassline.pause[x1];
            slide[x1] = Statics.output.getSequencer().bassline.slide[x1];
            accent[x1] = Statics.output.getSequencer().bassline.accent[x1];
        }

//        this.parent = currentSequence;
//        if (this.parent != null) this.parent.child = this;
//        currentSequence = this;
        pixmap = drawPixmap(300, 300);
        region = new TextureRegion(new Texture(pixmap));
        region.flip(false, true);
    }

//    public static SequencerData factory(){
//        if (currentSequence != null) {
//            boolean same = true;
//            for (int x1 = 0; x1 < 16; x1++) {
//                if (currentSequence.note[x1] != Statics.output.getSequencer().bassline.note[x1]) same = false;
//                if (currentSequence.pause[x1] != Statics.output.getSequencer().bassline.pause[x1]) same = false;
//                if (currentSequence.slide[x1] != Statics.output.getSequencer().bassline.slide[x1]) same = false;
//                if (currentSequence.accent[x1] != Statics.output.getSequencer().bassline.accent[x1]) same = false;
//            }
//            if (same)return currentSequence;
//        }
//        return new SequencerData();
//    }

    public void refresh() {
        for (int x1 = 0; x1 < 16; x1++) {
            Statics.output.getSequencer().bassline.note[x1] = note[x1];
            Statics.output.getSequencer().bassline.pause[x1] = pause[x1];
            Statics.output.getSequencer().bassline.slide[x1] = slide[x1];
            Statics.output.getSequencer().bassline.accent[x1] = accent[x1];
        }
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < 16; i++) {
            s += note[i] + (pause[i] ? "p" : "") + (slide[i] ? "s" : "") + (accent[i] ? "a" : "") + " ";
        }
        return s;
    }

    public Pixmap drawPixmap(int w, int h) {
        FrameBuffer drawBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
        drawBuffer.begin();
        Color c = ColorHelper.rainbowDark();
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
        ShapeRenderer renderer = new ShapeRenderer();
        renderer.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
        float skipx = ((float) w / 16f);
        float skipy = ((float) h / 31f);
        render(renderer, skipx, skipy);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(ColorHelper.rainbowInverse());
        for (int i=0;i<5;i++) {
            renderer.rect(i, i, w-i*2, h-i*2);
        }
        renderer.end();
        Pixmap pixmap1 = ScreenUtils.getFrameBufferPixmap(0, 0, w, h);
        Pixmap pixmap = new Pixmap((int) w, (int) h, Pixmap.Format.RGBA8888);
        pixmap.setColor(ColorHelper.rainbowInverse());
        pixmap.fill();
        pixmap.drawPixmap(pixmap1, 0, 0);
        drawBuffer.end();
        drawBuffer.dispose();

        return pixmap;
    }

    public static void render(ShapeRenderer renderer1, float skipx, float skipy) {
        renderer1.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < 16; i++) {
            if (Statics.output.getSequencer().bassline.pause[i]) {
                continue;
            }
            if (Statics.output.getSequencer().bassline.accent[i]) {
                renderer1.setColor(ColorHelper.rainbowInverse());
            } else {

                renderer1.setColor(ColorHelper.rainbowLight());
            }
            if (Statics.output.getSequencer().bassline.slide[i]) {
                if (i < 15) {
                    renderer1
                            .line((i) * skipx + skipx / 2,
                                    (Statics.output.getSequencer().bassline.note[i] + 16)
                                            * skipy + skipy / 2,
                                    (i + 1) * skipx + skipx / 2,
                                    (Statics.output.getSequencer().bassline.note[(i + 1) % 16] + 16)
                                            * skipy + skipy / 2);
                } else {
                    renderer1
                            .line((i) * skipx + skipx / 2,
                                    (Statics.output.getSequencer().bassline.note[i] + 16)
                                            * skipy + skipy / 2,
                                    (i + 1) * skipx,
                                    (Statics.output.getSequencer().bassline.note[(i + 1) % 16] + 16)
                                            * skipy + skipy / 2);
                    renderer1
                            .line(skipx / 2,
                                    (Statics.output.getSequencer().bassline.note[0] + 16)
                                            * skipy + skipy / 2,
                                    0,
                                    (Statics.output.getSequencer().bassline.note[15] + 16)
                                            * skipy + skipy / 2);
                }
            }
        }
        renderer1.end();

        renderer1.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 16; i++) {
            if (Statics.output.getSequencer().bassline.pause[i]) {
                continue;
            }
            if (Statics.output.getSequencer().bassline.accent[i]) {
                renderer1.setColor(ColorHelper.rainbowInverse());
            } else {
                renderer1.setColor(ColorHelper.rainbowLight());
            }

            if (Statics.output.getSequencer().bassline.accent[i]) {
//                    if (i==0||!Statics.output.getSequencer().bassline.slide[i-1])
                float cx = Math.min(skipx, skipy);
                renderer1
                        .rect(
                                i * skipx + ((skipx - cx) / 2),
                                (Statics.output.getSequencer().bassline.note[i] + 16)
                                        * skipy, cx,
                                cx);
//                        renderer1.circle(i * skipx + skipx / 2, (Statics.output.getSequencer().bassline.note[i] + 16) * skipy + skipy / 2, Math.min(skipx, skipy) / 2);

            } else {
                renderer1.circle(i * skipx + skipx / 2, (Statics.output.getSequencer().bassline.note[i] + 16) * skipy + skipy / 2, Math.min(skipx, skipy) / 2);
            }
        }
        renderer1.end();
    }

    static Stack<SequencerData> sequences = new Stack<SequencerData>();

    public static SequencerData peekStack() {
        if (sequences.empty()) return null;
        SequencerData peek = sequences.peek();
        return peek;
    }

    public static SequencerData popStack() {
        if (sequences.empty()) return null;
        return sequences.pop();
    }

    public static void pushStack(SequencerData sd) {
        sequences.push(sd);
    }

}
