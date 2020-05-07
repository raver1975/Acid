package com.acid;

import com.acid.actors.BelowKnobsActor;
import com.acid.actors.CurrentDrumActor;
import com.acid.actors.CurrentKnobsActor;
import com.acid.actors.CurrentSequencerActor;
import com.acid.actors.DrumActor;
import com.acid.actors.KnobActor;
import com.acid.actors.LightActor;
import com.acid.actors.RectangleActor;
import com.acid.actors.SequenceActor;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Stack;

import io.nayuki.flac.app.EncodeWavToFlac;
import synth.BasslineSynthesizer;
import synth.Output;

import static com.badlogic.gdx.input.GestureDetector.GestureListener;

//import io.ipfs.api.*;
//import io.ipfs.api.NamedStreamable.FileWrapper;
//import io.ipfs.multiaddr.MultiAddress;
//import io.textile.ipfslite.Peer;
//import de.sciss.jump3r.Main;
//import org.jcodec.api.transcode.TranscodeMain;

public class Acid implements ApplicationListener {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    static float rainbowFade = 0f;
    private static float rainbowFadeDir = .005f;
    ArrayList<SequencerData> sequencerDataArrayList = new ArrayList<SequencerData>();
    ArrayList<DrumData> drumDataArrayList = new ArrayList<DrumData>();
    ArrayList<KnobData> knobsArrayList = new ArrayList<KnobData>();
    int songPosition = 0;
    int maxSongPosition = 0;
    int minSongPosition = 0;
    private BitmapFont font;
    private Stage stage;
    private LightActor mutateLight = null;
    private float newZoom;
    private Label BpmLabel;
    private SequenceActor sequenceMatrix;
    private DrumActor drumMatrix;
    private double[] knobs;
    private boolean drumsSelected;
    private float drumsSynthScale = 1.0f;
    private int prevStep = -1;
    private Label maxSongLengthLabel;
    private Label songLengthLabel;
    private Label minSongLengthLabel;
    private Label stepLabel;
    private Label minSongLengthCaption;
    private Label songLengthCaption;
    private Label maxSongLengthCaption;
    private Label stepCaption;
    private TextButton waveButton;
    private SelectBox<String> selectSongList;
    private ArrayList<String> fileList;
    private Label knobDataArrayListLabel;
    private Label sequencerDataArrayListLabel;
    private Label drumDataArrayListLabel;
    private TextButton exportSongButton;
    private TextButton freeButton;
    private TextButton pauseButton;

    public Acid() {
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Returns if the actor is visible or not. Useful to implement 2D culling.
     **/
    public static boolean actorIsVisible(Actor actor) {
        Vector2 actorStagePos = actor.localToStageCoordinates(new Vector2(0, 0));
        Vector2 actorStagePosTl = actor.localToStageCoordinates(new Vector2(
                actor.getWidth(),
                actor.getHeight()));

        Vector3 actorPixelPos = new Vector3(actorStagePos.x, actorStagePos.y, 0);
        Vector3 actorPixelPosTl = new Vector3(actorStagePosTl.x, actorStagePosTl.y, 0);

        actorPixelPos = actor.getStage().getCamera().project(actorPixelPos);
        actorPixelPosTl = actor.getStage().getCamera().project(actorPixelPosTl);

        return !(actorPixelPosTl.x < 0 ||
                actorPixelPos.x > Gdx.graphics.getWidth() ||
                actorPixelPosTl.y < 0 ||
                actorPixelPos.y > Gdx.graphics.getHeight()
        );
    }

    @Override
    public void create() {
        Output.resume();
        final Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        stage = new Stage();

        Statics.renderer = new ShapeRenderer();
        Statics.output = new Output();
        Statics.output.getSequencer().setBpm(120);
        Statics.output.getSequencer().randomizeRhythm();
        drumsSelected = false;
        Statics.output.getSequencer().randomizeSequence();

        InputMultiplexer multiplexer = new InputMultiplexer();
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
                //((OrthographicCamera) stage.getCamera()).translate(-deltaX / 2f, deltaY / 2f);
                return false;
            }

            @Override
            public boolean panStop(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean zoom(float initialDistance, float distance) {
//                newZoom = (initialDistance / distance) * ((OrthographicCamera) stage.getCamera()).zoom;
                return true;
            }

            @Override
            public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
                return false;
            }

            @Override
            public void pinchStop() {
//                newZoom = ((OrthographicCamera) stage.getCamera()).zoom;
            }
        };
        GestureDetector gd = new GestureDetector(gl);
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(gd);

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
        multiplexer.addProcessor(il);

        Gdx.input.setInputProcessor(multiplexer);

        font = new BitmapFont(Gdx.app.getFiles().getFileHandle("data/font.fnt",
                FileType.Internal), false);
        font.getData().setScale(.7f);
        Statics.output.start();
        Statics.synth = (BasslineSynthesizer) Statics.output.getTrack(0);
        Statics.drums = Statics.output.getTrack(1);
        Statics.output.getSequencer().drums.randomize();
        Statics.output.getSequencer().bass.randomize();
        Table table = new Table(skin);
//        table.setFillParent(true);
        stage.addActor(table);

        RectangleActor rectangleActor = new RectangleActor(330, 50);
        rectangleActor.setPosition(122, 120);
        table.addActor(rectangleActor);

        final CurrentSequencerActor currentSequencerActor = new CurrentSequencerActor(100, 100);
        currentSequencerActor.setPosition(0, 295);
        currentSequencerActor.addListener(new ActorGestureListener() {

            @Override
            public void tap(InputEvent event, float stageX, float stageY, int count, int button) {
//                SequencerData.undo();
                if (SequencerData.peekStack() != null) SequencerData.peekStack().refresh();

            }

//            @Override
//            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
//                if (velocityX > 0) SequencerData.redo();
//                if (velocityX < 0) SequencerData.popStack();
//            }
        });
        currentSequencerActor.addListener(new DragListener() {
            float xi = currentSequencerActor.getX();
            float xs = 0;

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                xs=xi;
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (SequencerData.sequences.size() > 1) {
                    currentSequencerActor.moveBy(x - currentSequencerActor.getWidth() / 2, 0);

                    if (currentSequencerActor.getX() < xs - currentSequencerActor.getWidth() / 4f) {
                        shiftStackLeft(SequencerData.sequences);
                        currentSequencerActor.setPosition(xs, currentSequencerActor.getY());
                        cancel();
                    }
                    if (currentSequencerActor.getX() > xs + currentSequencerActor.getWidth() / 4f) {
                        shiftStackRight(SequencerData.sequences);
                        currentSequencerActor.setPosition(xs, currentSequencerActor.getY());
                        cancel();
                    }
                }
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (Math.abs(currentSequencerActor.getX() - xi) > .001f) {
                            float dir = (currentSequencerActor.getX() - xi) / 4;
                            currentSequencerActor.moveBy(-dir, 0);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        currentSequencerActor.setPosition(xi, currentSequencerActor.getY());
                    }
                }).start();
            }

        });
        table.addActor(currentSequencerActor);

        sequencerDataArrayListLabel = new Label("", skin);
        sequencerDataArrayListLabel.setPosition(-10, 290);
        sequencerDataArrayListLabel.setFontScale(1f);
        table.addActor(sequencerDataArrayListLabel);

        TextButton pushtoSequencer = new TextButton("X", skin);
        pushtoSequencer.setPosition(105, 305);
        table.addActor(pushtoSequencer);
        pushtoSequencer.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
