package com.acid;

import android.os.Environment;

public class AndroidSDCard implements SDCard{
    @Override
    public String getPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
