package com.jb.filemanager.function.applock.event;

/**
 * Created by nieyh on 2017/1/4.
 * 入侵者开关状态改变
 */

public class IntruderSwitcherStateEvent {
    //是否打开
    public boolean isOpen;

    public IntruderSwitcherStateEvent(boolean isOpen) {
        this.isOpen = isOpen;
    }
}
