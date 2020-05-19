package com.jiubang.commerce.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.jb.ga0.commerce.util.LogUtils;
import java.util.Calendar;

public class AdTimer {
    public static final long AN_HOUR = 3600000;
    public static final long ONE_DAY_MILLS = 86400000;
    private AMReceiver mAMReceiver = new AMReceiver();
    private String mAction;
    private AlarmManager mAlarmManager;
    private Context mContext;
    /* access modifiers changed from: private */
    public ITimeUp mListener;

    public interface ITimeUp {
        void onTimeUp();
    }

    public AdTimer(Context context, String action) {
        this.mContext = context;
        this.mAction = action;
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
    }

    public void schedule(long triggerAtMillis, ITimeUp listener) {
        this.mListener = listener;
        registerAMReceiver();
        try {
            this.mAlarmManager.set(0, triggerAtMillis, PendingIntent.getBroadcast(this.mContext, 0, new Intent(this.mAction), 134217728));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        this.mAlarmManager.cancel(PendingIntent.getBroadcast(this.mContext, 0, new Intent(this.mAction), 134217728));
        unregisterAMReceiver();
    }

    private void registerAMReceiver() {
        try {
            this.mContext.registerReceiver(this.mAMReceiver, new IntentFilter(this.mAction));
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: private */
    public void unregisterAMReceiver() {
        try {
            this.mContext.unregisterReceiver(this.mAMReceiver);
        } catch (Exception e) {
        }
    }

    public static long getTodayZeroMills() {
        Calendar c = Calendar.getInstance();
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTimeInMillis();
    }

    public class AMReceiver extends BroadcastReceiver {
        public AMReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            LogUtils.d("wbq", "Time is up!action:" + intent.getAction());
            AdTimer.this.unregisterAMReceiver();
            if (AdTimer.this.mListener != null) {
                AdTimer.this.mListener.onTimeUp();
            }
        }
    }
}
