package com.randarlabs.sigfree.util;

import android.content.Context;
import android.content.pm.PackageManager;

import com.randarlabs.sigfree.BuildConfig;
import com.randarlabs.sigfree.object.Version;

public class UtilsWhatsApp {

    public static String getInstalledWhatsAppVersion(Context context) {
        String version = "";

        try {
            version = context.getPackageManager().getPackageInfo("org.thoughtcrime.securesms", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static Boolean isWhatsAppInstalled(Context context) {
        Boolean res;

        try {
            context.getPackageManager().getPackageInfo("org.thoughtcrime.securesms", 0);
            res = true;
        } catch (PackageManager.NameNotFoundException e) {
            res = false;
        }

        return res;
    }

    public static Boolean isUpdateAvailable(String installedVersion, String latestVersion) {
        if (BuildConfig.DEBUG_MODE) {
            return false;
        } else {
            Version installed = new Version(installedVersion);
            Version latest = new Version(latestVersion);

            return installed.compareTo(latest) < 0;
        }
    }

}
