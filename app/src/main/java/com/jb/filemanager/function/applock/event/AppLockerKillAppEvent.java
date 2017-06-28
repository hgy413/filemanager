package com.jb.filemanager.function.applock.event;

/**
 * Created by nieyh on 2017/1/5.
 */

public class AppLockerKillAppEvent {

    public String pkgName;

    public AppLockerKillAppEvent(String pkgName) {
        this.pkgName = pkgName;
    }
}
