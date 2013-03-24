package com.klemstinegroup.sound;

import synth.BasslineSynthesizer;
import synth.Output;
import synth.RhythmSynthesizer;
import synth.Synthesizer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
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

public class Sound implements ApplicationListener {
	public static final boolean grid = !false;
	public static Output output;
	BitmapFont font;
	public static boolean drums = true;
	public static ShapeRenderer renderer;
	static BasslineSynthesizer synth;
	static int[] maxValue = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	static int[] scale = new int[] { 0, 2, 4, 5, 7, 9, 11 };
	protected static boolean mutate;
	protected static Synthesizer drumzz;
	private Stage stage;
	KnobActor[] mya = new KnobActor[10];

	public Sound() {
	}

	@Override
	public void create() {
		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage();
		renderer = new ShapeRenderer();
		output = new Output();
		Gdx.input.setInputProcessor(stage);

		font = new BitmapFont(Gdx.app.getFiles().getFileHandle("data/font.fnt",
				FileType.Internal), false);
		output.start();
		synth = (BasslineSynthesizer) output.getTrack(0);
		drumzz= (RhythmSynthesizer) output.getTrack(1);
		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		TextureRegionActor my = new TextureRegionActor();
		table.addActor(my);
		final Touchpad touch1 = new Touchpad(0, skin);
		touch1.setBounds(15, 15, 100, 100);
		touch1.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				synth.controlChange(35, (int) (touch1.getKnobX()));
				synth.controlChange(34, (int) (touch1.getKnobY()));

			}
		});
		touch1.setPosition(20, 190);
		table.addActor(touch1);

		final Touchpad touch2 = new Touchpad(0, skin);
		touch2.setBounds(15, 15, 100, 100);
		touch2.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				synth.controlChange(36, (int) (touch2.getKnobX()));
				synth.controlChange(37, (int) (touch2.getKnobY()));

			}
		});
		touch2.setPosition(20, 300);
		table.addActor(touch2);

		
		table.setPosition(Gdx.graphics.getWidth() / 2-280,
				Gdx.graphics.getHeight() / 2-280);
		((OrthographicCamera)stage.getCamera()).zoom-=.30f;

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

		int hj = 120;
		int gh = 120;
		mya[0].setPosition(hj, gh);
		mya[1].setPosition(hj += 56, gh);
		mya[2].setPosition(hj += 56, gh);
		mya[3].setPosition(hj += 56, gh);
		mya[4].setPosition(hj += 56, gh);
		mya[5].setPosition(hj += 56, gh);

		MatrixActor matrixa = new MatrixActor(0);
		table.addActor(matrixa);
		matrixa.setPosition(130, 180);
		
		TextButton tb4=new TextButton("WaveForm",skin);
		table.addActor(tb4);
		tb4.setPosition(7.5f, 130);
		tb4.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Sound.output.getSequencer().bass.switchWaveform();
				return true;
			}
		});
		TextButton tb1=new TextButton("Mutate",skin);
		table.addActor(tb1);
		tb1.setPosition(470, 260);
		tb1.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Sound.mutate=!Sound.mutate;
				return true;
			}
		});
		
		TextButton tb3=new TextButton("Random",skin);
		table.addActor(tb3);
		tb3.setPosition(470, 300);
		tb3.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				output.getSequencer().randomize();
				output.getSequencer().setBpm(output.getSequencer().bpm);
				return true;
			}
		});

		
		TextButton tb2=new TextButton("Drums",skin);
		table.addActor(tb2);
		tb2.setPosition(470,220);
		tb2.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Sound.drums=!Sound.drums;
				Sound.output.tracks = new Synthesizer[Sound.drums?2:1];
				Sound.output.tracks[0] = Sound.synth;
				if(Sound.drums)Sound.output.tracks[1] = Sound.drumzz;
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
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
//		stage.setViewport(width, height, true);
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
