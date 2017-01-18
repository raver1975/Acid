package com.acid;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import synth.BasslineSynthesizer;
import synth.Output;
import synth.RhythmSynthesizer;

import static com.badlogic.gdx.input.GestureDetector.*;

public class Acid implements ApplicationListener {

    BitmapFont font;
    private Stage stage;
    LightActor la3 = null;
    private float newZoom;
    public static float rainbowFade = 0f;
    private static float rainbowFadeDir = .005f;
    private Label fpsLabel;

    public Acid() {
    }

    @Override
    public void create() {

        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        stage = new Stage();

        Statics.renderer = new ShapeRenderer();
        Statics.output = new Output();
        Statics.output.getSequencer().setBpm(120);
        Statics.output.getSequencer().randomize();
        Statics.drumsSelected = false;
        Statics.output.getSequencer().randomize();

        InputMultiplexer mult = new InputMultiplexer();
        GestureListener gl = new GestureListener() {

            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                return false;
            }

            @Override
            public boolean longPress(float x, float y) {
                return false;
            }

            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                return false;
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                ((OrthographicCamera) stage.getCamera()).translate(-deltaX/2f,deltaY/2f);
                return false;
            }

            @Override
            public boolean panStop(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean zoom(float initialDistance, float distance) {
                newZoom = initialDistance / distance;
//				((OrthographicCamera) stage.getCamera()).zoom =initialDistance/distance;
                return true;
            }

            @Override
            public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
                return false;
            }

            @Override
            public void pinchStop() {

            }
        };
        GestureDetector gd = new GestureDetector(gl);
        mult.addProcessor(stage);
        mult.addProcessor(gd);

        InputProcessor il = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                if (character == 'z') {
                    SequencerData.undo();
                }

                if (character == 'a') {
                    SequencerData.redo();
                }


                if (character == 's') {
                    DrumData.undo();
                }

                if (character == 'x') {
                    DrumData.redo();
                }
                return true;
            }


            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        };
        mult.addProcessor(il);

        Gdx.input.setInputProcessor(mult);

        font = new BitmapFont(Gdx.app.getFiles().getFileHandle("data/font.fnt",
                FileType.Internal), false);
        font.getData().setScale(.7f);
        Statics.output.start();
        Statics.synth = (BasslineSynthesizer) Statics.output.getTrack(0);
        Statics.drums = (RhythmSynthesizer) Statics.output.getTrack(1);
        Statics.output.getSequencer().drums.randomize();
        Statics.output.getSequencer().bass.randomize();
        Table table = new Table(skin);
        table.setFillParent(true);
        stage.addActor(table);

        RectangleActor rectangleActor=new RectangleActor(330,50);
        rectangleActor.setPosition(122,120);
        table.addActor(rectangleActor);

        CurrentSequencerActor my2 = new CurrentSequencerActor(100,100);
        my2.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                SequencerData.undo();
                return true;
            }
        });
        my2.setPosition(20,300);
        table.addActor(my2);

        CurrentDrumActor my1 = new CurrentDrumActor(100,100);
        my1.setPosition(20,190);
        my1.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                DrumData.undo();
                return true;
            }
        });
        table.addActor(my1);


//        final Touchpad touch1 = new Touchpad(0, skin);
//        touch1.setBounds(15, 15, 100, 100);
//        touch1.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Statics.synth.controlChange(35, (int) (touch1.getKnobX()));
//                Statics.synth.controlChange(34, (int) (touch1.getKnobY()));
//
//            }
//        });
//        touch1.setPosition(20, 190);
//        table.addActor(touch1);

//        final Touchpad touch2 = new Touchpad(0, skin);
//        touch2.setBounds(15, 15, 100, 100);
//        touch2.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Statics.synth.controlChange(36, (int) (touch2.getKnobX()));
//                Statics.synth.controlChange(37, (int) (touch2.getKnobY()));
//
//            }
//        });
//        touch2.setPosition(20, 300);
//        table.addActor(touch2);

        table.setPosition(Gdx.graphics.getWidth() / 2 - 280,
                Gdx.graphics.getHeight() / 2 - 290);
        ((OrthographicCamera) stage.getCamera()).zoom -= .30f;
        newZoom = ((OrthographicCamera) stage.getCamera()).zoom;
        KnobActor[] mya = new KnobActor[10];
        mya[0] = new KnobActor("Tune", 0);
        table.addActor(mya[0]);
        mya[1] = new KnobActor("Cut", 1);
        table.addActor(mya[1]);
        mya[2] = new KnobActor("Res", 2);
        table.addActor(mya[2]);
        mya[3] = new KnobActor("Env", 3);
        table.addActor(mya[3]);
        mya[4] = new KnobActor("Dec", 4);
        table.addActor(mya[4]);
        mya[5] = new KnobActor("Acc", 5);
        table.addActor(mya[5]);
        mya[6] = new KnobActor("bpm", 6);
        table.addActor(mya[6]);
        mya[7] = new KnobActor("Vol", 7);
        table.addActor(mya[7]);


        //bottom row of knobs
        int hj = 130;
        int gh = 125;
        mya[0].setPosition(hj, gh);
        mya[1].setPosition(hj += 56, gh);
        mya[2].setPosition(hj += 56, gh);
        mya[3].setPosition(hj += 56, gh);
        mya[4].setPosition(hj += 56, gh);
        mya[5].setPosition(hj += 56, gh);

        mya[6].setPosition(40, 408);
        mya[7].setPosition(85, 408);

        MatrixActor matrixa = new MatrixActor();
        table.addActor(matrixa);
        matrixa.setPosition(130, 178);

        final TextButton tb4 = new TextButton("Wave", skin);
        table.addActor(tb4);
        tb4.setPosition(470f, 140);
        tb4.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                Statics.output.getSequencer().bass.switchWaveform();
                tb4.setChecked(tb4.isChecked());
                tb4.setColor(tb4.isChecked() ? Color.WHITE : Color.RED);
