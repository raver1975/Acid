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
public class KnobData extends InstrumentData {
    private double[][] knobs = new double[16][8];
    public final KnobData parent;
    public KnobData child;
    public static KnobData currentSequence;


    public KnobData() {

       for (int i=0;i<16;i++){
           for (int j=0;j<8;j++){
               knobs[i][j]=KnobImpl.knobs[i][j];
           }
       }
        System.out.println("copying knobs " + this);
        this.parent = currentSequence;
        if (this.parent != null) this.parent.child = this;
        currentSequence = this;
        pixmap = drawPixmap(300, 300);
        region = new TextureRegion(new Texture(pixmap));
        region.flip(false, true);
    }

    public void refresh() {
        for (int i=0;i<16;i++){
            for (int j=0;j<8;j++){
                knobs[i][j]=KnobImpl.knobs[i][j];
            }
        }
        System.out.println("restoring knobs " + this);
    }

    @Override
    public String toString() {
        String s = "";
        for (int i=0;i<16;i++){
            for (int j=0;j<8;j++){
                s+=knobs[i][j]+" ";
            }
        }
        return s;
    }

    public static void setcurrentSequence(KnobData sd){
        if (sd!= null) {
            currentSequence = sd;
            currentSequence.refresh();
        }
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
        Color c = ColorHelper.rainbowDark();
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
        ShapeRenderer renderer = new ShapeRenderer();
        renderer.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
        float skipx = ((float) w / 16f);
        float skipy = ((float) h / 8f);
        render(renderer, skipx, skipy);
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
        for (int i=0;i<16;i++){
            for (int j=0;j<8;j++){
                renderer1.setColor(ColorHelper.numberToColorPercentage(KnobImpl.percent(j,(float)KnobImpl.getRotation(j,knobs[i][j]))));
                renderer1.rect(skipx*i,skipy*j,skipx,skipy);
            }
        }
        renderer1.end();
    }
}
