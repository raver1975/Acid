package com.acid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import synth.BasslineSynthesizer;
import synth.Output;
import synth.Synthesizer;

public class Statics {


    public static boolean drumsOn = true;
    public static boolean synthOn1= true;
    public static boolean synthOn2= true;

    public static ShapeRenderer renderer;

    static BasslineSynthesizer synth1;
    static BasslineSynthesizer synth2;
    public static Synthesizer drums;
    public static Output output;

    public static boolean recording = true;
    public static boolean free = true;
    public static boolean waveSquare1;
    public static boolean waveSquare2;
    public static boolean export;
    public static FileHandle exportFile;
    public static FileHandle saveName;

//    public static FileHandle getFileHandle(String selected) {
//        //if (Gdx.files.isExternalStorageAvailable()) return Gdx.files.external(selected);
//        if (Gdx.files.isLocalStorageAvailable()) return Gdx.files.local(selected);
//        return null;
//    }
}
