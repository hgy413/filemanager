package com.jb.filemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.event.ScreenStateEvent;

/**
 * Created by nieyh on 2016/12/28.
 */

public class ScreenStateReceiver {

    private static ScreenStateReceiver sInstance;

    private ScreenStateReceiver() {}

    public static ScreenStateReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new ScreenStateReceiver();
        }
        return sInstance;
    }

    private BroadcastReceiver mCommonReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                TheApplication.getGlobalEventBus().post(new ScreenStateEvent(ScreenStateEvent.SCREEN_OFF));
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                TheApplication.getGlobalEventBus().post(new ScreenStateEvent(ScreenStateEvent.SCREEN_ON));
            }
        }
    };

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        TheApplication.getAppContext().registerReceiver(mCommonReceiver, filter);
    }

    public void unRegisterReceiver() {
        TheApplication.getAppContext().unregisterReceiver(mCommonReceiver);
    }

}
