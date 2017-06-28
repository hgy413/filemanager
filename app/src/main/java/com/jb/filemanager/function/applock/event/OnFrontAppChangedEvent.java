package com.jb.filemanager.function.applock.event;

import android.content.ComponentName;
import android.text.TextUtils;

import com.jb.filemanager.function.applock.service.FrontAppMonitor;

/**
 * 当处于前台的应用改变时发出的事件<br>
 * 注意: 目前只有在5.0以下才有效, 5.0以上获得使用应用统计信息授权才有效<br>
 */
public class OnFrontAppChangedEvent {
    private ComponentName mComponentName;

    /**
     * 由于此事件发送频繁，为了不浪费对象，只需要一实例即可<br>
     */
    private final static OnFrontAppChangedEvent sInstance = new OnFrontAppChangedEvent();

    private OnFrontAppChangedEvent() {
    }

    public static OnFrontAppChangedEvent getInstance() {
        return sInstance;
    }

    public void setComponentName(ComponentName componentName) {
        mComponentName = componentName;
    }

    public String getFrontAppPackageName() {
        String packageName = mComponentName.getPackageName();
        if (TextUtils.isEmpty(packageName)) {
            packageName = FrontAppMonitor.INVALID_PACKAGE_NAME;
        }
        return packageName;
    }

    public ComponentName getTopActivity() {
        return mComponentName;
    }

}