//                SequencerData.undo();
                SequencerData.popStack();

                return true;
            }
        });
        TextButton popFromSequencer = new TextButton("<", skin);
        popFromSequencer.setPosition(105, 360);
        table.addActor(popFromSequencer);
        popFromSequencer.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                SequencerData.pushStack(new SequencerData());
                return true;
            }
        });


//        Gdx.files.local("filelist.ser").delete();
        try {
            Object o = Serializer.load("filelist.ser");
            if (o != null && o instanceof ArrayList) fileList = (ArrayList<String>) o;
        } catch (Exception e1) {
            //e1.printStackTrace();
        }
        if (fileList == null) {
            fileList = new ArrayList<String>();
            fileList.add("<New>");
            try {
                Serializer.save(fileList, "filelist.ser");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        selectSongList = new SelectBox<String>(skin);
        selectSongList.setPosition(130, 465);
        selectSongList.setSize(170, 30);
        selectSongList.setZIndex(0);
        table.addActor(selectSongList);
        selectSongList.setItems(new Array<String>(fileList.toArray(new String[]{})));

        TextButton saveSongButton = new TextButton("Save", skin);
        saveSongButton.setPosition(305, 465);
        table.addActor(saveSongButton);
        saveSongButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                if (selectSongList.getSelectedIndex() > 0) {
                    String saveName = selectSongList.getSelected().replaceAll("[^a-zA-Z0-9]", "");
                    if (saveName.length() == 0) saveName = "blank";
                    saveSong(saveName, skin);

                } else {
                    Gdx.input.getTextInput(new Input.TextInputListener() {
                        @Override
                        public void input(String text) {
                            String saveName = text.replaceAll("[^a-zA-Z0-9]", "");
                            if (saveName.length() == 0) saveName = "blank";
                            saveSong(saveName, skin);
                        }

                        @Override
                        public void canceled() {

                        }
                    }, "Save Song", "", "Song Title");
                }
                return true;
            }
        });

        TextButton loadSongButton = new TextButton("Load", skin);
        loadSongButton.setPosition(350, 465);
        table.addActor(loadSongButton);
        loadSongButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                Dialog dialog = new Dialog("Load song " + selectSongList.getSelected() + "?", skin) {

                    @Override
                    protected void result(Object object) {
                        boolean yes = (Boolean) object;
                        if (yes) {
                            if (selectSongList.getSelectedIndex() > 0) {
                                loadSong(selectSongList.getSelected());
                            } else {
                                sequencerDataArrayList.clear();
                                drumDataArrayList.clear();
                                knobsArrayList.clear();
                                SequencerData.sequences.clear();
                                KnobData.sequences.clear();
                                DrumData.sequences.clear();
                                Statics.output.getSequencer().randomizeRhythm();
                                Statics.output.getSequencer().bass.randomize();
                                Statics.output.getSequencer().randomizeSequence();
                                maxSongPosition = 0;
                                minSongPosition = 0;
                                songPosition = 0;
                                KnobImpl.refill();

                            }
                        } else {
                            return;
                        }
                    }

                    @Override
                    public Dialog show(Stage stage) {
                        return super.show(stage);
                    }

                    @Override
                    public void cancel() {
                        super.cancel();
                    }

                    @Override
                    public float getPrefHeight() {
                        return 50f;
                    }
                };
                dialog.button("Yes", true);
                dialog.button("No", false);
                dialog.key(Input.Keys.ENTER, true);
                dialog.key(Input.Keys.ESCAPE, false);
                dialog.show(stage);

                return true;
            }
        });

        freeButton = new TextButton("Free", skin);
        freeButton.setChecked(false);
        freeButton.setColor(freeButton.isChecked() ? Color.WHITE : Color.RED);
        table.addActor(freeButton);
        freeButton.setPosition(15f, 95);
        freeButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,

                                     int pointer, int button) {
                freeButton.setColor(freeButton.isChecked() ? Color.RED : Color.WHITE);
                Statics.free = freeButton.isChecked();
                return true;
            }
        });

        exportSongButton = new TextButton("Export", skin);
        exportSongButton.setPosition(455, 465);
        table.addActor(exportSongButton);
        exportSongButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                if (selectSongList.getSelectedIndex() == 0) {
                    Gdx.input.getTextInput(new Input.TextInputListener() {
                        @Override
                        public void input(String text) {
                            String selected = text.replaceAll("[^a-zA-Z0-9]", "");
                            if (selected.length() == 0) {
                                selected = "blank";
                            }
                            selected += ".wav";
                            export(selected);
                        }

                        @Override
                        public void canceled() {

                        }
                    }, "Export WAV and FLAC (be patient, share link will open after upload)", "", "Song Title");
                } else {
                    String selected = selectSongList.getSelected().replaceAll("[^a-zA-Z0-9]", "");
                    selected += ".wav";
                    export(selected);
                }
                return true;
            }

            private void export(String selected) {
                final FileHandle fileHandle = Gdx.files.local(selected);
                if (!fileHandle.exists()) {
                    startSaving(fileHandle);
                } else {
                    Dialog dialog = new Dialog("Overwrite " + selected + "?", skin) {

                        @Override
                        protected void result(Object object) {
                            boolean yes = (Boolean) object;
                            if (yes) {
                                startSaving(fileHandle);
                            } else {
                                return;
                            }
                        }

                        @Override
                        public Dialog show(Stage stage) {
                            return super.show(stage);
                        }

                        @Override
                        public void cancel() {
                            super.cancel();
                        }

                        @Override
                        public float getPrefHeight() {
                            return 50f;
                        }
                    };
                    dialog.button("Yes", true);
                    dialog.button("No", false);
                    dialog.key(Input.Keys.ENTER, true);
                    dialog.key(Input.Keys.ESCAPE, false);
                    dialog.show(stage);
                }
            }
        });


        TextButton inbutton = new TextButton(" In ", skin);
        inbutton.setPosition(470, 430);
        table.addActor(inbutton);
        inbutton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(final String text) {
                        Object o = null;
                        try {
                            o = Serializer.fromBase64(text);
                            if (o == null || !(o instanceof SaveObject)) {
                                return;
                            }
                        } catch (Exception e) {
                            return;
                        }


                        Dialog dialog = new Dialog("Restore song from clipboard? ", skin) {

                            @Override
                            protected void result(Object object) {
                                Boolean yes = (Boolean) object;
                                if (yes) {
                                    Object o = null;
                                    try {
                                        o = Serializer.fromBase64(text);
                                        if (o != null && o instanceof SaveObject) {
                                            boolean free = Statics.free;
                                            boolean rec = Statics.recording;
                                            Statics.free = false;
                                            Statics.recording = false;
                                            if (o != null) {
                                                ((SaveObject) o).restore(Acid.this);
                                            }
                                            Statics.free = free;
                                            Statics.recording = rec;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                            @Override
                            public Dialog show(Stage stage) {
                                return super.show(stage);
                            }

                            @Override
                            public void cancel() {
                                super.cancel();
                            }

                            @Override
                            public float getPrefHeight() {
                                return 50f;
                            }
                        };
                        dialog.button("Yes", true);
                        dialog.button("No", false);
                        dialog.show(stage);

                    }


                    @Override
                    public void canceled() {

                    }
                }, "Import Song from clipboard.", Gdx.app.getClipboard().

                        getContents(), "");
                return true;
            }
        });

        TextButton outbutton = new TextButton("Out", skin);
        outbutton.setPosition(505, 430);
        table.addActor(outbutton);
        outbutton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                try {
                    String out = Serializer.toBase64(new SaveObject(Acid.this));
                    Gdx.app.getClipboard().setContents(out);
                    Dialog dialog = new Dialog("Copied current song to clipboard. ", skin) {

                        @Override
                        protected void result(Object object) {
                        }

                        @Override
                        public Dialog show(Stage stage) {
                            return super.show(stage);
                        }

                        @Override
                        public void cancel() {
                            super.cancel();
                        }

                        @Override
                        public float getPrefHeight() {
                            return 50f;
                        }
                    };
                    dialog.button("Ok", true);
                    dialog.show(stage);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });

        TextButton deleteSongButton = new TextButton("Delete", skin);
        deleteSongButton.setPosition(395, 465);
        table.addActor(deleteSongButton);
        deleteSongButton.addListener(new

                                             InputListener() {
                                                 public boolean touchDown(InputEvent event, float x, float y,
                                                                          int pointer, int button) {
                                                     if (selectSongList.getSelectedIndex() > 0) {
                                                         Dialog dialog = new Dialog("Delete song " + selectSongList.getSelected() + "?", skin) {

                                                             @Override
                                                             protected void result(Object object) {
                                                                 boolean yes = (Boolean) object;
                                                                 if (yes) {
                                                                     fileList.remove(selectSongList.getSelected());
                                                                     try {
                                                                         Serializer.save(fileList, "fiielist.ser");
                                                                     } catch (Exception e) {
                                                                         e.printStackTrace();
                                                                     }
                                                                     selectSongList.setItems(new Array<String>(fileList.toArray(new String[]{})));
                                                                 } else {
                                                                     return;
                                                                 }
                                                             }

                                                             @Override
                                                             public Dialog show(Stage stage) {
                                                                 return super.show(stage);
                                                             }

                                                             @Override
                                                             public void cancel() {
                                                                 super.cancel();
                                                             }

                                                             @Override
                                                             public float getPrefHeight() {
                                                                 return 50f;
                                                             }
                                                         };
                                                         dialog.button("Yes", true);
                                                         dialog.button("No", false);
                                                         dialog.key(Input.Keys.ENTER, true);
                                                         dialog.key(Input.Keys.ESCAPE, false);
                                                         dialog.show(stage);
                                                     }
                                                     return true;
                                                 }
                                             });

        final CurrentDrumActor currentDrumActor = new CurrentDrumActor(100, 100);
        currentDrumActor.setPosition(0, 185);
        currentDrumActor.addListener(new

                                             ActorGestureListener() {

                                                 @Override
                                                 public void tap(InputEvent event, float stageX, float stageY, int count, int button) {
                                                     if (DrumData.peekStack() != null)
                                                         DrumData.peekStack().refresh();

                                                 }
                                             });
        currentDrumActor.addListener(new DragListener() {
            float xi = currentDrumActor.getX();
            float xs = 0;

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                xs=xi;
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (DrumData.sequences.size() > 1) {
                    currentDrumActor.moveBy(x - currentDrumActor.getWidth() / 2, 0);

                    if (currentDrumActor.getX() < xs - currentDrumActor.getWidth() / 4f) {
                        shiftStackLeft(DrumData.sequences);
                        currentDrumActor.setPosition(xs, currentDrumActor.getY());
                        cancel();
                    }
                    if (currentDrumActor.getX() > xs + currentDrumActor.getWidth() / 4f) {
                        shiftStackRight(DrumData.sequences);
                        currentDrumActor.setPosition(xs, currentDrumActor.getY());
                        cancel();
                    }
                }
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (Math.abs(currentDrumActor.getX() - xi) > .001f) {
                            float dir = (currentDrumActor.getX() - xi) / 4;
                            currentDrumActor.moveBy(-dir, 0);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        currentDrumActor.setPosition(xi, currentDrumActor.getY());
                    }
                }).start();
            }

        });
        table.addActor(currentDrumActor);

        drumDataArrayListLabel = new Label("", skin);
        drumDataArrayListLabel.setPosition(-10, 180);
        drumDataArrayListLabel.setFontScale(1f);
        table.addActor(drumDataArrayListLabel);

        TextButton pushtoDrum = new TextButton("X", skin);
        pushtoDrum.setPosition(105, 195);
        table.addActor(pushtoDrum);
        pushtoDrum.addListener(new

                                       InputListener() {
                                           public boolean touchDown(InputEvent event, float x, float y,
                                                                    int pointer, int button) {
//                SequencerData.undo();
                                               DrumData.popStack();
                                               return true;
                                           }
                                       });
        TextButton popFromDrum = new TextButton("<", skin);
        popFromDrum.setPosition(105, 250);
        table.addActor(popFromDrum);
        popFromDrum.addListener(new

                                        InputListener() {
                                            public boolean touchDown(InputEvent event, float x, float y,
                                                                     int pointer, int button) {
                                                DrumData.pushStack(new DrumData());

                                                return true;
                                            }
                                        });

        final CurrentKnobsActor currentKnobsActor = new CurrentKnobsActor(80, 70);
        currentKnobsActor.setPosition(460, 200);
        table.addActor(currentKnobsActor);
        currentKnobsActor.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float stageX, float stageY, int count, int button) {
                if (KnobData.peekStack() != null)
                    KnobData.peekStack().refresh();
            }
        });
        currentKnobsActor.addListener(new DragListener() {
            float xi=currentKnobsActor.getX();
            float xs = 0;

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                xs = xi;
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (KnobData.sequences.size() > 1) {
                    currentKnobsActor.moveBy(x - currentKnobsActor.getWidth() / 2, 0);

                    if (currentKnobsActor.getX() < xs - currentKnobsActor.getWidth() / 4f) {
                        shiftStackLeft(KnobData.sequences);
                        currentKnobsActor.setPosition(xs, currentKnobsActor.getY());
                        cancel();
                    }
                    if (currentKnobsActor.getX() > xs + currentKnobsActor.getWidth() / 4f) {
                        shiftStackRight(KnobData.sequences);
                        currentKnobsActor.setPosition(xs, currentKnobsActor.getY());
                        cancel();
                    }
                }
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (Math.abs(currentKnobsActor.getX() - xi) > .001f) {
                            float dir = (currentKnobsActor.getX() - xi) / 4;
                            currentKnobsActor.moveBy(-dir, 0);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        currentKnobsActor.setPosition(xi, currentKnobsActor.getY());
                    }
                }).start();
            }
        });

        knobDataArrayListLabel = new Label("", skin);
        knobDataArrayListLabel.setPosition(450, 195);
        knobDataArrayListLabel.setFontScale(1f);
        table.addActor(knobDataArrayListLabel);


        TextButton pushtoKnob = new TextButton(" X ", skin);
        pushtoKnob.setPosition(510, 168);
        table.addActor(pushtoKnob);
        pushtoKnob.addListener(new

                                       InputListener() {
                                           public boolean touchDown(InputEvent event, float x, float y,
                                                                    int pointer, int button) {
                                               KnobData.popStack();
                                               return true;
                                           }
                                       });
        TextButton popFromKnob = new TextButton(" ^ ", skin);
        popFromKnob.setPosition(470, 168);
        table.addActor(popFromKnob);
        popFromKnob.addListener(new

                                        InputListener() {
                                            public boolean touchDown(InputEvent event, float x, float y,
                                                                     int pointer, int button) {
                                                KnobData.pushStack(KnobData.factory());
                                                return true;
                                            }
                                        });


        BelowKnobsActor belowKnobsActor = new BelowKnobsActor(80, 70);
        belowKnobsActor.setPosition(460, 95);
        belowKnobsActor.addListener(new

                                            ActorGestureListener() {

//            @Override
//            public void tap(InputEvent event,float stageX, float stageY, int count, int button){
//                DrumData.undo();
//            }

                                                @Override
                                                public void fling(InputEvent event, float velocityX, float velocityY, int button) {
//                System.out.println("swipe!! " + velocityX + ", " + velocityY);
//                if (velocityX > 0) KnobData.redo();
//                if (velocityX < 0) KnobData.undo();
                                                }
                                            });
        table.addActor(belowKnobsActor);
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

        table.setPosition(Gdx.graphics.getWidth() / 2f - 275,
                Gdx.graphics.getHeight() / 2f - 290);
