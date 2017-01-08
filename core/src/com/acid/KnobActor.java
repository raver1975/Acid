package com.acid;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import synth.BasslineSynthesizer;

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
                return true;
            }

            public void touchUp(InputEvent event, float x, float y,
                                int pointer, int button) {
            }

            public void touchDragged(InputEvent event, float x, float y,
                                     int pointer) {
                // (ShapeRenderingActor.this).rotate((distx - x));
                KnobImpl.touchDragged(id, getRotation(), (distx - x) + (disty - y));
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float rotation = KnobImpl.getRotation(id);
        font.setColor(Color.WHITE);
        GlyphLayout gl1 = new GlyphLayout(font, name);
        GlyphLayout gl2 = new GlyphLayout(font, (int)rotation+"");
        font.draw(batch, name, this.getX() + 20 - gl1.width / 2, this.getY() + this.getHeight() - gl1.height);
        font.draw(batch, (int)rotation + "", this.getX() + 20 - gl2.width / 2, this.getY() + 23 - gl2.height);
        batch.end();

        Statics.renderer.setProjectionMatrix(batch.getProjectionMatrix());
        Statics.renderer.setTransformMatrix(batch.getTransformMatrix());
        Statics.renderer.translate(getX(), getY(), 0);

        Statics.renderer.begin(ShapeType.Filled);
        Statics.renderer.setColor(Color.WHITE);
        Statics.renderer.circle(20, 25, 10, 20);
        Statics.renderer.end();

        Statics.renderer.begin(ShapeType.Line);
        Statics.renderer.setColor(Color.BLACK);
        Statics.renderer.circle(20, 25, 10, 20);
        Statics.renderer.end();

        Statics.renderer.begin(ShapeType.Filled);
        Statics.renderer.setColor(Color.RED);
        for (float i = 2; i < 12; i += 2) {
            Statics.renderer.circle(20 + MathUtils.cosDeg(rotation) * -i,
                    25 + MathUtils.sinDeg(rotation) * i, 2, 5);
        }
        Statics.renderer.end();

        batch.begin();

    }

}