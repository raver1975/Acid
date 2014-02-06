package com.acid;

import com.badlogic.gdx.backends.lwjgl.LwjglApplet;
import com.klemstinegroup.sound.Sound;

public class MainApplet extends LwjglApplet {
	private static final long serialVersionUID = 1L;

	public MainApplet() {
		super(new Sound(), false);
	}
}