package com.jb.filemanager.function.applock.event;

/**
 * Created by nieyh on 2016/12/28.
 */

public class ScreenStateEvent {

    public int mScreenState = -1;

    public static final int SCREEN_ON = 1;

    public static final int SCREEN_OFF = 2;

    public ScreenStateEvent(int state) {
        mScreenState = state;
    }
}
