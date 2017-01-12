package com.acid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import synth.Sequencer;

public class TextureRegionActor extends Actor {
    TextureRegion region;

    public TextureRegionActor(String filename) {
        //Texture.setEnforcePotImages(false);

//        	region= new TextureRegion(new Texture(Gdx.files.internal(filename)));
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        if (region!=null)batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        else
            region = new TextureRegion(new Texture(SequencerData.currentSequence.drawBitmap(500, 300)));
    }

}