package com.randarlabs.sigfree.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.randarlabs.sigfree.util.UtilsAsync;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new UtilsAsync.NotifyWhatsAppVersion(context, intent).execute();
    }

}
