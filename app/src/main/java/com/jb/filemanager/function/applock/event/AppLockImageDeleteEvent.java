package com.jb.filemanager.function.applock.event;

/**
 * Created by chenhewen on 16-3-8.
 * appLock分享详情页删除图片发送此事件
 */
public class AppLockImageDeleteEvent {

    private String mPath;

    public AppLockImageDeleteEvent(String path) {
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }
}
