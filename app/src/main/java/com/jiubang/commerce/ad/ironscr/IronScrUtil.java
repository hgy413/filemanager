package com.jiubang.commerce.ad.ironscr;

import android.content.Context;

public class IronScrUtil {
    public static String getAppNameInEnglish(Context context) {
        String pkgName = context.getPackageName();
        if ("com.gau.go.launcherex".equals(pkgName)) {
            return "GO Launcher-Theme,Wallpaper";
        }
        if ("com.gau.go.launcherex.gowidget.gopowermaster".equals(pkgName)) {
            return "GO Battery Saver&Power Widget";
        }
        if ("com.jb.zcamera".equals(pkgName)) {
            return "Z Camera";
        }
        if ("com.jb.emoji.gokeyboard".equals(pkgName)) {
            return "GO Keyboard - Emoji, Sticker";
        }
        if ("com.jiubang.alock".equals(pkgName)) {
            return "AppLock pro - privacy & vault";
        }
        if ("com.jiubang.go.music".equals(pkgName)) {
            return "GO Music -mp3,equalizer,themes";
        }
        if ("com.gau.go.launcherex.gowidget.weatherwidget".equals(pkgName)) {
            return "GO Weather";
        }
        if ("com.g3.news".equals(pkgName)) {
            return "GO News";
        }
        if ("com.jiubang.goscreenlock".equals(pkgName)) {
            return "GO Locker";
        }
        if ("com.jiubang.fastestflashlight".equals(pkgName)) {
            return "Beacon Flashlight";
        }
        if ("com.gtp.nextlauncher.trial".equals(pkgName)) {
            return "Next Launcher Lite";
        }
        if ("com.zeroteam.zerolauncher".equals(pkgName)) {
            return "Zero Launcher";
        }
        if ("com.gto.zero.zboost".equals(pkgName)) {
            return "GO Speed";
        }
        if ("com.jb.security".equals(pkgName)) {
            return "GO Security";
        }
        if ("com.jb.gosms".equals(pkgName)) {
            return "GO SMS Pro";
        }
        if ("com.jb.gocaller".equals(pkgName)) {
            return "GO Caller";
        }
        return null;
    }
}
