package com.acid.actors;

import com.acid.KnobData;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BelowKnobsActor extends Actor {
    private final int h;
    private final int w;
    TextureRegion region;

    public BelowKnobsActor(int w, int h) {
        this.w = w;
        this.h = h;
        this.setWidth(w);
        this.setHeight(h);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (KnobData.currentSequence!=null&& KnobData.currentSequence.region != null) {// && KnobData.currentSequence.parent != null && KnobData.currentSequence.parent.region != null)
            batch.draw(KnobData.currentSequence.region, getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }
}