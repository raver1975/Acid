package com.acid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CurrentSequencerActor extends Actor {
    private final int h;
    private final int w;
    TextureRegion region;

    public CurrentSequencerActor(int w, int h) {
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
        if (SequencerData.currentSequence.region != null && SequencerData.currentSequence.parent != null && SequencerData.currentSequence.parent.region != null)
            batch.draw(SequencerData.currentSequence.parent.region, getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}