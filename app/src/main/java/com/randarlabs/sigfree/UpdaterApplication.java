package com.randarlabs.sigfree;

import android.app.Application;

import com.randarlabs.sigfree.util.AppPreferences;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

public class UpdaterApplication extends Application {
    private static AppPreferences appPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        appPreferences = new AppPreferences(this);
        Iconics.registerFont(new MaterialDesignIconic());
    }

    public static AppPreferences getAppPreferences() {
        return appPreferences;
    }
}