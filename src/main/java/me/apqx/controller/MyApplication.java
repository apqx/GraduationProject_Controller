package me.apqx.controller;

import android.app.Application;
import android.content.Context;

/**
 * Created by apqx on 2017/3/24.
 */

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
