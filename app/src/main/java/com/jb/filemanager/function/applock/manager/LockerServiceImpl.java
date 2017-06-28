package com.jb.filemanager.function.applock.manager;

import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import com.jb.filemanager.Const;

/**
 * 应用锁基础服务 Created by makai on 15-6-11.
 *
 */
public class LockerServiceImpl {

    private LockerFloatLayerManager mLockerFloatLayerManager;

    private LockerServiceDataManager mLockerServiceDataManager;

    private Context mContext = null;

    public LockerServiceImpl(Context context) {
        mContext = context;
        mLockerFloatLayerManager = LockerFloatLayerManager.getInstance();
        mLockerServiceDataManager = new LockerServiceDataManager(mContext);
    }

    /**
     * 整体步骤如下：<br>
     *     1. 先查看当前是否展示锁屏 - 没有则判定为条件满足 <br>
     *     2. 再查看当前应用与上一次展示锁屏时候的应用是否一样 - 如果不一样则判定为条件满足 <br>
     */
    public void actionOnFrontAppTick(ComponentName componentName) {
        boolean needCheck = !mLockerFloatLayerManager.isShowingLocker();
        if (!TextUtils.isEmpty(componentName.getPackageName())
                && !mLockerFloatLayerManager.getCurrentPkgName().equals(componentName.getPackageName())) {
            needCheck = true;
        }
        if (needCheck) {
            mLockerServiceDataManager.checkStatusChanage(componentName);
        }
    }

    /**
     * 刷新数据，当锁应用发生变化时调用
     */
    public void actionRefreshLockerData() {
        mLockerServiceDataManager.startLoadData();
    }

    /**
     * 选择退出
     */
    public void actionUserChoiceCancel(String pkgname) {
        if (!Const.PACKAGE_NAME.equals(pkgname)) {
             mLockerServiceDataManager.choiceCancel(pkgname);
        }
    }

    /**
     * 锁应用
     */
    public void actionLockApp(String pkgName) {
        mLockerFloatLayerManager.showFloatViewOutSide(pkgName);
    }

    /**
     * 重新设置上一次加锁记录
     */
    public void actionScreenOpenClearData() {
        mLockerServiceDataManager.reset();
    }

    /**
     * 执行解除锁定应用
     * */
    public void actionUnLockApp() {
        mLockerFloatLayerManager.onBackPress();
    }


    public void onDestory() {
        mLockerFloatLayerManager.onDestory();
        mLockerFloatLayerManager = null;
        mLockerServiceDataManager = null;
    }
}
