package com.jb.filemanager.alarmtask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.util.device.Machine;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by bill wang on 2017/6/20.
 *
 */

@SuppressWarnings("unused")
public class ScheduleTaskHandler {

    private static final String ACTION_UPLOAD_BASIC_STATIC = "upload_base_static_data";
    private static final String ACTION_UPLOAD_ONE_HOUR = "upload_one_hour";

    // 15秒
    private final static long AUTO_CHECK_DELAY = 15 * 1000; //
    // 每隔8小时检查一次更新
    private final static long UPDATE_8_HOURS_INTERVAL = 8 * 60 * 60 * 1000;
    // 安装前一小时实时上传
    private final static long UPDATE_1_HOURS_INTERVAL = 60 * 60 * 1000;

    private static final String SP_UPLOAD_BASIC_KEY = "sp_upload_basic_key";
    private static final String SP_UPLOAD_FAIL_KEY = "sp_upload_fail_key";
    private static final String SP_UPLOAD_NOW_KEY = "sp_upload_now_key";

    private final HashMap<String, PendingIntent> mPendingHashMap = new HashMap<>(2);

    private Context mContext;
    private AlarmManager mAlarmManager;
    private TaskReceiver mReceiver;

    // 是否需要马上上传
    private boolean mIsNowUpload;

    public ScheduleTaskHandler(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPLOAD_BASIC_STATIC);
        filter.addAction(ACTION_UPLOAD_ONE_HOUR);

        // 注册网络状态监听
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        mReceiver = new TaskReceiver();
        mContext.registerReceiver(mReceiver, filter);
        SharedPreferencesManager sp = SharedPreferencesManager.getInstance(mContext);
        mIsNowUpload = sp.getBoolean(SP_UPLOAD_NOW_KEY, true);

        if (mIsNowUpload) {
            startAlarmTask(UPDATE_1_HOURS_INTERVAL, ACTION_UPLOAD_ONE_HOUR);
        }

        // 先15秒上传，然后是每个8小时上传，如果没有网络，当有网络的时候，调整下一次tick时间，保持8小时上传
        startAlarmTask(AUTO_CHECK_DELAY, ACTION_UPLOAD_BASIC_STATIC); // v5.01 关闭后台自动下载apk
    }

    private void startAlarmTask(long time, String action, int flag) {
        try {
            final long triggerTime = System.currentTimeMillis() + time;

            Intent intent = new Intent(action);
            if (flag != -1) {
                intent.setFlags(flag);
            }
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            mPendingHashMap.put(action, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启任意一项定时任务
     *
     * @param time   （定时时间）
     * @param action （要执行的action）
     */
    private void startAlarmTask(long time, String action) {
        try {
            startAlarmTask(time, action, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void cancel() {
        Collection<PendingIntent> collection = mPendingHashMap.values();
        for (PendingIntent pendingIntent : collection) {
            mAlarmManager.cancel(pendingIntent);
        }
        mPendingHashMap.clear();
        mContext.unregisterReceiver(mReceiver);
    }

    /**
     * 功能简述:取消特定action的pendingIntent 注意:
     *
     * @param action action
     */
    public synchronized void cancelPendingIntent(String action) {
        if (mPendingHashMap != null && mAlarmManager != null) {
            mAlarmManager.cancel(mPendingHashMap.get(action));
            mPendingHashMap.remove(action);
        }
    }

    private void uploadStaticWhileFail() {
        SharedPreferencesManager sharedPreferences = SharedPreferencesManager.getInstance(mContext);
        boolean isFail = sharedPreferences.getBoolean(SP_UPLOAD_FAIL_KEY, false);
        if (isFail) {
            sharedPreferences.commitBoolean(SP_UPLOAD_FAIL_KEY, false);
            doUploadBasicInfo();
        }

    }

    /**
     * 15s开始统计一次活跃用户的定时操作，之后每隔8小时再处理一次 3min的那次暂时不用此方法
     */
    private void doUploadBasicInfo() {

        if (!Machine.isNetworkOK(mContext)) {
            SharedPreferencesManager sharedPreferences = SharedPreferencesManager.getInstance(mContext);
            sharedPreferences.commitBoolean(SP_UPLOAD_FAIL_KEY, true);
        }

        long now = System.currentTimeMillis();
        long lastUploadTime = getLastTimeByName(SP_UPLOAD_BASIC_KEY);
        final long pastTime = now - lastUploadTime;
        long nextCheckTime = UPDATE_8_HOURS_INTERVAL; // 下一次上传间隔时间 用于19协议

        if (lastUploadTime == 0L || pastTime >= UPDATE_8_HOURS_INTERVAL || pastTime <= 0L) {
            // 上传桌面活跃用户统计
            upload();
            // 保存本次检查的时长
            setLastTimeByName(SP_UPLOAD_BASIC_KEY, now);
        } else {
            // 动态调整下一次的间隔时间
            nextCheckTime = UPDATE_8_HOURS_INTERVAL - pastTime;
        }
        // 启动下一次定时检查
        startAlarmTask(nextCheckTime, ACTION_UPLOAD_BASIC_STATIC);
    }

    private long getLastTimeByName(String key) {
        SharedPreferencesManager sharedPreferences = SharedPreferencesManager.getInstance(mContext);
        long lastTime = 0L;
        if (sharedPreferences != null) {
            lastTime = sharedPreferences.getLong(key, 0L);
        }
        return lastTime;
    }

    /**
     * 在sp记录的某个任务的最后一个时间（根据定义的sp名字和key来设置）
     *
     * @param key key
     * @param time time
     */
    private void setLastTimeByName(String key, long time) {
        SharedPreferencesManager sharedPreferences = SharedPreferencesManager.getInstance(mContext);
        if (sharedPreferences != null) {
            sharedPreferences.commitLong(key, time);
        }
    }

    /**
     * 任务广播接受者
     *
     * @author yangguanxiang
     */
    private class TaskReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            try {
                switch (action) {
                    case ConnectivityManager.CONNECTIVITY_ACTION:
                        // 网络监听
                        TheApplication.postRunOnShortTaskThread(new Runnable() {

                            @Override
                            public void run() {
                                uploadStaticWhileFail();
                            }
                        });
                        break;
                    case ACTION_UPLOAD_BASIC_STATIC:
                        // 基础数据上传
                        TheApplication.postRunOnShortTaskThread(new Runnable() {
                            @Override
                            public void run() {
                                doUploadBasicInfo();
                            }
                        });
                        break;
                    case ACTION_UPLOAD_ONE_HOUR:
                        TheApplication.postRunOnShortTaskThread(new Runnable() {

                            @Override
                            public void run() {
                                mIsNowUpload = false;
                                SharedPreferencesManager sp = SharedPreferencesManager.getInstance(mContext);
                                sp.commitBoolean(SP_UPLOAD_NOW_KEY, false);
                            }
                        });
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void upload() {
        startUpload();
    }


    /**
     * 用户基础信息
     * GO系列用户统计协议（19）
     * http://wiki.3g.net.cn/pages/viewpage.action?pageId=6914524
     */
    private void startUpload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StatisticsTools.upload19Info();
            }
        }).start();
    }
}
