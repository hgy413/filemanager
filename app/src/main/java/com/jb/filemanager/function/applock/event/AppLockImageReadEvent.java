package com.jb.filemanager.function.applock.event;

/**
 * Created by chenhewen on 16-3-9.
 */
public class AppLockImageReadEvent {

    private String mPath;

    public AppLockImageReadEvent(String path) {
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }
}
