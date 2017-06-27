package com.jb.filemanager.function.scanframe.bean.appBean;

import android.content.ComponentName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2016/10/21.<br>
 * 正在运行的程序列表<br>
 */

public class RunningAppBean extends BaseAppBean {
    /**
     * 进程PID
     */
    public ArrayList<Integer> mPids = new ArrayList<>();
    /**
     * 进程名
     */
    public String mProcessName;
    /**
     * 占用内存(KB)
     */
    public long mMemory = -1;
    /**
     * 是否为前台app进程
     */
    public boolean mIsForeground;
    /**
     * 是否为可打开的app
     */
    public boolean mIsLaunchableApp;
    /**
     * 是否在白名单
     */
    public boolean mIsIgnore;

    /**
     * 记录正在运行的服务的组件名字<br>
     */
    public final List<ComponentName> mRunningServices = new ArrayList<ComponentName>();

    /**
     * 是否存在正在运行的service
     */
    public boolean hasRunningServices() {
        return mRunningServices.size() > 0;
    }

    @Override
    public String toString() {
        return "RunningAppModle [mPids=" + mPids.toArray() + ", mProcessName=" + mProcessName + ", mMemory=" + mMemory
                + ", mIsForeground=" + mIsForeground + ", mIsLaunchableApp=" + mIsLaunchableApp + ", mIsIgnore="
                + mIsIgnore + ", hasRunningServices=" + hasRunningServices() + "]";
    }
}
