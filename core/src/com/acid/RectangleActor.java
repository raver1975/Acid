package com.acid;

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
        this.w=w;
        this.h=h;
        this.setWidth(w);
        this.setHeight(h);
        Pixmap pm=new Pixmap(w,h, Pixmap.Format.RGBA8888);
        pm.setColor(Color.CLEAR);
        pm.fill();
//        pm.setColor(Color.CYAN);
//        for (int i=0;i<10;i++){
//            pm.drawRectangle(i,i,w-i*2,h-i*2);
//        }
        region=new TextureRegion(new Texture(pm));
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
//        if (region!=null)System.out.println(region.getRegionWidth()+","+region.getRegionHeight()+"\t"+getWidth()+","+getHeight()+"\t"+getScaleX()+":"+getScaleY());
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (DrumData.currentSequence.region!=null)batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

}