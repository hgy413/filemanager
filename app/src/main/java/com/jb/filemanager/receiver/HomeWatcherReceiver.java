package com.jb.filemanager.receiver;

import android.content.Context;
import android.content.Intent;

/**
 * Created by nieyh on 2016/10/12.
 *
 */
public class HomeWatcherReceiver extends BaseBroadcastReceiver {

    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

    private TouchSystemKeyListener mTouchSystemKeyListener;

    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                // 短按Home键
                if (mTouchSystemKeyListener != null) {
                    mTouchSystemKeyListener.onTouchHome();
                }
            } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                // 长按Home键 或者 activity切换键
            } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                // 锁屏
            } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                // SamSung 长按Home键
            }
        }
    }

    public void setTouchSystemKeyListener(TouchSystemKeyListener touchSystemKeyListener) {
        mTouchSystemKeyListener = touchSystemKeyListener;
    }

    public interface TouchSystemKeyListener {
        void onTouchHome();
    }
}