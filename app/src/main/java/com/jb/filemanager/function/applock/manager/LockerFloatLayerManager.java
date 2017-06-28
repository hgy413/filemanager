package com.jb.filemanager.function.applock.manager;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.activity.AppLockActivity;
import com.jb.filemanager.function.applock.activity.IntruderHoriGalleryActivity;
import com.jb.filemanager.function.applock.activity.IntruderOpenGuideActivity;
import com.jb.filemanager.function.applock.activity.RetrievePasswordActivity;
import com.jb.filemanager.function.applock.event.AppLockerKillAppEvent;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;
import com.jb.filemanager.function.applock.view.CustomWindowManager;
import com.jb.filemanager.function.applock.view.FloatAppLockerView;
import com.jb.filemanager.function.applock.view.PatternView;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.CameraUtil;

import java.util.List;

/**
 * Created by nieyh on 2017/1/5.
 * 应用锁悬浮窗展示的所有逻辑在这里
 */

public class LockerFloatLayerManager implements FloatAppLockerView.IFloatAppLockerViewEvtListener {

    private static LockerFloatLayerManager sIntance;
    //用于添加悬浮窗的管理
    private CustomWindowManager mCustomWindowManager;
    //浮窗展示的View
    private FloatAppLockerView mFloatAppLockerView;
    //当前的应用包名
    private String mCurrentPkgName;
    //当前密码已经输错次数
    private int mCurrentWrongTime;
    //当前密码输入错误最多次数
    private int mMaxWrongTimes;

    private LockerFloatLayerManager() {
        mCustomWindowManager = new CustomWindowManager(TheApplication.getAppContext());
    }

    /**
     * 初始化浮动窗口
     * */
    private void safeToInitFloatAppLockerView() {
        if (mFloatAppLockerView == null) {
            mFloatAppLockerView = new FloatAppLockerView(TheApplication.getAppContext());
            mFloatAppLockerView.setIFloatAppLockerViewEvtListener(this);
        }
    }

    public static LockerFloatLayerManager getInstance() {
        if (sIntance == null) {
            sIntance = new LockerFloatLayerManager();
        }
        return sIntance;
    }

    /**
     * 在外部展示悬浮窗
     */
    public void showFloatViewOutSide(String pkgName) {
        //增加保护
        if (TextUtils.isEmpty(pkgName)) {
            return;
        }
//        if (!TextUtils.isEmpty(mCurrentPkgName) && pkgName.equals(mCurrentPkgName)) {
//            //之前锁过的界面 直接过滤
//            return;
//        }
        //重置包名
        mCurrentPkgName = pkgName;
        safeToInitFloatAppLockerView();
        mFloatAppLockerView.bindViewData(mCurrentPkgName, FloatAppLockerView.VIEW_OUTSIDE_APP, AppLockerDataManager.getInstance().isPatternPsd());
        mFloatAppLockerView.resetPatternView();
        //获取最大的次数
        mMaxWrongTimes = SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getInt(IPreferencesIds.KEY_APP_LOCK_WRONG_PSD_TIMES, 2);
        mCustomWindowManager.addView(mFloatAppLockerView);
    }

    /**
     * 在外部展示悬浮窗
     */
    public void showFloatViewInSide() {
        //重置包名
        mCurrentPkgName = TheApplication.getAppContext().getPackageName();
        safeToInitFloatAppLockerView();
        mFloatAppLockerView.bindViewData(mCurrentPkgName, FloatAppLockerView.VIEW_INSIDE_APP, AppLockerDataManager.getInstance().isPatternPsd());
        mFloatAppLockerView.resetPatternView();
        //获取最大的次数
        mMaxWrongTimes = SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getInt(IPreferencesIds.KEY_APP_LOCK_WRONG_PSD_TIMES, 2);
        mCustomWindowManager.addView(mFloatAppLockerView);
    }

    /**
     * 获取是否可以展示入侵者引导对话框
     */
    private boolean isShouldShowIntruderGuideDialog() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        //是否前置摄像头可用
        if (!CameraUtil.isFrontCameraAvailable()) {
            return false;
        }
        //是否Intruder开关是否打开
        if (isIntruderOpen()) {
            return false;
        }
        //弹出次数
        int popTimes = sharedPreferencesManager.getInt(IPreferencesIds.KEY_INTRUDER_DIALOG_POP_TIMES, 0);
        if (popTimes >= 2) {
            return false;
        }
        //上次弹出时间
        long lastPopTimeLong = sharedPreferencesManager.getLong(IPreferencesIds.KEY_LAST_INTRUDER_DIALOG_POP_TIME, 0);
        if (System.currentTimeMillis() - lastPopTimeLong < 86400000) {
            return false;
        }
        //是否之前开启过
        if (sharedPreferencesManager.getBoolean(IPreferencesIds.KEY_INTRUDER_SETTING_CHANGED, false)) {
            return false;
        }

