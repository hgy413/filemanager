package com.jb.filemanager.home.event;

/**
 * Created by nieyh on 17-7-26.
 */

public class SwitcherChgStateEvent {

    private static int USB_TYPE = 1;
    private static int FREESPACE_TYPE = 2;
    private static int LOGGER_TYPE = 3;

    private int mType;
    private boolean mEnable;

    private SwitcherChgStateEvent(int type, boolean enable) {
        mType = type;
        mEnable = enable;
    }

    public static SwitcherChgStateEvent buildUsbStateChgEvent(boolean isEnable) {
        return new SwitcherChgStateEvent(USB_TYPE, isEnable);
    }

    public static SwitcherChgStateEvent buildLoggerStateChgEvent(boolean isEnable) {
        return new SwitcherChgStateEvent(LOGGER_TYPE, isEnable);
    }

    public static SwitcherChgStateEvent buildFreeSpaceStateChgEvent(boolean isEnable) {
        return new SwitcherChgStateEvent(FREESPACE_TYPE, isEnable);
    }

    public boolean isUsb() {
        return mType == USB_TYPE;
    }

    public boolean isFreespace() {
        return mType == FREESPACE_TYPE;
    }

    public boolean isLogger() {
        return mType == LOGGER_TYPE;
    }

    public boolean isEnable() {
        return mEnable;
    }
}
