package com.jb.filemanager.function.scanframe.bean.appBean;

import android.content.ComponentName;

/**
 * Created by xiaoyu on 2016/10/21.<br>
 * app信息的基类<br>
 */

public class BaseAppBean {
    /**
     * app名字
     */
    public String mAppName;
    /**
     * 包名
     */
    public String mPackageName;
    /**
     * ComponentName
     */
    public ComponentName mComponent;

    /**
     * 是否系统应用
     */
    public boolean mIsSysApp;
}
