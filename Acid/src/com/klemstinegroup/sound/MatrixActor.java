package com.klemstinegroup.sound;

import java.util.Arrays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class MatrixActor extends Actor {

	private int id = 0;
	public boolean fl;
	private float y2;
	private float x2;

	public MatrixActor(final int id) {
		this.id = id;
		this.setWidth(320);
		this.setHeight(280);
		this.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				int x1 = (int) (x / ((getWidth() / 16)));
				int y1 = (int) (y / (getHeight() / (Statics.drumdisplay ? 7 : 31))) - (Statics.drumdisplay ? 0 : 16);
				x2 = x1;
				y2 = y1;

				ttouch(x1, y1);
				return true;
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
			}

			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				int x1 = (int) (x / ((getWidth() / 16)));
				int y1 = (int) (y / (getHeight() / (Statics.drumdisplay ? 7 : 31))) - (Statics.drumdisplay ? 0 : 16);
				if (x1 != x2 || y1 != y2) {
					ttouch(x1, y1);
					x2 = x1;
					y2 = y1;
				}
			}
		});
	}

	protected void ttouch(int x1, int y1) {
		if (!Statics.drumdisplay) {
			if (x1 < 16 && x1 > -1) {

				Statics.output.getSequencer().bassline.note[x1] = (byte) y1;
				// int ran = (int) (Math.random() * 3.f);
				// int ran=0;
				if (Statics.output.getSequencer().bassline.pause[x1]) {
					Statics.output.getSequencer().bassline.pause[x1] = false;
					// } else if
					// (Sound.output.getSequencer().bassline.accent[x1]) {
					// Sound.output.getSequencer().bassline.accent[x1] = false;
				} else if (!Statics.output.getSequencer().bassline.slide[x1]) {
					Statics.output.getSequencer().bassline.slide[x1] = true;
				} else {
					Statics.output.getSequencer().bassline.pause[x1] = true;
					Statics.output.getSequencer().bassline.accent[x1] = !Statics.output
							.getSequencer().bassline.accent[x1];
					Statics.output.getSequencer().bassline.slide[x1] = false;
				}
			}
		} else {
			if (x1 < 16 && x1 > -1 && y1 >= 0 && y1 < 7) {
				if (Statics.output.getSequencer().rhythm[y1][x1] >0) {
					Statics.output.getSequencer().rhythm[y1][x1] = 0;
				} else
					Statics.output.getSequencer().rhythm[y1][x1] = 127;
			}
		}
	}

	public void draw(SpriteBatch batch, float parentAlpha) {
		if (Statics.mutate && Math.random() < .01) {
			ttouch((int) (MathUtils.random() * 16),
					(int) (MathUtils.random() * 31) - 16);
		}
		batch.end();
		Statics.renderer.setProjectionMatrix(batch.getProjectionMatrix());
		Statics.renderer.setTransformMatrix(batch.getTransformMatrix());
		Statics.renderer.translate(getX(), getY(), 0);

		int skipx = (int) (getWidth() / 16);
		int skipy = (int) (getHeight() / (Statics.drumdisplay ? 7 : 31));
		// grid
		if (Statics.grid) {
			Statics.renderer.begin(ShapeType.Line);
			Statics.renderer.setColor(Color.GRAY);
			for (int r = 0; r < 16; r += 4) {
				Statics.renderer.line(r * skipx, 0, r * skipx, getHeight());
			}
			for (int r = 0; r < (Statics.drumdisplay ? 8 : 32); r++) {
				Statics.renderer.line(0, r * skipy, getWidth(), r * skipy);
			}
			Statics.renderer.end();
		}

		Statics.renderer.begin(ShapeType.Line);
		Statics.renderer.setColor(Color.BLUE);
		Statics.renderer.line(
				(Statics.output.getSequencer().step) % 16 * skipx, 0,
				(Statics.output.getSequencer().step) % 16 * skipx, getHeight());
		Statics.renderer.end();

		Statics.renderer.begin(ShapeType.Rectangle);
		Statics.renderer.setColor(Color.RED);
		Statics.renderer.rect(0, 0, this.getWidth(), this.getHeight());
		Statics.renderer.end();
		if (!Statics.drumdisplay) {
			Statics.renderer.begin(ShapeType.FilledRectangle);
			Statics.renderer.setColor(Color.YELLOW);

			for (int r = 0; r < 16; r++) {
				if (Statics.output.getSequencer().bassline.pause[r])
					continue;
				int skipd = 3;
				if (Statics.output.getSequencer().bassline.slide[r])
					skipd = 0;
				// if (r > 0 && Sound.output.getSequencer().bassline.slide[r -
				// 1])
				// skipd = 0;
				Statics.renderer.filledRect(r * skipx + skipd,
						(Statics.output.getSequencer().bassline.note[r] + 16)
								* skipy, skipx - skipd - skipd, skipy);
			}
			Statics.renderer.end();

			Statics.renderer.begin(ShapeType.FilledRectangle);
			Statics.renderer.setColor(Color.RED);
			for (int r = 0; r < 16; r++) {
				if (Statics.output.getSequencer().bassline.pause[r])
					continue;
				int skipd = 3;
				if (Statics.output.getSequencer().bassline.slide[r])
					skipd = 0;
				// if (r > 0 && Sound.output.getSequencer().bassline.slide[r -
				// 1])
				// skipd = 0;

				if (Statics.output.getSequencer().bassline.accent[r])
					Statics.renderer
							.filledRect(
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
			Statics.renderer.end();
		} else {
			Statics.renderer.begin(ShapeType.FilledRectangle);
			Statics.renderer.setColor(Color.YELLOW);
			for (int r = 0; r < Statics.output.getSequencer().rhythm.length; r++) {
				Statics.renderer.setColor(Color.YELLOW);
				for (int r1 = 0; r1 < 16; r1++) {
					if (Statics.output.getSequencer().rhythm[r][r1] > 0) {
						Statics.renderer.filledRect(r1 * skipx+2, (r)
								* skipy+2, skipx-4, skipy-4);
					}
				}
			}
			Statics.renderer.end();
		}
		batch.begin();
	}

}