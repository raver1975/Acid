package com.acid;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

	public KnobActor(String name,final int id) {
		font = new BitmapFont(Gdx.app.getFiles().getFileHandle("data/font.fnt",
				Files.FileType.Internal), false);
		font.getData().setScale(.75f);
		this.id = id;
		this.name=name;
		this.setWidth(60);
		this.setHeight(60);
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
				int cc = (int) ((int) (((KnobActor.this)
						.getRotation() * (127f / 360f) + 127+127/2) % 127) - (distx - x));

				switch (id) {
				case 0:
					// tune
					Statics.synth.controlChange(33, cc);
					break;
				case 1:
					//cutoff
					Statics.synth.controlChange(34, cc);
					break;

				case 2:
					//resonance
					Statics.synth.controlChange(35, cc);
					break;

				case 3:
					 Statics.synth.controlChange(36, cc);
					break;

				case 4:
					//decay
					 Statics.synth.controlChange(37, cc);
					break;

				case 5:
					//accent
					 Statics.synth.controlChange(38, cc);
					break;
				case 6:
					//accent
					 Statics.output.getSequencer().setBpm(cc+100);
					break;
				}


			}

		});
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		font.setColor(Color.WHITE);

		font.draw(batch,name,this.getX(),this.getY()+this.getHeight()-10);

		batch.end();
		float rotation = 0f;
		switch (id) {
		case 0:
			rotation = (int) (((((BasslineSynthesizer) Statics.output
					.getTrack(0)).tune - .5f) * 400.0) / 1.5f);

			break;
		case 1:
			rotation =(float) ((((BasslineSynthesizer) Statics.output.getTrack(0)).cutoff
					.getValue() - 1200) * 5.0f) /50f;
			break;

		case 2:
			rotation = (float) (((BasslineSynthesizer) Statics.output.getTrack(0)).resonance
					.getValue()*500f)-100f;
			break;

		case 3:
			rotation =  (int) ((((BasslineSynthesizer) Statics.output
					.getTrack(0)).envMod * 500) -100);
			break;

		case 4:
			rotation= (float) ((((20 - ((BasslineSynthesizer) Statics.output
					.getTrack(0)).decay)) * Gdx.graphics.getWidth()) / 20.0f)-100f;
			break;

		case 5:
			rotation=(float) ((BasslineSynthesizer) Statics.output.getTrack(0)).accent*360f;
			break;
		case 6:
			//accent
			rotation=(float)  Statics.output.getSequencer().bpm;
			break;
		}
//		rotation=(rotation+360)%360;
//		System.out.println(rotation);
		Statics.renderer.setProjectionMatrix(batch.getProjectionMatrix());
		Statics.renderer.setTransformMatrix(batch.getTransformMatrix());
		Statics.renderer.translate(getX(), getY(), 0);

		Statics.renderer.begin(ShapeType.Filled);
		Statics.renderer.setColor(Color.WHITE);
		Statics.renderer.circle(20, 20, 10, 20);
		Statics.renderer.end();

		Statics.renderer.begin(ShapeType.Line);
		Statics.renderer.setColor(Color.BLACK);
		Statics.renderer.circle(20, 20, 10, 20);
		Statics.renderer.end();

		Statics.renderer.begin(ShapeType.Filled);
		Statics.renderer.setColor(Color.RED);
		for (float i = 2; i < 12; i += 2) {
			Statics.renderer.circle(20+MathUtils.cosDeg(rotation) *- i,
					20+MathUtils.sinDeg(rotation) * i, 2, 5);
		}
		Statics.renderer.end();

		batch.begin();

	}

}