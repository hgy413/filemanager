package com.jb.filemanager.function.permissionalarm.event;

/**
 * Created by nieyh on 2017/2/10.
 * 权限框隐藏事件
 */

public class PermissionViewDismissEvent {
    //是否重要 note: 代表是否需要特别注意 （额外标记，可以自己定义含义）
    public boolean isSignificance = false;

    public PermissionViewDismissEvent() {

    }

    public PermissionViewDismissEvent(boolean isSignificance) {
        this.isSignificance = isSignificance;
    }
}
