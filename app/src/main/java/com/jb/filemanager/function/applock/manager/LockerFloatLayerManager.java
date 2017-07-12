package com.jb.filemanager.function.applock.manager;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.activity.RetrievePasswordActivity;
import com.jb.filemanager.function.applock.event.AppLockerKillAppEvent;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;
import com.jb.filemanager.function.applock.view.CustomWindowManager;
import com.jb.filemanager.function.applock.view.FloatInnerAppLockerView;
import com.jb.filemanager.function.applock.view.FloatOuterAppLockerView;
import com.jb.filemanager.function.applock.view.IFloatAppLockerViewEvtListener;
import com.jb.filemanager.function.applock.view.PatternView;

import java.util.List;

/**
 * Created by nieyh on 2017/1/5.
 * 应用锁悬浮窗展示的所有逻辑在这里
 */

public class LockerFloatLayerManager implements IFloatAppLockerViewEvtListener {

    private static LockerFloatLayerManager sIntance;
    //用于添加悬浮窗的管理
    private CustomWindowManager mCustomWindowManager;
    //浮窗内部解锁
    private FloatInnerAppLockerView mFloatInnerAppLockerView;
    //浮窗外部解锁
    private FloatOuterAppLockerView mFloatOuterAppLockerView;
    //当前的应用包名
    private String mCurrentPkgName;

    private LockerFloatLayerManager() {
        mCustomWindowManager = new CustomWindowManager(TheApplication.getAppContext());
    }

    /**
     * 初始化浮动窗口
     */
    private void safeToInitFloatOutterAppLockerView() {
        if (mFloatOuterAppLockerView == null) {
            mFloatOuterAppLockerView = new FloatOuterAppLockerView(TheApplication.getAppContext());
            mFloatOuterAppLockerView.setIFloatAppLockerViewEvtListener(this);
        }
    }

    private void safeToInitFloatInnerAppLockerView() {
        if (mFloatInnerAppLockerView == null) {
            mFloatInnerAppLockerView = new FloatInnerAppLockerView(TheApplication.getAppContext());
            mFloatInnerAppLockerView.setIFloatAppLockerViewEvtListener(this);
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
        safeToInitFloatOutterAppLockerView();
        mFloatOuterAppLockerView.bindViewData(mCurrentPkgName);
        mFloatOuterAppLockerView.resetPatternView();
        //获取最大的次数
        mCustomWindowManager.addView(mFloatOuterAppLockerView);
    }

    /**
     * 在外部展示悬浮窗
     */
    public void showFloatViewInSide() {
        //重置包名
        mCurrentPkgName = TheApplication.getAppContext().getPackageName();
        safeToInitFloatInnerAppLockerView();
        mFloatInnerAppLockerView.resetPatternView();
        mCustomWindowManager.addView(mFloatInnerAppLockerView);
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

    /***********
     * 浮窗View的回调函数{@link IFloatAppLockerViewEvtListener}
     **********/

    @Override
    public void onBackPress() {
        if (mFloatOuterAppLockerView != null && mFloatOuterAppLockerView.getParent() != null) {
            TheApplication.getGlobalEventBus().post(new AppLockerKillAppEvent(mCurrentPkgName));
            mCustomWindowManager.removeView(mFloatOuterAppLockerView);
        } else if (mFloatInnerAppLockerView != null && mFloatInnerAppLockerView.getParent() != null) {
            mCustomWindowManager.removeView(mFloatInnerAppLockerView);
        }
    }

    @Override
    public void onForgetClick(View v) {
        if (mFloatOuterAppLockerView != null && mFloatOuterAppLockerView.getParent() != null) {
            mCustomWindowManager.removeView(mFloatOuterAppLockerView);
        } else if (mFloatInnerAppLockerView != null && mFloatInnerAppLockerView.getParent() != null) {
            mCustomWindowManager.removeView(mFloatInnerAppLockerView);
        }
        Intent i = new Intent(TheApplication.getAppContext(), RetrievePasswordActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TheApplication.getAppContext().startActivity(i);
    }

    @Override
    public void onInputCompleted(List<PatternView.Cell> cellList, String[] numbers) {
        if (mFloatOuterAppLockerView != null && mFloatOuterAppLockerView.getParent() != null) {
            //当密码错误的时候
            if (!judgePasscode(cellList, numbers)) {
                mFloatOuterAppLockerView.delayClearErrorPattern();
            } else {
                //当密码输入正确的时候 并且已经错过很多次
                mCustomWindowManager.removeView(mFloatOuterAppLockerView);
                mFloatOuterAppLockerView.resetPatternView();
            }
        } else if (mFloatInnerAppLockerView != null && mFloatInnerAppLockerView.getParent() != null) {
            //当密码错误的时候
            if (!judgePasscode(cellList, numbers)) {
                mFloatInnerAppLockerView.delayClearErrorPattern();
            } else {
                //当密码输入正确的时候 并且已经错过很多次
                mCustomWindowManager.removeView(mFloatInnerAppLockerView);
                mFloatInnerAppLockerView.resetPatternView();
            }
        }
    }

    @Override
    public void onHomeClick() {
        if (mFloatOuterAppLockerView != null && mFloatOuterAppLockerView.getParent() != null) {
            TheApplication.getGlobalEventBus().post(new AppLockerKillAppEvent(mCurrentPkgName));
            mCustomWindowManager.removeView(mFloatOuterAppLockerView);
        } else if (mFloatInnerAppLockerView != null && mFloatInnerAppLockerView.getParent() != null) {
            mCustomWindowManager.removeView(mFloatInnerAppLockerView);
        }
    }

    /**
     * 是否正在展示
     */
    public boolean isShowingLocker() {
        return (mFloatOuterAppLockerView != null && mFloatOuterAppLockerView.getParent() != null)
                || (mFloatInnerAppLockerView != null && mFloatInnerAppLockerView.getParent() != null);
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
        mFloatOuterAppLockerView = null;
        mFloatInnerAppLockerView = null;
    }
}
