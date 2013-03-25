package com.klemstinegroup.sound;

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
				int y1 = (int) (y / (getHeight() / 31)) - 16;
				x2=x1;y2=y1;

				ttouch(x1, y1);
				return true;
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
			}

			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				int x1 = (int) (x / ((getWidth() / 16)));
				int y1 = (int) (y / (getHeight() / 31)) - 16;
				if (x1!=x2||y1!=y2){
					ttouch(x1,y1);
					x2=x1;y2=y1;
				}
			}
		});
	}

	protected void ttouch(int x1, int y1) {
		if (x1 < 16 && x1 > -1) {
			Sound.output.getSequencer().bassline.note[x1] = (byte) y1;
			// int ran = (int) (Math.random() * 3.f);
			// int ran=0;
			if (Sound.output.getSequencer().bassline.pause[x1]) {
				Sound.output.getSequencer().bassline.pause[x1] = false;
//			} else if (Sound.output.getSequencer().bassline.accent[x1]) {
//				Sound.output.getSequencer().bassline.accent[x1] = false;
			} else if (!Sound.output.getSequencer().bassline.slide[x1]) {
				Sound.output.getSequencer().bassline.slide[x1] = true;
			}
			else{
				Sound.output.getSequencer().bassline.pause[x1] = true;
				Sound.output.getSequencer().bassline.accent[x1] =!Sound.output.getSequencer().bassline.accent[x1] ;
				Sound.output.getSequencer().bassline.slide[x1] = false;
			}
		}

	}

	public void draw(SpriteBatch batch, float parentAlpha) {
		if (Sound.mutate && Math.random() < .01) {
			ttouch((int)(MathUtils.random() * 16),(int)(MathUtils.random() * 31)-16);
		}
		batch.end();
		Sound.renderer.setProjectionMatrix(batch.getProjectionMatrix());
		Sound.renderer.setTransformMatrix(batch.getTransformMatrix());
		Sound.renderer.translate(getX(), getY(), 0);

		int skipx = (int) (getWidth() / 16);
		int skipy = (int) (getHeight() / 31);
		// grid
		if (Sound.grid) {
			Sound.renderer.begin(ShapeType.Line);
			Sound.renderer.setColor(Color.GRAY);
			for (int r = 0; r < 16; r += 4) {
				Sound.renderer.line(r * skipx, 0, r * skipx, getHeight());
			}
			for (int r = 0; r < 32; r++) {
				Sound.renderer.line(0, r * skipy, getWidth(), r * skipy);
			}
			Sound.renderer.end();
		}

		Sound.renderer.begin(ShapeType.Line);
		Sound.renderer.setColor(Color.BLUE);
		// Sound.renderer.line((Sound.output.getSequencer().step-1)%16 * skipx,
		// 0,
		// (Sound.output.getSequencer().step-1)%16 * skipx, getHeight());
		Sound.renderer.line((Sound.output.getSequencer().step) % 16 * skipx, 0,
				(Sound.output.getSequencer().step) % 16 * skipx, getHeight());
		Sound.renderer.end();

		Sound.renderer.begin(ShapeType.Rectangle);
		Sound.renderer.setColor(Color.RED);

		Sound.renderer.rect(0, 0, this.getWidth(), this.getHeight());
		Sound.renderer.end();
		Sound.renderer.begin(ShapeType.FilledRectangle);
		Sound.renderer.setColor(Color.YELLOW);

		for (int r = 0; r < 16; r++) {
			if (Sound.output.getSequencer().bassline.pause[r])
				continue;
			int skipd = 3;
			if (Sound.output.getSequencer().bassline.slide[r])
				skipd = 0;
			// if (r > 0 && Sound.output.getSequencer().bassline.slide[r - 1])
			// skipd = 0;
			Sound.renderer
					.filledRect(r * skipx + skipd,
							(Sound.output.getSequencer().bassline.note[r] + 16)
									* skipy, skipx - skipd - skipd, skipy);
		}
		Sound.renderer.end();

		Sound.renderer.begin(ShapeType.FilledRectangle);
		Sound.renderer.setColor(Color.RED);
		for (int r = 0; r < 16; r++) {
			if (Sound.output.getSequencer().bassline.pause[r])
				continue;
			int skipd = 3;
			if (Sound.output.getSequencer().bassline.slide[r])
				skipd = 0;
			// if (r > 0 && Sound.output.getSequencer().bassline.slide[r - 1])
			// skipd = 0;

			if (Sound.output.getSequencer().bassline.accent[r])
				Sound.renderer.filledRect(r * skipx + skipd,
						(Sound.output.getSequencer().bassline.note[r] + 16)
								* skipy, skipx - skipd - skipd, skipy);
		}
		Sound.renderer.end();

		Sound.renderer.begin(ShapeType.Line);

		for (int r = 0; r < 15; r++) {
			if (!Sound.output.getSequencer().bassline.accent[r]) {

				Sound.renderer.setColor(Color.YELLOW);
			} else
				Sound.renderer.setColor(Color.RED);
			if (Sound.output.getSequencer().bassline.slide[r]
					&& !Sound.output.getSequencer().bassline.pause[r]) {
				Sound.renderer.line((r) * skipx + skipx / 2,
						(Sound.output.getSequencer().bassline.note[r] + 16)
								* skipy + skipy / 2, (r + 1) * skipx + skipx
								/ 2,
						(Sound.output.getSequencer().bassline.note[r + 1] + 16)
								* skipy + skipy / 2);
			}
		}
		Sound.renderer.end();

		batch.begin();
	}

}