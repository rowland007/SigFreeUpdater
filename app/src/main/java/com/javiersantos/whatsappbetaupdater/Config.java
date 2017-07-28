package com.javiersantos.whatsappbetaupdater;

public class Config {

    public static final String GITHUB_URL = "https://github.com/rowland007/SigFreeUpdater";
    public static final String GITHUB_TAGS = GITHUB_URL.concat("/tags");
    public static final String GITHUB_APK = GITHUB_URL.concat("/releases/download/");
    public static final String WHATSAPP_URL = "https://updates.signal.org/android/";
    public static final String WHATSAPP_APK = WHATSAPP_URL.concat("Signal-website-release-4.8.1.apk");
    public static final String PAYPAL_DONATION = "donate@javiersantos.me";

    public static final String PATTERN_LATEST_VERSION = "<p class=\"version\" align=\"center\">Version";
    public static final String PATTERN_LATEST_VERSION_CDN = "<a class=\"button\" href=\"";

}
