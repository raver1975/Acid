package com.acid.actors;

import com.acid.ColorHelper;
import com.acid.DrumData;
import com.acid.SequencerData;
import com.acid.Statics;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class SequenceActor extends Actor {

    private float y2;
    private float x2;


    public boolean notePause = false;
    public boolean noteAccent = false;
    public boolean noteSlide = false;


    public SequenceActor() {
        this.setWidth(320);
        this.setHeight(280);
        this.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                int x1 = (int) (x / ((getWidth() / 16)));
                int y1 = (int) (y / (getHeight() / 31)) - 16;
                notePause = Statics.output.getSequencer().bassline.pause[x1];
                noteSlide = Statics.output.getSequencer().bassline.slide[x1];
                noteAccent = Statics.output.getSequencer().bassline.accent[x1];
                ttouch(x1, y1);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y,
                                int pointer, int button) {
                //new SequencerData();
            }

            public void touchDragged(InputEvent event, float x, float y,
                                     int pointer) {
                int x1 = (int) (x / ((getWidth() / 16)));
                int y1 = (int) (y / (getHeight() / 31)) - 16;
                if (x1 != x2 || y1 != y2) {
                    ttouch(x1, y1);
                }
            }
        });
    }

    public void ttouch(int x1, int y1) {
        if (x1 < 16 && x1 > -1) {
            Statics.output.getSequencer().bassline.note[x1] = (byte) y1;
            boolean special = false;
            if (x1 == x2 && y1 == y2) {
                if (notePause) {
                    notePause = false;
                } else if (!noteSlide) {
                    noteSlide = true;
                } else {
                    notePause = true;
                    noteAccent = !noteAccent;
                    noteSlide = false;
                }
            } else {
                special = true;
            }


            if (x1 != x2) {
                notePause = Statics.output.getSequencer().bassline.pause[x1];
                noteSlide = Statics.output.getSequencer().bassline.slide[x1];
                noteAccent = Statics.output.getSequencer().bassline.accent[x1];
            }
            if (special && notePause) notePause = false;
            Statics.output.getSequencer().bassline.pause[x1] = notePause;
            Statics.output.getSequencer().bassline.slide[x1] = noteSlide;
            Statics.output.getSequencer().bassline.accent[x1] = noteAccent;
        }

        x2 = x1;
        y2 = y1;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.end();
        Statics.renderer.setProjectionMatrix(batch.getProjectionMatrix());
        Statics.renderer.setTransformMatrix(batch.getTransformMatrix());
        Statics.renderer.translate(getX(), getY(), 0);
        Statics.renderer.scale(this.getScaleX(), this.getScaleY(), 1f);

        int skipx = (int) (getWidth() / 16);
        int skipy = (int) (getHeight() / 31);
        // grid
        Statics.renderer.begin(ShapeType.Line);
        Statics.renderer.setColor(ColorHelper.rainbowDark());
        for (int r = 0; r < 16; r += 4) {
            Statics.renderer.line(r * skipx, 0, r * skipx, getHeight());
        }
        for (int r = 0; r < 32; r++) {
            Statics.renderer.line(0, r * skipy, getWidth(), r * skipy);
        }
        Statics.renderer.end();

        Statics.renderer.begin(ShapeType.Line);
        Statics.renderer.setColor(ColorHelper.rainbow());
        Statics.renderer.line(
                (Statics.output.getSequencer().step) % 16 * skipx, 0,
                (Statics.output.getSequencer().step) % 16 * skipx, getHeight());
        Statics.renderer.end();

        Statics.renderer.begin(ShapeType.Line);
        Statics.renderer.setColor(ColorHelper.rainbow());
        Statics.renderer.rect(0, 0, this.getWidth(), this.getHeight());
        Statics.renderer.end();
        SequencerData.render(Statics.renderer, skipx, skipy);
        batch.begin();
    }

}