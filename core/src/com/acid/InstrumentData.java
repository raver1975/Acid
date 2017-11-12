package com.acid;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.Serializable;

/**
 * Created by Paul on 1/15/2017.
 */
public abstract class InstrumentData implements Serializable {
    transient Pixmap pixmap;
    transient public TextureRegion region;
    abstract public void refresh();
    abstract public Pixmap drawPixmap(int w, int h);
}
