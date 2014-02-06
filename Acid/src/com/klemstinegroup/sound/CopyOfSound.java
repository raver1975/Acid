package com.klemstinegroup.sound;

import java.util.ArrayList;

import synth.BasslineSynthesizer;
import synth.Output;
import synth.Synthesizer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.math.Vector2;

public class CopyOfSound implements ApplicationListener, InputProcessor {
	public static Output output;
	BitmapFont font;
	SpriteBatch batch;
	static boolean mouseDown = false;
	public static boolean drums = true;
	protected ImmediateModeRenderer10 renderer;
	static BasslineSynthesizer synth;
	static int[] maxValue = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	static int[] scale = new int[] { 0, 2, 4, 5, 7, 9, 11 };
	Synthesizer drumsss;
	private ArrayList<Point> al = new ArrayList<Point>();
	private float col;

	public CopyOfSound() {
	}

	@Override
	public void create() {
		renderer = new ImmediateModeRenderer10();
		output = new Output();
		drumsss = output.tracks[1];
		Gdx.input.setInputProcessor(this);
		font = new BitmapFont(Gdx.app.getFiles().getFileHandle("data/font.fnt",
				FileType.Internal), false);
		batch = new SpriteBatch();
		batch.enableBlending();
		batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		output.start();
		synth = (BasslineSynthesizer) output.getTrack(0);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		int y = 1;

		int he = Gdx.graphics.getHeight() - 140;
		for (int r = 3; r < 11; r++) {
			float hue = (r - 3f) / 8f + .1f;
			Color c = new Color(
					output.getSequencer().bassline.isPaused(r - 3) ? hue : 0,
					output.getSequencer().bassline.isPaused(r - 3) ? hue : 0,
					(hue), 1f);
			// drawSolidPolygon(
			// (r - 3) * (Gdx.graphics.getWidth() / 8),
			// he,
			// (r - 3) * (Gdx.graphics.getWidth() / 8)
			// + (Gdx.graphics.getWidth() / 8),
			// (int) (he
			// * (((float) MindListener.channels[r]) / ((float) maxValue[r])) +
			// he),
			// c);
		}

		int y1 = (int) (((BasslineSynthesizer) output.getTrack(0)).resonance
				.getInstancedValue() * Gdx.graphics.getHeight()) / 4;
		int x = (int) ((((BasslineSynthesizer) output.getTrack(0)).cutoff
				.getInstancedValue() - 200) * Gdx.graphics.getWidth()) / 4800;
		for (Point p : al) {
			drawSolidPolygon(p.x - (int)(col*30), p.y - (int)(col*30), p.x+ (int)(col*15), p.y+ (int)(col*15), new Color(1,
					(float) (1.0 - col), (float) (1.0 - col), 1f));
			col += .0211111f;
			if (col>1)col=-1f;
		}
		
		drawSolidPolygon(0, (y),
				(int) (((BasslineSynthesizer) output.getTrack(0)).resonance
						.getInstancedValue() * Gdx.graphics.getWidth()),
				y + 20, new Color(.2f, 0, .8f, 1));
		y += 20;
		drawSolidPolygon(
				0,
				(y),
				(int) ((((BasslineSynthesizer) output.getTrack(0)).cutoff
						.getInstancedValue() - 200) * Gdx.graphics.getWidth()) / 4800,
				y + 20, new Color(.8f, 0, .8f, 1));
		y += 20;
		drawSolidPolygon(0, (y), (int) ((((BasslineSynthesizer) output
				.getTrack(0)).envMod * Gdx.graphics.getWidth()) - 0), y + 20,
				new Color(.2f, 0f, 0f, 1f));
		y += 20;
		drawSolidPolygon(0, (y), (int) (((((BasslineSynthesizer) output
				.getTrack(0)).tune - .5f) * Gdx.graphics.getWidth()) / 1.5f),
				y + 20, new Color(.3f, 0f, 0f, 1f));
		y += 20;
		drawSolidPolygon(0, (y), (int) (((20 - ((BasslineSynthesizer) output
				.getTrack(0)).decay)) * Gdx.graphics.getWidth()) / 20, y + 20,
				new Color(.4f, 0f, 0f, 1f));
		y += 20;
		drawSolidPolygon(0, (y), (int) ((((BasslineSynthesizer) output
				.getTrack(0)).accent * Gdx.graphics.getWidth()) - 0) / 1,
				y + 20, new Color(.4f, 0f, 0f, 1f));
		y += 20;
		drawSolidPolygon(0, (y), (int) ((((BasslineSynthesizer) output
				.getTrack(0)).volume * Gdx.graphics.getWidth()) - 0) / 1,
				y + 20, new Color(.5f, 0f, 0f, 1f));
		y += 20;
		drawSolidPolygon(0, (y), (int) (CopyOfSound.output.getSequencer().bpm
				* Gdx.graphics.getWidth() / 300), y + 20, new Color(.6f, 0f,
				0f, 1f));
		y += 20;
		// if (MindListener.channels[0] == 0)
		drawSolidPolygon(0, y, (200) * Gdx.graphics.getWidth() / 200, y + 20,
				new Color(0f, .7f, 0f, 1));



		//
		// else
		// // drawSolidPolygon(0, y, (200 - MindListener.channels[0])
		// // * Gdx.graphics.getWidth() / 200, y + 20, new Color((200f -
		// // MindListener.channels[0])/200f,
		// // (200f - MindListener.channels[0])/200f, 0f, 1));
		// drawSolidPolygon(0, y, Gdx.graphics.getWidth(), y + 20, new Color(
		// (200f - MindListener.channels[0]) / 200f,
		// (200f - MindListener.channels[0]) / 200f, 0f, 1));
		if (drums)
			drawSolidPolygon(10, Gdx.graphics.getHeight() - 20, 20,
					Gdx.graphics.getHeight() - 10, new Color(.8f, 0f, 0f, 1f));
		else
			drawSolidPolygon(10, Gdx.graphics.getHeight() - 20, 20,
					Gdx.graphics.getHeight() - 10, new Color(.2f, 0f, 0f, 1f));
		if (!output.isPaused())
			drawSolidPolygon(Gdx.graphics.getWidth() - 20,
					Gdx.graphics.getHeight() - 20,
					Gdx.graphics.getWidth() - 10,
					Gdx.graphics.getHeight() - 10, new Color(0f, .8f, 0f, 1f));
		else
			drawSolidPolygon(Gdx.graphics.getWidth() - 20,
					Gdx.graphics.getHeight() - 20,
					Gdx.graphics.getWidth() - 10,
					Gdx.graphics.getHeight() - 10, new Color(0f, .2f, 0f, 1f));
		batch.begin();
		font.draw(
				batch,
				"Resonance "
						+ value(Double.toString(((BasslineSynthesizer) output
								.getTrack(0)).resonance.getInstancedValue() * 100)),
				5, 20);
		font.draw(
				batch,
				"Cutoff "
						+ value(Double.toString((((BasslineSynthesizer) output
								.getTrack(0)).cutoff.getInstancedValue() - 200) / 48f)),
				5, 40);
		font.draw(
				batch,
				"Env "
						+ value(Double.toString(((BasslineSynthesizer) output
								.getTrack(0)).envMod * 100)), 5, 60);
		// font.draw(batch,
		// "Tune "+Double.toString((((BasslineSynthesizer)output.getTrack(0)).tune-.5f)/1.5*100),5,80);
		font.draw(
				batch,
				"Tune "
						+ value(Double.toString((((BasslineSynthesizer) output
								.getTrack(0)).tune - .5f) * 100f / 1.5f)), 5,
				80);
		font.draw(
				batch,
				"Decay "
						+ value(Double
								.toString(100 - ((BasslineSynthesizer) output
										.getTrack(0)).decay * 100 / 20)), 5,
				100);
		font.draw(
				batch,
				"Accent "
						+ value(Double.toString(((BasslineSynthesizer) output
								.getTrack(0)).accent * 100)), 5, 120);
		font.draw(
				batch,
				"Volume "
						+ value(Double.toString(((BasslineSynthesizer) output
								.getTrack(0)).volume * 100)), 5, 140);
		font.draw(
				batch,
				"Tempo "
						+ value(Double.toString((CopyOfSound.output.getSequencer().bpm)))
						+ "", 5, 160);
		// font.draw(batch,
		// "Connection " + ((200 - MindListener.channels[0]) / 2), 5, 180);
		batch.end();
	}