//                tb4.setText(tb4.isChecked() ? "~":"--");
                tb4.invalidate();
                return true;
            }
        });
        TextButton tb1 = new TextButton("Mutate", skin);
        table.addActor(tb1);
        tb1.setPosition(470, 260);
        tb1.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                Statics.mutate = !Statics.mutate;
                la3.on = Statics.mutate;
                return true;
            }
        });

        TextButton tb3 = new TextButton("Random", skin);
        table.addActor(tb3);
        tb3.setPosition(470, 300);
        tb3.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                Statics.output.getSequencer().randomize();
                if (Statics.drumsSelected){
                    new DrumData();
                }
                else{
                    new SequencerData();
                }
                return true;
            }
        });

        TextButton tb5 = new TextButton("Synth", skin);
        table.addActor(tb5);
        tb5.setPosition(470, 180);
        tb5.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                // Statics.synthOn = !Statics.synthOn;
                Statics.drumsSelected = false;
                return true;
            }
        });

        TextButton tb7 = new TextButton("Clear", skin);
        table.addActor(tb7);
        tb7.setPosition(470, 340);
        tb7.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                if (Statics.drumsSelected) {
                    for (int i = 0; i < Statics.output.getSequencer().rhythm.length; i++) {
                        for (int j = 0; j < Statics.output.getSequencer().rhythm[0].length; j++) {
                            Statics.output.getSequencer().rhythm[i][j] = 0;
                        }
                    }
                } else {
                    for (int i = 0; i < 16; i++) {
                        Statics.output.getSequencer().bassline.pause[i] = true;
                    }
                }

                return true;
            }
        });

        TextButton tb2 = new TextButton("Drums", skin);
        table.addActor(tb2);
        tb2.setPosition(470, 220);
        tb2.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                Statics.drumsSelected = true;
                return true;
            }
        });

        la3 = new LightActor(5, null, false);
        table.addActor(la3);
        la3.setPosition(455, 268);
        la3.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                la3.on = !la3.on;
                Statics.mutate = la3.on;
                return true;
            }
        });

        fpsLabel=new Label("",skin);
        fpsLabel.setPosition(52,407);
        fpsLabel.setFontScale(.5f);
        table.addActor(fpsLabel);

        if (Gdx.app.getType()== Application.ApplicationType.Desktop) {
            TextButton zi = new TextButton("Zoom +", skin);
            table.addActor(zi);
            zi.setPosition(470, 430);
            zi.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y,
                                         int pointer, int button) {
                    newZoom -= .05f;
                    return true;
                }
            });


            TextButton zo = new TextButton("Zoom - ", skin);
            table.addActor(zo);
            zo.setPosition(470, 400);
            zo.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y,
                                         int pointer, int button) {
                    newZoom += .05f;
                    return true;
                }
            });
        };

        final LightActor la2 = new LightActor(5, null, true);
        table.addActor(la2);
        la2.setPosition(455, 188);
        la2.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                la2.on = !la2.on;
                Statics.synthOn = la2.on;
                return true;
            }
        });

        final LightActor la1 = new LightActor(5, null, true);
        table.addActor(la1);
        la1.setPosition(455, 228);
        la1.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                la1.on = !la1.on;
                Statics.drumsOn = la1.on;
                return true;
            }
        });



        new SequencerData();
        new DrumData();

    }

    @Override
    public void resume() {
//		Output.running=true;
        Output.resume();

    }

    @Override
    public void render() {
        // mya.rotate(10);
//        Color c=ColorHelper.rainbowDark();
        Color c=Color.BLACK;
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        if (newZoom < ((OrthographicCamera) stage.getCamera()).zoom)
            ((OrthographicCamera) stage.getCamera()).zoom -= .02f;
        if (newZoom > ((OrthographicCamera) stage.getCamera()).zoom)
            ((OrthographicCamera) stage.getCamera()).zoom += .02f;
        stage.draw();
//        stage.getBatch().begin();
//        font.setColor(ColorHelper.rainbow());
//        font.draw(stage.getBatch(),
//                (int) Statics.output.getSequencer().bpm + "", 90, 360);
//        stage.getBatch().end();
        fpsLabel.setColor(ColorHelper.rainbow());
        fpsLabel.setText((int)Statics.output.getSequencer().bpm + "");
        rainbowFade += rainbowFadeDir;
        while (rainbowFade < 0f || rainbowFade > 1f) {
            rainbowFadeDir = -rainbowFadeDir;
            rainbowFade += rainbowFadeDir;
//            rainbowFadeDir+= (Math.random()-.5f)/10f;
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().setScreenSize(width,height);
    }

    @Override
    public void pause() {
        Output.pause();
    }

    @Override
    public void dispose() {
        Output.running = false;
        Statics.output.dispose();
    }
}
