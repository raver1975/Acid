package com.acid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.acid.Acid;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Here, thisActivity is the current activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission1 = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permission2 = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permission1 != PackageManager.PERMISSION_GRANTED||permission2 != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        1
                );
            }
        }
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new Acid(), config);
    }
}
