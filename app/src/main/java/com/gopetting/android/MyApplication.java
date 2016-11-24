package com.gopetting.android;

import android.app.Application;
import android.content.res.Configuration;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.instamojo.android.Instamojo;

/**
 * Created by Sumit on 10/2/2016.
 */
public class MyApplication extends Application {
    private static MyApplication singleton;

    public static MyApplication getInstance(){
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Instamojo.initialize(this); //Initialize Instamojo

        singleton = this;

        //set Custom Typeface
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/DIN-Regular.otf");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

