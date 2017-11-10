package com.acid.actors;

import com.acid.KnobData;
import com.acid.SequencerData;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CurrentKnobsActor extends Actor {
    private final int h;
    private final int w;
    TextureRegion region;

    public CurrentKnobsActor(int w, int h) {
        this.w = w;
        this.h = h;
        this.setWidth(w);
        this.setHeight(h);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
//        if (region!=null)System.out.println(region.getRegionWidth()+","+region.getRegionHeight()+"\t"+getWidth()+","+getHeight()+"\t"+getScaleX()+":"+getScaleY());
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (KnobData.currentSequence!=null&& KnobData.currentSequence.region != null) {// && KnobData.currentSequence.parent != null && KnobData.currentSequence.parent.region != null)
            batch.draw(KnobData.currentSequence.region, getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }
}