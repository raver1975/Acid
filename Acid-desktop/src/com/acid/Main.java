package com.acid;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.klemstinegroup.sound.Sound;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Acid";
		cfg.useGL20 = false;
		cfg.width = 480*2;
		cfg.height = 320*2;
		    
		new LwjglApplication(new Sound(), cfg);
	}
}
