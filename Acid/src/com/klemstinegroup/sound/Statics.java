package com.klemstinegroup.sound;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import synth.BasslineSynthesizer;
import synth.Output;
import synth.Synthesizer;

public class Statics {

	public static final boolean grid = !false;
	public static Output output;
	public static boolean drumzzz = true;
	public static boolean zzzynth = true;
	public static ShapeRenderer renderer;
	static BasslineSynthesizer synth;
	static int[] maxValue = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	static int[] scale = new int[] { 0, 2, 4, 5, 7, 9, 11 };
	protected static boolean mutate;
	protected static Synthesizer drums;
	static boolean drumdisplay=true;

}