	private String value(String string) {
		while (string.contains(".")) {
			string = string.substring(0, string.indexOf('.'));
		}
		if (string.length() > 3)
			return string.substring(0, 3);
		else
			return string;
	}

	private void drawSolidPolygon(int x0, int y0, int x1, int y1, Color color) {
		Vector2[] vertices = new Vector2[] { new Vector2(x0, y0),
				new Vector2(x1, y0), new Vector2(x0, y1), new Vector2(x1, y1) };
		renderer.begin(GL10.GL_TRIANGLE_STRIP);
		for (int i = 0; i < vertices.length; i++) {
			Vector2 v = vertices[i];
			renderer.color(color.r, color.g, color.b, color.a);
			renderer.vertex(v.x, v.y, 0);
		}
		renderer.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		output.running = false;
	}

	@Override
	public void dispose() {
		output.running = false;
		output.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		mouseDown = true;
		// System.out.println(x+","+y);
		// output.getSequencer().randomize();
		// output.getSequencer().setBpm(output.getSequencer().bpm*(Output.SAMPLE_RATE/44100));
		int gx = (int) (127 * x / Gdx.graphics.getWidth());
		// System.out.println(x+" "+y);
		if (y > Gdx.graphics.getHeight() - 15) {
			synth.controlChange(35, gx);
		} else if (y > Gdx.graphics.getHeight() - 45) {
			synth.controlChange(34, gx);
		} else if (y > Gdx.graphics.getHeight() - 65) {
			synth.controlChange(36, gx);
		} else if (y > Gdx.graphics.getHeight() - 85) {
			synth.controlChange(33, gx);
		} else if (y > Gdx.graphics.getHeight() - 105) {
			synth.controlChange(37, gx);
		} else if (y > Gdx.graphics.getHeight() - 125) {
			synth.controlChange(38, gx);
		} else if (y > Gdx.graphics.getHeight() - 145) {
			synth.controlChange(39, gx);
		} else if (y > Gdx.graphics.getHeight() - 165) {
			CopyOfSound.output.getSequencer().setBpm(
					300 * x / Gdx.graphics.getWidth());
		} else if (y > Gdx.graphics.getHeight() - 185) {
			output.getSequencer().randomize();
			output.getSequencer().setBpm(output.getSequencer().bpm);
		} else if (y < 30 && x > Gdx.graphics.getWidth() - 30) {
			// output.getSequencer().randomize();
			// output.getSequencer().setBpm(
			// output.getSequencer().bpm
			// * (Output.SAMPLE_RATE / 44100));
			if (!output.isPaused())
				output.pause();
			else
				output.resume();
		} else if (y < 30 && x < 30) {
			if (!drums) {
				drums = !false;
				output.tracks = new Synthesizer[] {
						(Synthesizer) output.tracks[0], drumsss };

			} else {
				drums = !true;
				drumsss = output.tracks[1];
				output.tracks = new Synthesizer[] { (Synthesizer) output.tracks[0] };
			}
		}

		// if (y > Gdx.graphics.getHeight() - 205) {
		else {
			int xx = (int) (8f * (float) x / (float) Gdx.graphics.getWidth());
			// touchedBar(xx);
			synth.controlChange(35, (int) ((y - 20) * 1.35));
			synth.controlChange(34, (x - 30) / 3);
			al.add(new Point(Gdx.input.getX(), Gdx.graphics.getHeight()
					- Gdx.input.getY()));
			if (al.size() > 30)
				al.remove(0);
			// synth.controlChange(34, gx);
		}

		// int gy=(int)(127*y /Gdx.graphics.getHeight());
		// synth.controlChange(34, gx);
		// synth.controlChange(35, gy);
		return false;
	}

	//
	// private void touchedBar(int xx) {
	// System.out.println(xx);
	// output.getSequencer().bassline.setPaused(xx,
	// !output.getSequencer().bassline.isPaused(xx));
	// }

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		mouseDown = false;
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		touchDown(x, y, pointer, 0);
		return false;
	}

	// @Override
	// public boolean touchMoved(int x, int y) {
	// return false;
	// }

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
}
