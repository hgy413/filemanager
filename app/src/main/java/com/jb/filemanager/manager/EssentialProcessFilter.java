package com.jb.filemanager.manager;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.util.SimpleArrayMap;

import com.jb.filemanager.R;

import java.util.Collections;
import java.util.HashSet;

/**
 * 重要进程过滤器
 */
public class EssentialProcessFilter {

    // 模糊过滤，含有以下关键词的系统应用
    public static final String DIALER = "dialer";
    public static final String PHONE = "phone";
    public static final String CONTACTS = "contacts";
    public static final String ANDROID_SYSTEM = "android";

    /**
     * 系统关键进程及用户主要的进程
     */
    private static HashSet<String> sCoreList;

    /**
     * 同一包名app查杀间隔（30s）
     */
    private static final long APP_KILL_INTERVAL = 15000;
    /**
     * 保存最近查杀的进程列表
     * <p> [key:value]=[包名:查杀时间]
     * <p> 查杀时间使用SystemClock.elapsedRealtime()标识
     */
    private static SimpleArrayMap<String, Long> sRecentKilled = new SimpleArrayMap<>();

    /**
     * 加载系统核心进程列表
     *
     * @param context
     */
    public static void loadCoreList(Context context) {
        final Resources r = context.getResources();
        String[] corePackages = r.getStringArray(R.array.default_core_list);
        sCoreList = new HashSet<>();
        Collections.addAll(sCoreList, corePackages);
    }

    /**
     * 判断进程是否属于重要进程
     *
     * @param process
     * @return
     */
    public static boolean isEssentialProcess(String process) {
        return sCoreList.contains(process);
    }

    /**
     * 系统关键进程关键词模糊匹配
     *
     * @param process
     * @param isSystemApp
     * @return
     */
    public static boolean isEssentialProcessMock(String process, boolean isSystemApp) {
        return isSystemApp && (process.contains(DIALER)
                || process.contains(PHONE)
                || process.contains(CONTACTS)
                || process.equalsIgnoreCase(ANDROID_SYSTEM));
    }

    /**
     * 将上次被杀死的进程添加到过滤列表中
     *
     * @param pacakageName
     */
    public static void addKilledProcess(String pacakageName) {
        sRecentKilled.put(pacakageName, SystemClock.elapsedRealtime());
    }

    /**
     * 是否最近查杀过的进程，在指定时间内不再显示正在运行
     *
     * @return
     */
    public static boolean isRecentKilled(String packageName, long now) {
        boolean isRecently = false;
        Long lastKill = sRecentKilled.get(packageName);
        if (lastKill != null && (now - lastKill <= APP_KILL_INTERVAL)) {
            isRecently = true;
        } else {
            isRecently = false;
        }
        return isRecently;
    }

    /**
     * 退出主界面时清除最近查杀列表，减少内存占用
     */
    public static void cleanUpRecentKilledApps() {
        sRecentKilled.clear();
    }

}
