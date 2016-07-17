package com.acid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TextureRegionActor extends Actor {
        TextureRegion region;

        public TextureRegionActor () {
            //Texture.setEnforcePotImages(false);
        	region= new TextureRegion(new Texture(Gdx.files.internal("data/tr303.png")));
        }


        @Override
        public void draw (Batch batch, float parentAlpha) {
                Color color = getColor();
                batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
                batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                        region.getRegionWidth(), region.getRegionHeight(), getScaleX(), getScaleY(), getRotation());
        }
        
}