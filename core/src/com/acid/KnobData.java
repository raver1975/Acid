package com.acid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Created by Paul on 1/10/2017.
 */
public class KnobData extends InstrumentData {
    double[][] knobs = new double[16][8];
    public static KnobData currentSequence;


    public KnobData() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                knobs[i][j] = KnobImpl.knobs[i][j];
            }
        }

        currentSequence = this;
        pixmap = drawPixmap(50, 50);
        region = new TextureRegion(new Texture(pixmap));
        region.flip(false, true);
    }

    public static KnobData factory() {
        if (currentSequence != null) {
            boolean same = true;
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 8; j++) {
                    if (currentSequence.knobs[i][j] != KnobImpl.knobs[i][j]) same = false;
                }
            }
            if (same) return currentSequence;
        }
        return new KnobData();
    }

    public void refresh() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                KnobImpl.knobs[i][j] = knobs[i][j];
            }
        }
        if (Acid.drumsSelected == 0) {
            KnobImpl.setControls(KnobImpl.getControl(Statics.output.getSequencer1().step),true);
        } else {
            KnobImpl.setControls(KnobImpl.getControl(Statics.output.getSequencer2().step),false);
        }
        KnobData.currentSequence = this;
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                s += knobs[i][j] + " ";
            }
        }
        return s;
    }

    public static void setcurrentSequence(KnobData sd) {
        if (sd != null) {
            currentSequence = sd;
            currentSequence.refresh();
        }
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
        float skipy = ((float) h / 6f);
        render(renderer, skipx, skipy);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(ColorHelper.rainbowLight());
        renderer.rect(1, 1, w - 1, h - 1);
        renderer.end();
        Pixmap pixmap1 = ScreenUtils.getFrameBufferPixmap(0, 0, w, h);
        Pixmap pixmap = new Pixmap((int) w, (int) h, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        pixmap.drawPixmap(pixmap1, 0, 0);
        drawBuffer.end();
        drawBuffer.dispose();

        return pixmap;
    }

    public void render(ShapeRenderer renderer1, float skipx, float skipy) {
        renderer1.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 6; j++) {
                renderer1.setColor(ColorHelper.numberToColorPercentage(KnobImpl.percent(j, (float) KnobImpl.getRotation(j, knobs[i][j]))));
                renderer1.rect(skipx * i, skipy * j, skipx, skipy);
            }
        }
        renderer1.end();
    }

    static Array<KnobData> sequences = new Array<>();

    public static KnobData peekStack() {
        if (sequences.isEmpty()) return null;
        KnobData peek = sequences.get(0);
        return peek;
    }

    public static KnobData popStack() {
        if (sequences.isEmpty()) return null;
        return sequences.removeIndex(0);
    }

    public static void pushStack(KnobData sd) {
        sequences.insert(0, sd);
    }


}
