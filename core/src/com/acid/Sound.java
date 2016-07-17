package com.acid;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import synth.BasslineSynthesizer;
import synth.Output;
import synth.RhythmSynthesizer;

public class Sound implements ApplicationListener {
	BitmapFont font;
	private Stage stage;
	LightActor la3 = null;

	public Sound() {
	}

	@Override
	public void create() {

		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage();

		Statics.renderer = new ShapeRenderer();
		Statics.output = new Output();
		Statics.output.getSequencer().setBpm(120);
		Statics.output.getSequencer().randomize();
		Statics.drumdisplay = false;
		Statics.output.getSequencer().randomize();

		Gdx.input.setInputProcessor(stage);

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
		TextureRegionActor my = new TextureRegionActor();
		table.addActor(my);
		final Touchpad touch1 = new Touchpad(0,skin);
		touch1.setBounds(15, 15, 100, 100);
		touch1.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Statics.synth.controlChange(35, (int) (touch1.getKnobX()));
				Statics.synth.controlChange(34, (int) (touch1.getKnobY()));

			}
		});
		touch1.setPosition(20, 190);
		table.addActor(touch1);

		final Touchpad touch2 = new Touchpad(0, skin);
		touch2.setBounds(15, 15, 100, 100);
		touch2.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Statics.synth.controlChange(36, (int) (touch2.getKnobX()));
				Statics.synth.controlChange(37, (int) (touch2.getKnobY()));

			}
		});
		touch2.setPosition(20, 300);
		table.addActor(touch2);

		table.setPosition(Gdx.graphics.getWidth() / 2 - 280,
				Gdx.graphics.getHeight() / 2 - 290);
		((OrthographicCamera) stage.getCamera()).zoom -= .30f;
		KnobActor[] mya = new KnobActor[10];
		mya[0] = new KnobActor(0);
		table.addActor(mya[0]);
		mya[1] = new KnobActor(1);
		table.addActor(mya[1]);
		mya[2] = new KnobActor(2);
		table.addActor(mya[2]);
		mya[3] = new KnobActor(3);
		table.addActor(mya[3]);
		mya[4] = new KnobActor(4);
		table.addActor(mya[4]);
		mya[5] = new KnobActor(5);
		table.addActor(mya[5]);
		mya[6] = new KnobActor(6);
		table.addActor(mya[6]);

		int hj = 120;
		int gh = 120;
		mya[0].setPosition(hj, gh);
		mya[1].setPosition(hj += 56, gh);
		mya[2].setPosition(hj += 56, gh);
		mya[3].setPosition(hj += 56, gh);
		mya[4].setPosition(hj += 56, gh);
		mya[5].setPosition(hj += 56, gh);
		mya[6].setPosition(hj += 56, gh);

		MatrixActor matrixa = new MatrixActor(0);
		table.addActor(matrixa);
		matrixa.setPosition(130, 178);

		TextButton tb4 = new TextButton("WaveForm", skin);
		table.addActor(tb4);
		tb4.setPosition(7.5f, 130);
		tb4.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Statics.output.getSequencer().bass.switchWaveform();
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
				// Statics.output.getSequencer().setBpm(Statics.output.getSequencer().bpm);
				return true;
			}
		});

		TextButton tb5 = new TextButton("Synth", skin);
		table.addActor(tb5);
		tb5.setPosition(470, 180);
		tb5.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				// Statics.zzzynth = !Statics.zzzynth;
				Statics.drumdisplay = false;
				return true;
			}
		});

		// TextButton tb6 = new TextButton("Instrument", skin);
		// table.addActor(tb6);
		// tb6.setPosition(470, 380);
		// tb6.addListener(new InputListener() {
		// public boolean touchDown(InputEvent event, float x, float y,
		// int pointer, int button) {
		// Statics.drumdisplay = !Statics.drumdisplay;
		// return true;
		// }
		// });

		TextButton tb7 = new TextButton("Clear", skin);
		table.addActor(tb7);
		tb7.setPosition(470, 340);
		tb7.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (Statics.drumdisplay) {
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
				// Statics.drumzzz = !Statics.drumzzz;
				Statics.drumdisplay = true;
				return true;
			}
		});

		la3 = new LightActor(5, Color.RED, false);
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
		
		
		TextButton zi = new TextButton("Zoom +", skin);
		table.addActor(zi);
		zi.setPosition(470, 430);
		zi.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				((OrthographicCamera) stage.getCamera()).zoom -= .05f;
				return true;
			}
		});
		
		
		TextButton zo = new TextButton("Zoom - ", skin);
		table.addActor(zo);
		zo.setPosition(470, 400);
		zo.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				((OrthographicCamera) stage.getCamera()).zoom += .05f;
				return true;
			}
		});
		
//		LightActor la4 = new LightActor(10, Color.RED, true);
//		table.addActor(la4);
//		la4.setPosition(470, 430);
//		la4.addListener(new InputListener() {
//			public boolean touchDown(InputEvent event, float x, float y,
//					int pointer, int button) {
//				((OrthographicCamera) stage.getCamera()).zoom -= .05f;
//				return true;
//			}
//		});
//		
//		LightActor la5 = new LightActor(10, Color.RED, true);
//		table.addActor(la5);
//		la5.setPosition(470, 400);
//		la5.addListener(new InputListener() {
//			public boolean touchDown(InputEvent event, float x, float y,
//					int pointer, int button) {
//				((OrthographicCamera) stage.getCamera()).zoom += .05f;
//				return true;
//			}
//		});



		final LightActor la2 = new LightActor(5, Color.RED, true);
		table.addActor(la2);
		la2.setPosition(455, 188);
		la2.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				la2.on = !la2.on;
				Statics.zzzynth = la2.on;
				return true;
			}
		});

		final LightActor la1 = new LightActor(5, Color.RED, true);
		table.addActor(la1);
		la1.setPosition(455, 228);
		la1.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				la1.on = !la1.on;
				Statics.drumzzz = la1.on;
				return true;
			}
		});

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		// mya.rotate(10);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stage.getBatch().begin();
		font.setColor(Color.BLACK);
		font.draw(stage.getBatch(), "bpm", 665, 212);
		font.draw(stage.getBatch(),
				(int) Statics.output.getSequencer().bpm + "", 669, 170);
		stage.getBatch().end();
	}

	@Override
	public void resize(int width, int height) {
		// stage.setViewport(width, height, true);
	}

	@Override
	public void pause() {
		Output.running = false;
	}

	@Override
	public void dispose() {
		Output.running = false;
		Statics.output.dispose();
	}
}
