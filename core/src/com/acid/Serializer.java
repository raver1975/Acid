package com.acid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Paul on 3/26/2016.
 */
public class Serializer {

    public static Object load(String file) throws Exception {
        FileHandle fileHandle = Gdx.files.local(file);
        byte[] obj = fileHandle.readBytes();
        ByteArrayInputStream fin = new ByteArrayInputStream(obj);
        ObjectInputStream oos = new ObjectInputStream(fin);
        Object o = oos.readObject();
        fin.close();
        return o;

    }

    public static void save(Object o, String file) throws Exception {
//        FileOutputStream fout = new FileOutputStream(file);
        FileHandle fileHandle = Gdx.files.local(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        baos.close();
        fileHandle.writeBytes(baos.toByteArray(), false);
    }
}
