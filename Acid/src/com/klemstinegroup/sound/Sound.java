package com.klemstinegroup.sound;

import synth.BasslineSynthesizer;
import synth.Output;
import synth.Synthesizer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Sound implements ApplicationListener {
	public static Output output;
	BitmapFont font;
	public static boolean drums = true;
	public static ShapeRenderer renderer;
	static BasslineSynthesizer synth;
	static int[] maxValue = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	static int[] scale = new int[] { 0, 2, 4, 5, 7, 9, 11 };
	Synthesizer drumsss;
	private Stage stage;
	ShapeRenderingActor[] mya = new ShapeRenderingActor[10];

	public Sound() {
	}

	@Override
	public void create() {
		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage();
		renderer = new ShapeRenderer();
		output = new Output();
		drumsss = Output.tracks[1];
		Gdx.input.setInputProcessor(stage);

		font = new BitmapFont(Gdx.app.getFiles().getFileHandle("data/font.fnt",
				FileType.Internal), false);
		output.start();
		synth = (BasslineSynthesizer) output.getTrack(0);
		// Table table = new Table();
		// table.setFillParent(true);
		// stage.addActor(table);
		TextureRegionActor my = new TextureRegionActor();
		stage.addActor(my);
		final Touchpad touch1 = new Touchpad(20, skin);
		touch1.setBounds(15, 15, 100, 100);
		touch1.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				synth.controlChange(35, (int) (touch1.getKnobX()));
				synth.controlChange(34, (int) (touch1.getKnobY()));

			}
		});
		touch1.setPosition(10, 200);
		stage.addActor(touch1);
		
		final Touchpad touch2 = new Touchpad(20, skin);
		touch2.setBounds(15, 15, 100, 100);
		touch2.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				synth.controlChange(36, (int) (touch2.getKnobX()));
				synth.controlChange(37, (int) (touch2.getKnobY()));

			}
		});
		touch2.setPosition(230, 200);
		stage.addActor(touch2);

		final Touchpad touch3 = new Touchpad(20, skin);
		touch3.setBounds(15, 15, 100, 100);
		touch3.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				 output.getSequencer().randomize();
				 output.getSequencer().setBpm(output.getSequencer().bpm);
			}
		});
		touch3.setPosition(120, 200);
		stage.addActor(touch3);

		
		mya[0] = new ShapeRenderingActor(0);
		stage.addActor(mya[0]);
		mya[1] = new ShapeRenderingActor(1);
		stage.addActor(mya[1]);
		mya[2] = new ShapeRenderingActor(2);
		stage.addActor(mya[2]);
		mya[3] = new ShapeRenderingActor(3);
		stage.addActor(mya[3]);
		mya[4] = new ShapeRenderingActor(4);
		stage.addActor(mya[4]);
		mya[5] = new ShapeRenderingActor(5);
		stage.addActor(mya[5]);

		int hj = 120;
		int gh = 120;
		mya[0].setPosition(hj, gh);
		mya[1].setPosition(hj += 56, gh);
		mya[2].setPosition(hj += 56, gh);
		mya[3].setPosition(hj += 56, gh);
		mya[4].setPosition(hj += 56, gh);
		mya[5].setPosition(hj += 56, gh);

		// stage.addListener(new InputListener() {
		// public boolean touchDown(InputEvent event, float x, float y,
		// int pointer, int button) {
		// return super.touchDown(event, x, y, pointer, button);
		// }
		//
		// public void touchUp(InputEvent event, float x, float y,
		// int pointer, int button) {
		// super.touchUp(event, x, y, pointer, button);
		// }
		// });

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		// mya.rotate(10);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
	}

	@Override
	public void pause() {
		Output.running = false;
	}

	@Override
	public void dispose() {
		Output.running = false;
		output.dispose();
	}
}
