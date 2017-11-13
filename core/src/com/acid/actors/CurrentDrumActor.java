package com.acid.actors;

import com.acid.DrumData;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CurrentDrumActor extends Actor {
    private final int h;
    private final int w;
    TextureRegion region;

    public CurrentDrumActor(int w, int h) {
        this.w = w;
        this.h = h;
        this.setWidth(w);
        this.setHeight(h);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (DrumData.peekStack()!=null && DrumData.peekStack().region != null)
            batch.draw(DrumData.peekStack().region, getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

}