package com.jb.filemanager.statistics;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.jb.filemanager.manager.spm.SharedPreferencesManager;

/**
 * 上传102静态统计协议
 * 默认8小时一次
 */
public class AlarmEight {
    private static final String ALARM_MANAGER_EIGHT_HOURS_102 = "alarm_manager_eight_hours";

    private static final String KEY_EIGHT_HOURS = "key_eight_hours";
    private static final long EIGHT_HOURS = 8 * 60 * 60 * 1000;
    private Context mContext;
    private AlarmManager mAlarmManager;

    public AlarmEight(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ALARM_MANAGER_EIGHT_HOURS_102);
        EightHoursReceiver mReceiver = new EightHoursReceiver();
        mContext.registerReceiver(mReceiver, intentFilter);
        startAlarmTask();
    }

    private void startAlarmTask() {
        try {
            long now = System.currentTimeMillis();
            long lastUploadTime = getLastUploadTime(KEY_EIGHT_HOURS);
            long pastTime = now - lastUploadTime;
            Intent intent = new Intent(ALARM_MANAGER_EIGHT_HOURS_102);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
            if (pastTime >= EIGHT_HOURS && lastUploadTime != 0) {
                upload102Infos(); // 上传102协议
                setLastTimeByName(KEY_EIGHT_HOURS, now);
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, now + EIGHT_HOURS, pendingIntent);
            } else if (lastUploadTime == 0) {
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, now + EIGHT_HOURS, pendingIntent);
            } else {
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, lastUploadTime + EIGHT_HOURS, pendingIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getLastUploadTime(String key) {
        SharedPreferencesManager sharedPreferences = SharedPreferencesManager.getInstance(mContext);
        long lastUploadTime = 0L;
        if (sharedPreferences != null) {
            lastUploadTime = sharedPreferences.getLong(key, 0); // 上次上传时间
        }
        return lastUploadTime;
    }

    private void setLastTimeByName(String key, long time) {
        SharedPreferencesManager sharedPreferences = SharedPreferencesManager.getInstance(mContext);
        if (sharedPreferences != null) {
            sharedPreferences.commitLong(key, time);
        }
    }

    /**
     *
     * com.jb.security.statistics.EightHoursReceiver
     *
     * @author wangying <br/>
     *         create at 2015-3-4 上午11:31:03
     */
    class EightHoursReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (context != null && intent.getAction() != null) {
                if (intent.getAction().equals(ALARM_MANAGER_EIGHT_HOURS_102)) {
                    long now = System.currentTimeMillis();
                    upload102Infos(); // 上传102协议
                    setLastTimeByName(KEY_EIGHT_HOURS, now);
                    Intent intent2 = new Intent(ALARM_MANAGER_EIGHT_HOURS_102);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent2, 0);
                    mAlarmManager.set(AlarmManager.RTC_WAKEUP, now + EIGHT_HOURS, pendingIntent);
                }
            }
        }
    }

    /**
     * 上传102统计信息
     */
    private void upload102Infos() {
        StatisticsTools.upload102InfoNew();
    }
}