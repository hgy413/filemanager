package com.jiubang.commerce.utils;

import android.content.Context;
import com.jb.ga0.commerce.util.CustomAlarm;
import com.jb.ga0.commerce.util.CustomAlarmManager;

public class AlarmProxy {
    public static final int ALARMID_ABTEST = 1;
    public static final int ALARMID_CACHE_CHECK = 2;

    public static CustomAlarm getAlarm(Context context) {
        return CustomAlarmManager.getInstance(context).getAlarm("GoAdSdk");
    }
}