//        table.setSize(800,600);
//        table.setSize(600,600);
//        ((OrthographicCamera)stage.getCamera()).setToOrtho(false,Gdx.graphics.getHeight(),Gdx.graphics.getWidth());


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
        mya[8] = new KnobActor("Delay", 8);
        table.addActor(mya[8]);
        mya[9] = new KnobActor("Fack", 9);
        table.addActor(mya[9]);


        //bottom row of knobs
        int hj = 130;
        int gh = 125;
        mya[0].setPosition(hj, gh);

        mya[1].setPosition(hj += 56, gh);

        mya[2].setPosition(hj += 56, gh);

        mya[3].setPosition(hj += 56, gh);

        mya[4].setPosition(hj += 56, gh);

        mya[5].setPosition(hj += 56, gh);

        mya[6].setPosition(40, 454);

        mya[7].setPosition(85, 454);

        mya[8].setPosition(40, 398);

        mya[9].setPosition(85, 398);


        drumMatrix = new

                DrumActor();
        table.addActor(drumMatrix);
        drumMatrix.setScale(drumsSynthScale);
        drumMatrix.setPosition(130, 178);

        sequenceMatrix = new

                SequenceActor();
        table.addActor(sequenceMatrix);
        sequenceMatrix.setScale(1f - drumsSynthScale);
        sequenceMatrix.setPosition(130, 178);


        final TextButton prevMin = new TextButton(" < ", skin);
        table.addActor(prevMin);
        prevMin.setPosition(140, 95);
        prevMin.addListener(new

                                    InputListener() {
                                        public boolean touchDown(InputEvent event, float x, float y,
                                                                 int pointer, int button) {
                                            if (minSongPosition > 0) {
                                                minSongPosition--;
                                            }
                                            return true;
                                        }
                                    });


        minSongLengthLabel = new

                Label("", skin);
        minSongLengthLabel.setPosition(166, 105);
        minSongLengthLabel.setFontScale(1f);
        table.addActor(minSongLengthLabel);

        minSongLengthCaption = new

                Label("Start", skin);
        minSongLengthCaption.setPosition(168, 75);
        minSongLengthCaption.setFontScale(.75f);
        table.addActor(minSongLengthCaption);


        final TextButton nextMin = new TextButton(" > ", skin);
        table.addActor(nextMin);
        nextMin.setPosition(200, 95);
        nextMin.addListener(new

                                    InputListener() {
                                        public boolean touchDown(InputEvent event, float x, float y,
                                                                 int pointer, int button) {
                                            if (minSongPosition < maxSongPosition && minSongPosition < 998)
                                                minSongPosition++;

                                            return true;
                                        }
                                    });


        final TextButton prev = new TextButton(" < ", skin);
        table.addActor(prev);
        prev.setPosition(245, 95);
        prev.addListener(new

                                 InputListener() {
                                     public boolean touchDown(InputEvent event, float x, float y,
                                                              int pointer, int button) {
                                         if (songPosition > minSongPosition) {
                                             swapPattern(songPosition, --songPosition);
                                         }
                                         return true;
                                     }
                                 });

        songLengthLabel = new

                Label("", skin);
        songLengthLabel.setPosition(270, 107);
        songLengthLabel.setFontScale(1.5f);
        table.addActor(songLengthLabel);

        songLengthCaption = new

                Label("Current", skin);
        songLengthCaption.setPosition(273, 75);
        songLengthCaption.setFontScale(.75f);
        table.addActor(songLengthCaption);

        final TextButton next = new TextButton(" > ", skin);
        table.addActor(next);
        next.setPosition(320, 95);
        next.addListener(new

                                 InputListener() {
                                     public boolean touchDown(InputEvent event, float x, float y,
                                                              int pointer, int button) {
                                         if (songPosition == maxSongPosition && songPosition < 998)
                                             return true;
                                         swapPattern(songPosition, ++songPosition);
                                         return true;
                                     }
                                 });

        final TextButton prevMax = new TextButton(" < ", skin);
        table.addActor(prevMax);
        prevMax.setPosition(363, 95);
        prevMax.addListener(new

                                    InputListener() {
                                        public boolean touchDown(InputEvent event, float x, float y,
                                                                 int pointer, int button) {
                                            if (maxSongPosition > 0 && maxSongPosition > minSongPosition) {
                                                maxSongPosition--;
                                            }
                                            return true;
                                        }
                                    });

        maxSongLengthLabel = new

                Label("", skin);
        maxSongLengthLabel.setPosition(389, 105);
        maxSongLengthLabel.setFontScale(1f);
        table.addActor(maxSongLengthLabel);

        maxSongLengthCaption = new

                Label("End", skin);
        maxSongLengthCaption.setPosition(394, 75);
        maxSongLengthCaption.setFontScale(.75f);
        table.addActor(maxSongLengthCaption);

        final TextButton nextMax = new TextButton(" > ", skin);
        table.addActor(nextMax);
        nextMax.setPosition(423, 95);
        nextMax.addListener(new

                                    InputListener() {
                                        public boolean touchDown(InputEvent event, float x, float y,
                                                                 int pointer, int button) {
                                            if (maxSongPosition < 998) maxSongPosition++;
                                            return true;
                                        }
                                    });


        final TextButton recButton = new TextButton("Rec", skin);
        recButton.setChecked(false);
        recButton.setColor(recButton.isChecked() ? Color.WHITE : Color.RED);
        table.addActor(recButton);
        recButton.setPosition(60f, 95);
        recButton.addListener(new

                                      InputListener() {
                                          public boolean touchDown(InputEvent event, float x, float y,

                                                                   int pointer, int button) {
                                              recButton.setColor(recButton.isChecked() ? Color.RED : Color.WHITE);
                                              Statics.recording = recButton.isChecked();
                                              return true;
                                          }
                                      });


        final TextButton prevStep = new TextButton(" < ", skin);
        table.addActor(prevStep);
        prevStep.setPosition(35f, 145);
        prevStep.addListener(new

                                     InputListener() {
                                         public boolean touchDown(InputEvent event, float x, float y,

                                                                  int pointer, int button) {
                                             Statics.output.getSequencer().step = (16 + Statics.output.getSequencer().step - 1) % 16;
                                             return true;
                                         }
                                     });
        stepLabel = new

                Label("", skin);
        stepLabel.setPosition(60, 158);
        stepLabel.setFontScale(1.5f);
        table.addActor(stepLabel);

        stepCaption = new

                Label("step", skin);
        stepCaption.setPosition(65, 125);
        stepCaption.setFontScale(.75f);
        table.addActor(stepCaption);


        final TextButton nextStep = new TextButton(" > ", skin);
        table.addActor(nextStep);
        nextStep.setPosition(98f, 145);
        nextStep.addListener(new

                                     InputListener() {
                                         public boolean touchDown(InputEvent event, float x, float y,

                                                                  int pointer, int button) {
                                             Statics.output.getSequencer().step = (Statics.output.getSequencer().step + 1) % 16;
                                             return true;
                                         }
                                     });


        pauseButton = new

                TextButton(" || ", skin);
        pauseButton.setChecked(false);
        table.addActor(pauseButton);
        pauseButton.setPosition(100f, 95);
        pauseButton.addListener(new

                                        InputListener() {
                                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                                if (!Output.isPaused()) Output.pause();
                                                else Output.resume();
                                                pauseButton.setColor(Output.isPaused() ? Color.RED : Color.WHITE);
                                                return true;
                                            }
                                        });

        waveButton = new

                TextButton(" # ", skin);
        table.addActor(waveButton);
        waveButton.setPosition(520f, 280);
        waveButton.addListener(new

                                       InputListener() {
                                           public boolean touchDown(InputEvent event, float x, float y,
                                                                    int pointer, int button) {
                                               Statics.output.getSequencer().bass.switchWaveform();
                                               waveButton.setChecked(Statics.waveSquare);
                                               waveButton.setColor(Statics.waveSquare ? Color.WHITE : Color.RED);
                                               waveButton.setText(Statics.waveSquare ? " # " : " ^ ");
                                               waveButton.invalidate();
                                               return true;
                                           }
                                       });

        TextButton randomButton = new TextButton("Random", skin);
        table.addActor(randomButton);
        randomButton.setPosition(470, 340);
        randomButton.addListener(new

                                         InputListener() {
                                             //            public void touchUp(InputEvent event, float x, float y,
//                                     int pointer, int button) {
//                new KnobData();
//            }
                                             public boolean touchDown(InputEvent event, float x, float y,
                                                                      int pointer, int button) {

                                                 if (drumsSelected) {
                                                     Statics.output.getSequencer().randomizeRhythm();
                                                 } else {
                                                     Statics.output.getSequencer().bass.randomize();
                                                     Statics.output.getSequencer().randomizeSequence();
                                                     KnobImpl.refill();

                                                 }
                                                 return true;
                                             }
                                         });

        TextButton clearButton = new TextButton("Clear", skin);
        table.addActor(clearButton);
        clearButton.setPosition(470, 370);
        clearButton.addListener(new

                                        InputListener() {
                                            public boolean touchDown(InputEvent event, float x, float y,
                                                                     int pointer, int button) {
                                                if (drumsSelected) {
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

        final TextButton drumsButton = new TextButton("Drums", skin);
        table.addActor(drumsButton);
        drumsButton.setPosition(470, 310);


        final LightActor drumsLight = new LightActor(5, null, true);
        table.addActor(drumsLight);
        drumsLight.setPosition(455, 318);
        drumsLight.addListener(new

                                       InputListener() {
                                           public boolean touchDown(InputEvent event, float x, float y,
                                                                    int pointer, int button) {
                                               drumsLight.on = !drumsLight.on;
                                               Statics.drumsOn = drumsLight.on;
                                               return true;
                                           }
                                       });

        BpmLabel = new

                Label("", skin);
        BpmLabel.setPosition(52, 453);
        BpmLabel.setFontScale(.5f);
        table.addActor(BpmLabel);


/*
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
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
        }
*/

//        TextButton donateButton = new TextButton("Donate!", skin);
//        table.addActor(donateButton);
//        donateButton.setPosition(470, 400);
//        donateButton.addListener(new InputListener() {
//            public boolean touchDown(InputEvent event, float x, float y,
//                                     int pointer, int button) {
//                Gdx.net.openURI("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CNP4H9HGEQDEA&source=url");
//                return true;
//            }
//        });

        TextButton githubButton = new TextButton("Luv", skin);
        table.addActor(githubButton);
        githubButton.setPosition(515, 465);
        githubButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                Gdx.net.openURI("https://github.com/raver1975/Acid");
                return true;
            }
        });


        final TextButton synthButton = new TextButton("Synth", skin);
        table.addActor(synthButton);
        synthButton.setColor(drumsSelected ? Color.WHITE : Color.RED);
        drumsButton.setColor(drumsSelected ? Color.RED : Color.WHITE);
        synthButton.setPosition(470, 280);
        synthButton.addListener(new

                                        InputListener() {
                                            public boolean touchDown(InputEvent event, float x, float y,
                                                                     int pointer, int button) {
                                                drumsSelected = false;
                                                synthButton.setChecked(drumsSelected);
                                                synthButton.setColor(drumsSelected ? Color.WHITE : Color.RED);
                                                drumsButton.setColor(drumsSelected ? Color.RED : Color.WHITE);
                                                return true;
                                            }
                                        });
        drumsButton.addListener(new

                                        InputListener() {
                                            public boolean touchDown(InputEvent event, float x, float y,
                                                                     int pointer, int button) {
                                                drumsSelected = true;
                                                synthButton.setChecked(drumsSelected);
                                                synthButton.setColor(drumsSelected ? Color.WHITE : Color.RED);
                                                drumsButton.setColor(drumsSelected ? Color.RED : Color.WHITE);
                                                return true;
                                            }
                                        });


        final LightActor synthLight = new LightActor(5, null, true);
        table.addActor(synthLight);
        synthLight.setPosition(455, 288);
        synthLight.addListener(new

                                       InputListener() {
                                           public boolean touchDown(InputEvent event, float x, float y,
                                                                    int pointer, int button) {
                                               synthLight.on = !synthLight.on;
                                               Statics.synthOn = synthLight.on;
                                               return true;
                                           }
                                       });

        SequencerData.pushStack(new

                SequencerData());
        DrumData.pushStack(new

                DrumData());
        KnobData.pushStack(KnobData.factory());


        while (actorIsVisible(table)) {
            ((OrthographicCamera) stage.getCamera()).zoom -= .001f;
            //System.out.println(((OrthographicCamera) stage.getCamera()).zoom);
            stage.getCamera().update();
        }
        while (!actorIsVisible(table)) {
            ((OrthographicCamera) stage.getCamera()).zoom += .001f;
            //System.out.println(((OrthographicCamera) stage.getCamera()).zoom);
            stage.getCamera().update();
        }
        while (actorIsVisible(table)) {
            ((OrthographicCamera) stage.getCamera()).zoom -= .001f;
            //System.out.println(((OrthographicCamera) stage.getCamera()).zoom);
            stage.getCamera().update();
        }

        newZoom = .75f * ((OrthographicCamera) stage.getCamera()).zoom;

        FileHandle ff = Gdx.files.local("supersecrettempfile.txt");
        if (ff.exists()) {
            String text = ff.readString();
            Object o = null;
            try {
                o = Serializer.fromBase64(text);
                if (o != null && o instanceof SaveObject) {
                    boolean free = Statics.free;
                    boolean rec = Statics.recording;
                    Statics.free = false;
                    Statics.recording = false;
                    if (o != null) {
                        ((SaveObject) o).restore(Acid.this);
                    }
                    Statics.free = free;
                    Statics.recording = rec;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void loadSong(String name) {
        try {
            byte[] bytesOfMessage = name.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            String loadfilename = bytesToHex(thedigest);
            Object o = Serializer.load(loadfilename);
            boolean free = Statics.free;
            boolean rec = Statics.recording;
            Statics.free = false;
            Statics.recording = false;
            if (o != null) {
                ((SaveObject) o).restore(this);
            }
            Statics.free = free;
            Statics.recording = rec;
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void saveSong(final String name, Skin skin) {

        if (!fileList.contains(name)) {
            fileList.add(name);
            try {
                Serializer.save(fileList, "filelist.ser");
            } catch (Exception e) {
                e.printStackTrace();
            }
            selectSongList.setItems(new Array<String>(fileList.toArray(new String[]{})));
            selectSongList.setSelected(name);
            try {
                byte[] bytesOfMessage = name.getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] thedigest = md.digest(bytesOfMessage);
                String savefilename = bytesToHex(thedigest);
                Serializer.save(new SaveObject(this), savefilename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Dialog dialog = new Dialog("Overwrite " + name + "?", skin) {

                @Override
                protected void result(Object object) {
                    boolean yes = (Boolean) object;
                    if (yes) {
                        selectSongList.setItems(new Array<String>(fileList.toArray(new String[]{})));
                        selectSongList.setSelected(name);
                        try {
                            byte[] bytesOfMessage = name.getBytes("UTF-8");
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            byte[] thedigest = md.digest(bytesOfMessage);
                            String savefilename = bytesToHex(thedigest);
                            Serializer.save(new SaveObject(Acid.this), savefilename);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }
                }

                @Override
                public Dialog show(Stage stage) {
                    return super.show(stage);

                }

                @Override
                public void cancel() {
                    super.cancel();
                }

                @Override
                public float getPrefHeight() {
                    return 50f;
                }
            };
            dialog.button("Yes", true);
            dialog.button("No", false);
            dialog.key(Input.Keys.ENTER, true);
            dialog.key(Input.Keys.ESCAPE, false);
            dialog.show(stage);
        }
    }

    void swapPattern(int curr, int next) {
        while (next >= sequencerDataArrayList.size()) {
            sequencerDataArrayList.add(new SequencerData());
            drumDataArrayList.add(new DrumData());
            knobsArrayList.add(KnobData.currentSequence);
        }
        if (Statics.recording) {
            if (sequencerDataArrayList.size() > curr) sequencerDataArrayList.remove(curr);
            if (drumDataArrayList.size() > curr) drumDataArrayList.remove(curr);
            if (knobsArrayList.size() > curr) knobsArrayList.remove(curr);
            sequencerDataArrayList.add(curr, new SequencerData());
            drumDataArrayList.add(curr, new DrumData());
            knobsArrayList.add(curr, KnobData.factory());
        }
        if (!Statics.free) {
            sequencerDataArrayList.get(next).refresh();
            drumDataArrayList.get(next).refresh();
            if (!KnobImpl.isTouched()) {
                KnobData.setcurrentSequence(knobsArrayList.get(next));
                KnobData.currentSequence.refresh();
            }

        }
    }

    @Override
    public void render() {
        Color c = Color.BLACK;
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        if (newZoom < ((OrthographicCamera) stage.getCamera()).zoom)
            ((OrthographicCamera) stage.getCamera()).zoom -= .005f;
        if (newZoom > ((OrthographicCamera) stage.getCamera()).zoom)
            ((OrthographicCamera) stage.getCamera()).zoom += .005f;
        stage.draw();
        if (KnobImpl.getControl(Statics.output.getSequencer().step) != null)
            for (int i = 0; i < 6; i++) {
                if (!KnobImpl.touched[i]) {
                    if (Statics.free) {

                    } else {
                        KnobImpl.setControls(KnobImpl.getControl(Statics.output.getSequencer().step)[i], i);
                    }
                } else {
                    KnobData.factory();
                    if (Statics.recording) {
                        KnobImpl.setControl(Statics.output.getSequencer().step, i);
                    }
                }

            }
//            KnobImpl.setControls(KnobImpl.getControl(Statics.output.getSequencer().step));

        if (Statics.output.getSequencer().step % 16 == 0 && prevStep % 16 == 1) {
            int old = songPosition;
            if (!Statics.free) {
                if (songPosition > minSongPosition) {
                    songPosition--;
                } else {
                    songPosition = maxSongPosition;
                }
            }
            swapPattern(old, songPosition);
        }

        if (Statics.output.getSequencer().step % 16 == 0 && prevStep % 16 == 15) {
            int old = songPosition;
            if (!Statics.free) songPosition++;
            if (songPosition > maxSongPosition) {
                songPosition = minSongPosition;
                if (Statics.export) {
                    stopSaving();
                }
            }
            swapPattern(old, songPosition);
        }
        prevStep = Statics.output.getSequencer().step;

        waveButton.setColor(Statics.waveSquare ? Color.WHITE : Color.RED);
        waveButton.setText(Statics.waveSquare ? " # " : " ^ ");

        sequencerDataArrayListLabel.setColor(ColorHelper.rainbowLight());
        sequencerDataArrayListLabel.setText(SequencerData.sequences.size() + "");

        drumDataArrayListLabel.setColor(ColorHelper.rainbowLight());
        drumDataArrayListLabel.setText(DrumData.sequences.size() + "");

        knobDataArrayListLabel.setColor(ColorHelper.rainbowLight());
        knobDataArrayListLabel.setText(KnobData.sequences.size() + "");

        BpmLabel.setText((int) Statics.output.getSequencer().bpm + "");


        BpmLabel.setColor(ColorHelper.rainbowLight());
        BpmLabel.setText((int) Statics.output.getSequencer().bpm + "");

        minSongLengthCaption.setColor(ColorHelper.rainbowLight());
        songLengthCaption.setColor(ColorHelper.rainbowLight());
        maxSongLengthCaption.setColor(ColorHelper.rainbowLight());

        maxSongLengthLabel.setColor(ColorHelper.rainbowLight());
        maxSongLengthLabel.setText(

                format(maxSongPosition + 1));
        minSongLengthLabel.setColor(ColorHelper.rainbowLight());
        minSongLengthLabel.setText((

                format(minSongPosition + 1)));
        songLengthLabel.setColor(ColorHelper.rainbowLight());
        songLengthLabel.setText(

                format(songPosition + 1));
        stepLabel.setColor(ColorHelper.rainbowLight());
        int step = Statics.output.getSequencer().step % 16 + 1;
        stepLabel.setText(step < 10 ? "0" + step : "" + step);
        stepCaption.setColor(ColorHelper.rainbowLight());

        rainbowFade += rainbowFadeDir;
        while (rainbowFade < 0f || rainbowFade > 1f) {
            rainbowFadeDir = -rainbowFadeDir;
            rainbowFade += rainbowFadeDir;
//            rainbowFadeDir+= (Math.random()-.5f)/10f;
        }
        if (drumsSelected && drumsSynthScale < 1f) {
            drumsSynthScale += .05f;
            drumMatrix.setScale(drumsSynthScale);
            sequenceMatrix.setScale(1.0f - drumsSynthScale);
        }
        if (!drumsSelected && drumsSynthScale > 0f) {
            drumsSynthScale -= .05f;
            drumMatrix.setScale(drumsSynthScale);
            sequenceMatrix.setScale(1.0f - drumsSynthScale);
        }


    }

    private void startSaving(FileHandle selected) {
        Statics.saveName = selected;

        if (Statics.free) {
            InputEvent event1 = new InputEvent();
            event1.setType(InputEvent.Type.touchDown);
            freeButton.fire(event1);

            InputEvent event2 = new InputEvent();
            event2.setType(InputEvent.Type.touchUp);
            freeButton.fire(event2);

        }

        if (Output.isPaused()) {
            InputEvent event1 = new InputEvent();
            event1.setType(InputEvent.Type.touchDown);
            pauseButton.fire(event1);

            InputEvent event2 = new InputEvent();
            event2.setType(InputEvent.Type.touchUp);
            pauseButton.fire(event2);

        }


        stage.getRoot().setTouchable(Touchable.disabled);
        songPosition = minSongPosition;
        Statics.output.getSequencer().tick = 0;
        Statics.output.getSequencer().step = 0;
        Statics.exportFile = Gdx.files.local("supersecrettempfile.pcm");

        Statics.exportFile.delete();
        Statics.export = true;
    }

    private void stopSaving() {
        Statics.export = false;
        stage.getRoot().setTouchable(Touchable.enabled);
        exportSongButton.setChecked(false);
        try {
            if (Statics.exportFile != null && Statics.saveName != null)
                rawToWave(Statics.exportFile, Statics.saveName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Statics.exportFile.delete();
    }

/*    private void convertWavFileToMp3File(String source, String target) throws IOException {
        String[] mp3Args = {"--preset", "standard",
                "-q", "0",
                "-m", "s", "-r", "-s", "44.1",
                source,
                target
        };
        (new Main()).run(mp3Args);
    }*/

//    public void copy(Path original, OutputStream out)
//            throws IOException {
//        File copied = original.toFile();
//        InputStream in = new BufferedInputStream(
//                new FileInputStream(copied));
//
//        byte[] buffer = new byte[1024];
//        int lengthRead;
//        while ((lengthRead = in.read(buffer)) > 0) {
//            out.write(buffer, 0, lengthRead);
//            out.flush();
//        }
//
//    }

    private void rawToWave(final FileHandle rawFile, final FileHandle waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(rawFile.read());
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(waveFile.write(false));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/


            int samplerate = 44100;
            int channels = 2;
            int bitspersample = 16;


            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
//            writeShort(output, (short) 3); // audio format (1 = PCM)
            writeShort(output, (short) channels); // number of channels
            writeInt(output, samplerate); // sample rate
            writeInt(output, samplerate * channels * bitspersample / 8); // byte rate   == SampleRate * NumChannels * BitsPerSample/8
            writeShort(output, (short) (channels * bitspersample / 8)); // block align == NumChannels * BitsPerSample/8
            writeShort(output, (short) bitspersample); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            waveFile.write(rawFile.read(), true);
//            IPFS ipfs=new IPFS(new MultiAddress("/dns6/ipfs.infura.io/tcp/5001/https"));
//            NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(waveFile.name(), waveFile.readBytes());
//            MerkleNode addResult = ipfs.add(file).get(0);
//            Boolean debug = true;
//            Peer litePeer = new Peer("/dns6/ipfs.infura.io/tcp/5001/https", debug, true);
//            litePeer.start();
//            String cid = litePeer.addFileSync(waveFile.readBytes());
//            System.out.println("cid:" + cid);
            output.flush();
            output.close();
            output = null;
            try {
                if (Gdx.files.isExternalStorageAvailable()) {
                    FileHandle ext = Gdx.files.external(waveFile.name());
                    ext.write(waveFile.read(), false);
                }
            } catch (Exception e) {
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileHandle flac = Gdx.files.local(waveFile.nameWithoutExtension() + ".flac");
                    try {
                        System.out.println("starting flac conversion");
                        EncodeWavToFlac.flac(waveFile.file(), flac.file());
                        System.out.println("finished flac conversion");
                        try {
                            if (Gdx.files.isExternalStorageAvailable()) {
                                FileHandle ext = Gdx.files.external(flac.name());
                                ext.write(flac.read(), false);
                            }
                        } catch (Exception e) {
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("error flac conversion");
                    }
                    System.out.println("starting wav upload");
                    try {
                        uploadFile(flac.readBytes(), flac.name());
//                        uploadFile(waveFile.readBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("finished wav upload");

                }
            }).start();


        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    public void uploadFile(byte[] data, String filename) throws IOException {
        String url = "https://ipfs.infura.io:5001/api/v0/add?pin=true";
        String charset = "UTF-8";
//        String param = "file";
//        File textFile = new File("/path/to/file.txt");
//        File binaryFile = new File("/path/to/file.bin");
        String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.

        URLConnection connection = new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true)
        ) {
            // Send normal param.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"file\"").append(CRLF);
            writer.append("Content-Type: audio/wav; charset=" + charset).append(CRLF);
            writer.append(CRLF).flush();
            output.write(data);
            output.flush();
            writer.append(CRLF).flush();

//            // Send text file.
//            writer.append("--" + boundary).append(CRLF);
//            writer.append("Content-Disposition: form-data; name=\"textFile\"; filename=\"" + textFile.getName() + "\"").append(CRLF);
//            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!
//            writer.append(CRLF).flush();
//            copy(textFile.toPath(), output);
//            output.flush(); // Important before continuing with writer!
//            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            // Send binary file.
//            writer.append("--" + boundary).append(CRLF);
//            writer.append("Content-Disposition: form-data").append(CRLF);
//            writer.append("Content-Type: multipart/form-data").append(CRLF);
//            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
//            writer.append(CRLF).flush();
//            copy(binaryFile.toPath(), output);
//            output.write(data);
//            output.flush(); // Important before continuing with writer!
//            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
        }

// Request is lazily fired whenever you need to obtain information about response.
        int responseCode = ((HttpURLConnection) connection).getResponseCode();
        System.out.println(responseCode); // Should be 200
        System.out.println();
        InputStream is = connection.getInputStream();
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (is, "UTF-8"))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        System.out.println();
        String response = textBuilder.toString();
        System.out.println(response);
        JsonParser jsonParser = new JsonParser();
        try {
            JsonObject root = jsonParser.parse(response).getAsJsonObject();
            if (root.has("Hash")) {
                String hash = root.get("Hash").getAsString();
                Gdx.net.openURI("https://ipfs.io/ipfs/" + hash + "?filename=" + filename);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }

    private String format(int i) {
        String s = i + "";
        if (i < 100) s = "0" + s;
        if (i < 10) s = "00" + i;
        return s;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().setScreenSize(width, height);
    }

    @Override
    public void pause() {
        Output.pause();
        try {
            String out = Serializer.toBase64(new SaveObject(Acid.this));
            Gdx.files.local("supersecrettempfile.txt").writeString(out, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resume() {
//		Output.running=true;
        Output.resume();

    }

    @Override
    public void dispose() {
        try {
            String out = Serializer.toBase64(new SaveObject(Acid.this));
            Gdx.files.local("supersecrettempfile.txt").writeString(out, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Output.running = false;
        Statics.output.dispose();

    }
    public void shiftStackLeft(Stack sequences) {
        Object rem = sequences.remove(0);
        sequences.add(sequences.size(),rem);
    }

    public void shiftStackRight(Stack sequences) {
        Object rem = (InstrumentData) sequences.remove(sequences.size()-1);
        sequences.add(0,rem);
    }
}
