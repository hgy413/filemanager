package com.jiubang.commerce.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.jiubang.commerce.ad.manager.AdSdkManager;

public class BootBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (AdSdkManager.isShieldAdSdk()) {
        }
    }
}
