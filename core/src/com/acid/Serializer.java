package com.acid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.nio.charset.StandardCharsets;


/**
 * Created by Paul on 3/26/2016.
 */
public class Serializer {

    public static Object load(Class clazz,String file) throws Exception {
        FileHandle fileHandle = Gdx.files.local(file);
//        byte[] obj = fileHandle.readBytes();
//        ByteArrayInputStream fin = new ByteArrayInputStream(obj);
//        ObjectInputStream oos = new ObjectInputStream(fin);
//        Object o = oos.readObject();
//        fin.close();
        String json=fileHandle.readString();
        return new Json().fromJson(clazz,json);

    }

    public static void save(Object o, String file) throws Exception {
//        FileOutputStream fout = new FileOutputStream(file);
        FileHandle fileHandle = Gdx.files.local(file);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(baos);
//        oos.writeObject(o);
//        baos.close();
//        fileHandle.writeBytes(baos.toByteArray(), false);
        fileHandle.writeString(new Json().toJson(o),false);
    }

    public static String toBase64(Object o) throws Exception {
        return Base64Coder.encodeLines(new Json().toJson(o).getBytes());
    }

    public static Object fromBase64(String s) throws Exception {
        s=s.replaceAll("[^A-Za-z0-9+/=]","");
        String decoded=new String(Base64Coder.decode(s));
        return new Json().fromJson(SaveObject.class,decoded);
    }
}
