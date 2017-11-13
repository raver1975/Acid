package com.acid.actors;

import com.acid.DrumData;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class RectangleActor extends Actor {
    private final int h;
    private final int w;
    TextureRegion region;

    public RectangleActor(int w, int h) {
        this(w, h, false);
    }


    public RectangleActor(int w, int h, boolean debug) {
        this.w = w;
        this.h = h;
        this.setWidth(w);
        this.setHeight(h);
        Pixmap pm = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pm.setColor(Color.CLEAR);
        pm.fill();
        if (debug) {
            pm.setColor(Color.CYAN);
            for (int i = 0; i < 10; i++) {
                pm.drawRectangle(i, i, w - i * 2, h - i * 2);
            }
        }
        region = new TextureRegion(new Texture(pm));
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (DrumData.peekStack() != null && DrumData.peekStack().region != null)
            batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

}