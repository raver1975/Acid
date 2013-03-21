package com.klemstinegroup.sound;

import synth.BasslineSynthesizer;
import synth.Output;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class ShapeRenderingActor extends Actor {

	private int id = 0;

	public ShapeRenderingActor(final int id) {
		this.id = id;
		this.setWidth(40);
		this.setHeight(40);
		this.addListener(new InputListener() {
			private float distx;

			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				distx = x;
				return true;
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
			}

			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				// (ShapeRenderingActor.this).rotate((distx - x));
				int cc = (int) ((int) (((ShapeRenderingActor.this)
						.getRotation() * (127f / 360f) + 127) % 127) + (distx - x));

				switch (id) {
				case 0:
					// tune
					Sound.synth.controlChange(33, cc);
					break;
				case 1:
					//cutoff
					Sound.synth.controlChange(34, cc);
					break;

				case 2:
					//resonance
					Sound.synth.controlChange(35, cc);
					break;

				case 3:
					 Sound.synth.controlChange(36, cc);
					break;

				case 4:
					//decay
					 Sound.synth.controlChange(37, cc);
					break;

				case 5:
					//accent
					 Sound.synth.controlChange(38, cc);
					break;
				}

			}

		});
	}

	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.end();
		float rotation = 0f;
		switch (id) {
		case 0:
			rotation = (float) (((BasslineSynthesizer) Sound.output.getTrack(0)).tune * 360f);
			break;
		case 1:
			rotation = (float) (((BasslineSynthesizer) Sound.output.getTrack(0)).cutoff
					.getInstancedValue()*100f);
			System.out.println(rotation);
			break;

		case 2:
			rotation = (float) (((BasslineSynthesizer) Sound.output.getTrack(0)).resonance
					.getInstancedValue()*360f);
			break;

		case 3:
			rotation = (float) ((BasslineSynthesizer) Sound.output.getTrack(0)).envMod*360f;
			break;

		case 4:
			rotation=(float) ((BasslineSynthesizer) Sound.output.getTrack(0)).decay*360f;
			break;

		case 5:
			rotation=(float) ((BasslineSynthesizer) Sound.output.getTrack(0)).accent*360f;
			break;
		}
		rotation=(rotation+360)%360;
		System.out.println(rotation);
		Sound.renderer.setProjectionMatrix(batch.getProjectionMatrix());
		Sound.renderer.setTransformMatrix(batch.getTransformMatrix());
		Sound.renderer.translate(getX(), getY(), 0);

		Sound.renderer.begin(ShapeType.FilledCircle);
		Sound.renderer.setColor(Color.WHITE);
		Sound.renderer.filledCircle(0, 0, 10, 20);
		Sound.renderer.end();

		Sound.renderer.begin(ShapeType.Circle);
		Sound.renderer.setColor(Color.BLACK);
		Sound.renderer.circle(0, 0, 10, 20);
		Sound.renderer.end();

		Sound.renderer.begin(ShapeType.FilledCircle);
		Sound.renderer.setColor(Color.RED);
		for (float i = 2; i < 12; i += 2) {
			Sound.renderer.filledCircle(MathUtils.cosDeg(rotation) * i,
					MathUtils.sinDeg(rotation) * i, 2, 5);
		}
		Sound.renderer.end();

		batch.begin();
	}

}