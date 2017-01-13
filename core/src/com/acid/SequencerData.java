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
    Pixmap pixmap;
    public TextureRegion region;
    public static SequencerData currentSequence;


    public SequencerData() {

        for (int x1 = 0; x1 < 16; x1++) {
            note[x1] = Statics.output.getSequencer().bassline.note[x1];
            pause[x1] = Statics.output.getSequencer().bassline.pause[x1];
            slide[x1] = Statics.output.getSequencer().bassline.slide[x1];
            accent[x1] = Statics.output.getSequencer().bassline.accent[x1];
        }
        System.out.println("copying " + this);
        this.parent = currentSequence;
        if (this.parent != null) this.parent.child = this;
        currentSequence = this;
        pixmap= drawPixmap(100,100);
        region=new TextureRegion(new Texture(pixmap));
        region.flip(false,true);
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

    public Pixmap drawPixmap(int w, int h) {
        FrameBuffer drawBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
        drawBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
        ShapeRenderer renderer = new ShapeRenderer();
        renderer.getProjectionMatrix().setToOrtho2D(0,0,w,h);
        float skipx = ((float)w / 16f);
        float skipy = ((float)h /  31f);
        // grid
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(ColorHelper.rainbowDark());
        for (int r = 0; r < 16; r += 4) {
            renderer.line(r * skipx, 0, r * skipx, h);
        }
        for (int r = 0; r < (32); r++) {
            renderer.line(0, r * skipy, w, r * skipy);
        }
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(ColorHelper.rainbow());
        renderer.rect(0, 0, w, h);
        renderer.end();
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.YELLOW);

        for (int r = 0; r < 16; r++) {
            if (pause[r])
                continue;
            int skipd = 3;
            if (slide[r])
                skipd = 0;
            renderer.rect(r * skipx + skipd,
                    (note[r] + 16)
                            * skipy, skipx - skipd - skipd, skipy);
        }
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.RED);
        for (int r = 0; r < 16; r++) {
            if (pause[r])
                continue;
            int skipd = 3;
            if (slide[r])
                skipd = 0;
            if (accent[r])
                renderer
                        .rect(
                                r * skipx + skipd,
                                (note[r] + 16)
                                        * skipy, skipx - skipd - skipd,
                                skipy);
        }
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Line);

        for (int r = 0; r < 15; r++) {
            if (accent[r]) {
                renderer.setColor(Color.YELLOW);
            } else
                renderer.setColor(Color.RED);
            if (slide[r]
                    && !pause[r]) {
                renderer
                        .line((r) * skipx + skipx / 2,
                                (note[r] + 16)
                                        * skipy + skipy / 2,
                                (r + 1) * skipx + skipx / 2,
                                (note[r + 1] + 16)
                                        * skipy + skipy / 2);
            }
        }
        renderer.end();
        Pixmap pixmap1 = ScreenUtils.getFrameBufferPixmap(0,0,w,h);
        Pixmap pixmap = new Pixmap((int) w, (int) h, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        pixmap.drawPixmap(pixmap1, 0, 0);
        drawBuffer.end();
        drawBuffer.dispose();

        return pixmap;
    }

}