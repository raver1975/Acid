package com.klemstinegroup.sound;

import synth.BasslineSynthesizer;
import synth.Output;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class LightActor extends Actor {

	int radius = 0;
	Color color;
	boolean on;

	public LightActor(int radius, Color color, boolean on) {
		this.on = on;
		this.radius = radius;
		this.color = color;
		this.setWidth(radius * 2);
		this.setHeight(radius * 2);
	}

	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.end();
		Statics.renderer.setProjectionMatrix(batch.getProjectionMatrix());
		Statics.renderer.setTransformMatrix(batch.getTransformMatrix());
		Statics.renderer.translate(getX(), getY(), 0);
		if (on) {
			Statics.renderer.begin(ShapeType.FilledCircle);
			Statics.renderer.setColor(color);
			Statics.renderer.filledCircle(radius, radius, radius, 10);
			Statics.renderer.end();

		} else {
			Statics.renderer.begin(ShapeType.Circle);
			Statics.renderer.setColor(color);
			Statics.renderer.circle(radius, radius, radius, 10);
			Statics.renderer.end();
		}
		batch.begin();
	}

}