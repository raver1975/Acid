package com.acid.actors;

import com.acid.ColorHelper;
import com.acid.Statics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LightActor extends Actor {

	int radius = 0;
	Color color;
	public boolean on;

	public LightActor(int radius, Color color, boolean on) {
		this.on = on;
		this.radius = radius;
		this.color = color;
		this.setWidth(radius * 2);
		this.setHeight(radius * 2);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		Statics.renderer.setProjectionMatrix(batch.getProjectionMatrix());
		Statics.renderer.setTransformMatrix(batch.getTransformMatrix());
		Statics.renderer.translate(getX(), getY(), 0);
		if (on) {
			Statics.renderer.begin(ShapeType.Filled);
			Statics.renderer.setColor(color==null?ColorHelper.rainbow():color);
			Statics.renderer.circle(radius, radius, radius, 10);
			Statics.renderer.end();

		} else {
			Statics.renderer.begin(ShapeType.Line);
			Statics.renderer.setColor(color==null? ColorHelper.rainbow():color);
			Statics.renderer.circle(radius, radius, radius, 10);
			Statics.renderer.end();
		}
		batch.begin();
	}

}