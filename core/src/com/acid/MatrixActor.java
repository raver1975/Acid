package com.acid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class MatrixActor extends Actor {

    private float y2;
    private float x2;


    public boolean notePause = false;
    public boolean noteAccent = false;
    public boolean noteSlide = false;


    public MatrixActor() {
//        this.id = id;
        this.setWidth(320);
        this.setHeight(280);
        this.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                int x1 = (int) (x / ((getWidth() / 16)));
                int y1 = (int) (y / (getHeight() / (Statics.drumsSelected ? 7 : 31))) - (Statics.drumsSelected ? 0 : 16);
//                x2 = x1;
//                y2 = y1;
                notePause = Statics.output.getSequencer().bassline.pause[x1];
                noteSlide = Statics.output.getSequencer().bassline.slide[x1];
                noteAccent = Statics.output.getSequencer().bassline.accent[x1];
                ttouch(x1, y1);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y,
                                int pointer, int button) {
                if (Statics.drumsSelected) {
                    new DrumData();
                } else {
                    new SequencerData();
                }
            }

            public void touchDragged(InputEvent event, float x, float y,
                                     int pointer) {
                int x1 = (int) (x / ((getWidth() / 16)));
                int y1 = (int) (y / (getHeight() / (Statics.drumsSelected ? 7 : 31))) - (Statics.drumsSelected ? 0 : 16);
                if (x1 != x2 || y1 != y2) {
                    ttouch(x1, y1);
//                    x2 = x1;
//                    y2 = y1;
                }
            }
        });
    }

    protected void ttouch(int x1, int y1) {
        if (!Statics.drumsSelected) {
            if (x1 < 16 && x1 > -1) {
                Statics.output.getSequencer().bassline.note[x1] = (byte) y1;
                if (x1 == x2 && y1 == y2) {
                    if (notePause) {
//                        Statics.output.getSequencer().bassline.pause[x1] = false;
                        notePause = false;
                    } else if (!noteSlide) {
//                        Statics.output.getSequencer().bassline.slide[x1] = true;
                        noteSlide = true;
                    } else {
                        notePause = true;
                        noteAccent = !noteAccent;
                        noteSlide = false;
//                        Statics.output.getSequencer().bassline.pause[x1] = true;
//                        Statics.output.getSequencer().bassline.accent[x1] = !Statics.output
//                                .getSequencer().bassline.accent[x1];
//                        Statics.output.getSequencer().bassline.slide[x1] = false;
                    }
                }
                if (x1!=x2){
                    notePause=Statics.output.getSequencer().bassline.pause[x1];
                    noteSlide=Statics.output.getSequencer().bassline.slide[x1];
                    noteAccent=Statics.output.getSequencer().bassline.accent[x1];
                }
                Statics.output.getSequencer().bassline.pause[x1] = notePause;
                Statics.output.getSequencer().bassline.slide[x1] = noteSlide;
                Statics.output.getSequencer().bassline.accent[x1] = noteAccent;
            }
        } else {
            if (x1 < 16 && x1 > -1 && y1 >= 0 && y1 < 7) {
                if (Statics.output.getSequencer().rhythm[y1][x1] > 0) {
                    Statics.output.getSequencer().rhythm[y1][x1] = 0;
                } else
                    Statics.output.getSequencer().rhythm[y1][x1] = 127;
            }

        }
        x2 = x1;
        y2 = y1;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (Statics.mutate && Math.random() < .01) {
            ttouch((int) (MathUtils.random() * 16),
                    (int) (MathUtils.random() * 31) - 16);
            if (Statics.drumsSelected)new DrumData();
            else new SequencerData();
        }
        batch.end();
        Statics.renderer.setProjectionMatrix(batch.getProjectionMatrix());
        Statics.renderer.setTransformMatrix(batch.getTransformMatrix());
        Statics.renderer.translate(getX(), getY(), 0);

        int skipx = (int) (getWidth() / 16);
        int skipy = (int) (getHeight() / (Statics.drumsSelected ? 7 : 31));
        // grid
        Statics.renderer.begin(ShapeType.Line);
        Statics.renderer.setColor(ColorHelper.rainbowDark());
        for (int r = 0; r < 16; r += 4) {
            Statics.renderer.line(r * skipx, 0, r * skipx, getHeight());
        }
        for (int r = 0; r < (Statics.drumsSelected ? 8 : 32); r++) {
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
        if (!Statics.drumsSelected) {
            /*Statics.renderer.begin(ShapeType.Filled);
            Statics.renderer.setColor(Color.YELLOW);

            for (int r = 0; r < 16; r++) {
                if (Statics.output.getSequencer().bassline.pause[r])
                    continue;
                int skipd = 3;
                if (Statics.output.getSequencer().bassline.slide[r])
                    skipd = 0;
                // if (r > 0 && Acid.output.getSequencer().bassline.slide[r -
                // 1])
                // skipd = 0;
                Statics.renderer.rect(r * skipx + skipd,
                        (Statics.output.getSequencer().bassline.note[r] + 16)
                                * skipy, skipx - skipd - skipd, skipy);
            }
            Statics.renderer.end();

            Statics.renderer.begin(ShapeType.Filled);
            Statics.renderer.setColor(Color.RED);
            for (int r = 0; r < 16; r++) {
                if (Statics.output.getSequencer().bassline.pause[r])
                    continue;
                int skipd = 3;
                if (Statics.output.getSequencer().bassline.slide[r])
                    skipd = 0;
                // if (r > 0 && Acid.output.getSequencer().bassline.slide[r -
                // 1])
                // skipd = 0;

                if (Statics.output.getSequencer().bassline.accent[r])
                    Statics.renderer
                            .rect(
                                    r * skipx + skipd,
                                    (Statics.output.getSequencer().bassline.note[r] + 16)
                                            * skipy, skipx - skipd - skipd,
                                    skipy);
            }
            Statics.renderer.end();

            Statics.renderer.begin(ShapeType.Line);

            for (int r = 0; r < 15; r++) {
                if (!Statics.output.getSequencer().bassline.accent[r]) {

                    Statics.renderer.setColor(Color.YELLOW);
                } else
                    Statics.renderer.setColor(Color.RED);
                if (Statics.output.getSequencer().bassline.slide[r]
                        && !Statics.output.getSequencer().bassline.pause[r]) {
                    Statics.renderer
                            .line((r) * skipx + skipx / 2,
                                    (Statics.output.getSequencer().bassline.note[r] + 16)
                                            * skipy + skipy / 2,
                                    (r + 1) * skipx + skipx / 2,
                                    (Statics.output.getSequencer().bassline.note[r + 1] + 16)
                                            * skipy + skipy / 2);
                }
            }
            Statics.renderer.end();*/

            SequencerData.render(Statics.renderer,skipx,skipy);


        } else {
            DrumData.render(Statics.renderer,skipx,skipy);
        }
        batch.begin();
        //batch.draw(new TextureRegion(new Texture(SequencerData.currentSequence.drawPixmap(50, 50))), 0,0);
    }

}