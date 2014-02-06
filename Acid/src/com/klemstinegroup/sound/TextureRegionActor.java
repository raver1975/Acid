package com.klemstinegroup.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class TextureRegionActor extends Actor {
        TextureRegion region;

        public TextureRegionActor () {
            Texture.setEnforcePotImages(false); 
        	region= new TextureRegion(new Texture(Gdx.files.internal("data/tr303.png")));
        }

        public void draw (SpriteBatch batch, float parentAlpha) {
                Color color = getColor();
                batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
                batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                        region.getRegionWidth(), region.getRegionHeight(), getScaleX(), getScaleY(), getRotation());
        }
        
}