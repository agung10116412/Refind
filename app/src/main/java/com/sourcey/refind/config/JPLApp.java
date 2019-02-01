package com.sourcey.refind.config;

import android.app.Application;

/**
 * Created by dhamdani666 on 3/3/17.
 */



public class JPLApp extends Application {

    public static final String TAG = JPLApp.class.getSimpleName();
    private static JPLApp mInstance;

    public static synchronized JPLApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/MavenPro-Regular.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/MavenPro-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/MavenPro-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/MavenPro-Regular.ttf");


//        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Rubik-Regular.ttf");
//        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Rubik-Regular.ttf");
//        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Rubik-Regular.ttf");
//        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Rubik-Regular.ttf");

//

//        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/ABEL-REGULAR.TTF");
//        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/ABEL-REGULAR.TTF");
//        FontsOverride.setDefaultFont(this, "SERIF", "fonts/ABEL-REGULAR.TTF");
//        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/ABEL-REGULAR.TTF");

        mInstance = this;
    }


}