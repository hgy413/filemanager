package com.jb.filemanager.function.zipfile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jb.filemanager.function.zipfile.ExtractManager;

/**
 * Created by xiaoyu on 2017/7/6 20:12.
 */

public class NotificationClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ExtractManager.getInstance().addProgressDialogToWindow();
    }
}