        sharedPreferencesManager.commitInt(IPreferencesIds.KEY_INTRUDER_DIALOG_POP_TIMES, ++popTimes);
        sharedPreferencesManager.commitLong(IPreferencesIds.KEY_LAST_INTRUDER_DIALOG_POP_TIME, System.currentTimeMillis());
        return true;
    }

    /**
     * 判断输入密码是否正确
     */
    private boolean judgePasscode(List<PatternView.Cell> cellList, String[] numbers) {
        String passcode = AppLockerDataManager.getInstance().getLockerPassword();
        if (passcode == null) {
            return false;
        }
        if (cellList != null) {
            if (passcode.trim().equals(cellList.toString())) {
                return true;
            }
        }
        if (numbers != null) {
            StringBuilder inputPsd = new StringBuilder();
            for (String number : numbers) {
                inputPsd.append(number);
            }
            if (passcode.trim().equals(inputPsd.toString().trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否开关打开
     */
    private boolean isIntruderOpen() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        return sharedPreferencesManager.getBoolean(IPreferencesIds.KEY_APP_LOCK_REVEAL_ENABLE, false);
    }

    /***********
     * 浮窗View的回调函数{@link FloatAppLockerView.IFloatAppLockerViewEvtListener}
     **********/

    @Override
    public void onBackPress() {
        if (mFloatAppLockerView != null && mFloatAppLockerView.getParent() != null) {
            if (mFloatAppLockerView.isNeedKillLockerApp()) {
                TheApplication.getGlobalEventBus().post(new AppLockerKillAppEvent(mCurrentPkgName));
            }
            mCustomWindowManager.removeView(mFloatAppLockerView);
            mCurrentWrongTime = 0;
        }
    }

    @Override
    public void onForgetClick(View v) {
        if (mFloatAppLockerView != null && mFloatAppLockerView.getParent() != null) {
            mCustomWindowManager.removeView(mFloatAppLockerView);
            mCurrentWrongTime = 0;
        }
        Intent i = new Intent(TheApplication.getAppContext(), RetrievePasswordActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TheApplication.getAppContext().startActivity(i);
    }

    @Override
    public void onInputCompleted(List<PatternView.Cell> cellList, String[] numbers) {
        if (mFloatAppLockerView != null && mFloatAppLockerView.getParent() != null) {
            //当密码错误的时候
            if (!judgePasscode(cellList, numbers)) {
                mCurrentWrongTime++;
                //当开关已经打开
                if (isIntruderOpen()) {
                    //当快到时候打开相机
                    if (mCurrentWrongTime == mMaxWrongTimes - 1) {
                        mFloatAppLockerView.openCamera(mCurrentPkgName);
                    }
                    //到达时候则拍照
                    if (mCurrentWrongTime == mMaxWrongTimes) {
                        mFloatAppLockerView.capturePeep();
                    }
                }
                mFloatAppLockerView.showNumberErrorState();
                mFloatAppLockerView.delayClearErrorPattern();
            } else {
                boolean isInside = mFloatAppLockerView.isInsideAppLockPop();
                //在外部锁上时 输对密码时
                if (AntiPeepDataManager.getInstance(TheApplication.getAppContext()).getUnreadPhoto().size() != 0
                        && !isInside) {
                    //在外部解锁 并发现拥有图片
                    //显示图片
                    IntruderHoriGalleryActivity.gotoIntruderHoriGallery(TheApplication.getAppContext());
                } else {
                    //如果在内部则跳转到应用锁界面
                    if (isInside) {
                        AppLockActivity.gotoAppLock(TheApplication.getAppContext());
                    }
                    if (mCurrentWrongTime >= mMaxWrongTimes && isShouldShowIntruderGuideDialog()) {
                        //达到了最大错误数量
                        //是否需要引导打开入侵者拍照功能
                        IntruderOpenGuideActivity.pop();
                    }
                }
                //当密码输入正确的时候 并且已经错过很多次
                mCustomWindowManager.removeView(mFloatAppLockerView);
                mCurrentWrongTime = 0;
                mFloatAppLockerView.resetPatternView();
            }
        }
    }

    @Override
    public void onHomeClick() {
        if (mFloatAppLockerView != null && mFloatAppLockerView.getParent() != null) {
            if (mFloatAppLockerView.isNeedKillLockerApp()) {
                TheApplication.getGlobalEventBus().post(new AppLockerKillAppEvent(mCurrentPkgName));
            }
            mCustomWindowManager.removeView(mFloatAppLockerView);
            mCurrentWrongTime = 0;
        }
    }

    /**
     * 是否正在展示
     */
    public boolean isShowingLocker() {
        return mFloatAppLockerView != null && mFloatAppLockerView.getParent() != null;
    }

    /**
     * 获取当前应用
     */
    public String getCurrentPkgName() {
        return mCurrentPkgName == null ? "" : mCurrentPkgName;
    }

    /**
     * 销毁
     */
    public void onDestory() {
        sIntance = null;
        mCustomWindowManager = null;
        mFloatAppLockerView = null;
    }
}
