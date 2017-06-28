package com.jb.filemanager.function.applock.event;

import android.content.ComponentName;

/**
 * 以一定的时间间隔不断发送的事件，附带当前前台应用的信息<br>
 */
public class OnFrontAppTickEvent {
    private ComponentName mComponentName;

    /**
     * 由于此事件发送频繁，为了不浪费对象，只需要一实例即可<br>
     */
    private final static OnFrontAppTickEvent sInstance = new OnFrontAppTickEvent();

    private boolean mIsFontAppChanged = false;

    private OnFrontAppTickEvent() {
    }

    public static OnFrontAppTickEvent getInstance() {
        return sInstance;
    }

    public void setIsFontAppChanged(boolean isFontAppChanged) {
        this.mIsFontAppChanged = isFontAppChanged;
    }

    public boolean isFontAppChanged() {
        return mIsFontAppChanged;
    }

    public void setComponentName(ComponentName componentName) {
        mComponentName = componentName;
    }

    public ComponentName getTopActivity() {
        return mComponentName;
    }

}
