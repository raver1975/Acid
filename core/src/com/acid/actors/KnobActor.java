package com.acid.actors;

import com.acid.ColorHelper;
import com.acid.KnobImpl;
import com.acid.Statics;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;


public class KnobActor extends Actor {

    private final BitmapFont font;
    private final int id;
    private final String name;

    public KnobActor(String name, final int id) {
        font = new BitmapFont(Gdx.app.getFiles().getFileHandle("data/font.fnt",
                Files.FileType.Internal), false);

        font.getData().setScale(.75f);
        this.id = id;
        this.name = name;
        this.setWidth(60);
        this.setHeight(60);
        this.addListener(new InputListener() {
            private float distx;
            private float disty;

            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                distx = x;
                disty = y;
                KnobImpl.touchDown(id);
//                ccpos = (int) ((int) ((KnobImpl.getRotation(id) * (127f / 360f) + 127 + 127 / 2) % 127) - 0);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y,
                                int pointer, int button) {
                KnobImpl.touchReleased(id);
            }

            public void touchDragged(InputEvent event, float x, float y,
                                     int pointer) {
                // (ShapeRenderingActor.this).rotate((distx - x));
                // ccpos = (int) ((int) ((KnobImpl.getRotation(id) * (127f / 360f) + 127 + 127 / 2) % 127) - 0);

                KnobImpl.touchDragged(id, (distx - x) + (disty - y));
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        int xc = 20;
        int yc = 20;
        float rotation = KnobImpl.getRotation(id);
        font.setColor(ColorHelper.rainbow());
        GlyphLayout gl1 = new GlyphLayout(font, name);
        font.draw(batch, name, this.getX() + xc - gl1.width / 2, this.getY() + this.getHeight() - gl1.height);
        batch.end();

        Statics.renderer.setProjectionMatrix(batch.getProjectionMatrix());
        Statics.renderer.setTransformMatrix(batch.getTransformMatrix());
        Statics.renderer.translate(getX(), getY(), 0);

        Statics.renderer.begin(ShapeType.Filled);
        Statics.renderer.setColor(ColorHelper.rainbow());
        for (float i = 0, ic = 0; i < Math.PI * 2; ic++, i += Math.PI / 8) {
            float x1, x2, y1, y2;
            x1 = MathUtils.cos(i) * 12f + xc;
            y1 = MathUtils.sin(i) * 12f + yc;
            x2 = MathUtils.cos(i) * 16f + xc;
            y2 = MathUtils.sin(i) * 16f + yc;
            Statics.renderer.line(x1, y1, x2, y2);
        }
        Statics.renderer.setColor(ColorHelper.numberToColorPercentage(KnobImpl.percent(id, KnobImpl.getRotation(id))));
        Statics.renderer.arc(xc, yc, 13, 180 - KnobImpl.percent(id, KnobImpl.getRotation(id)) * 360, KnobImpl.percent(id, KnobImpl.getRotation(id)) * 360);
        Statics.renderer.setColor(Color.DARK_GRAY);
        Statics.renderer.circle(xc, yc, 10, 20);
        Statics.renderer.setColor(ColorHelper.numberToColorPercentage(KnobImpl.percent(id, KnobImpl.getRotation(id))));
        for (float i = 2; i < 12; i += 2) {
            Statics.renderer.circle(xc + MathUtils.cosDeg(rotation) * -i,
                    yc + MathUtils.sinDeg(rotation) * i, 2, 5);
        }
        Statics.renderer.end();
        batch.begin();
    }

}