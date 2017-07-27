package com.jb.filemanager.function.rate;

import android.support.annotation.IntDef;

import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.provider.RateTriggeringFactorProvider;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.NetworkUtil;
import com.jb.filemanager.util.TimeUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by nieyh on 17-7-19.
 * 评分管理者 <br/>
 * 用于判断是否满足评分弹出条件
 */

public class RateManager {

    private final String TAG = "RateManager";

    private static RateManager sInstance;

    private RateManager() {}

    public static RateManager getsInstance() {
        if (sInstance == null) {
            sInstance = new RateManager();
        }
        return sInstance;
    }

    /********************************触发条件相关逻辑********************************/
    /***
     * <ol>
     *     <li>{@link #CLEAN_FINISH} 成功清理</li>
     *     <li>{@link #STORAGE_SUB_PAGE} 进入存储子页面</li>
     *     <li>{@link #FILE_OPERATE} 文件操作 (底部栏的操作)</li>
     *     <li>{@link #SEARCH_RESULT_CLICK} 搜索结果页选项被选中</li>
     *     <li>{@link #APPLOCK_ENTER} 进入应用锁列表页</li>
     * </ol>
     * */
    public static final int CLEAN_FINISH = 1;
    public static final int STORAGE_SUB_PAGE = 2;
    public static final int FILE_OPERATE = 3;
    public static final int SEARCH_RESULT_CLICK = 4;
    public static final int APPLOCK_ENTER = 5;

    @IntDef({CLEAN_FINISH, STORAGE_SUB_PAGE, FILE_OPERATE, SEARCH_RESULT_CLICK, APPLOCK_ENTER})
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface FactorType {}
    //每一次触发间隔时间 = 30分钟
    private final long INTERVAL_TIME = 30 * 60 * 1000;

    /**
     * 收集评分触发因素
     * @param type 触发因素
     * */
    public void collectTriggeringFactor(@FactorType int type) {
        RateTriggeringFactorProvider rateTriggeringFactorProvider
                = new RateTriggeringFactorProvider(TheApplication.getAppContext());

        long date = rateTriggeringFactorProvider.getLastTriggerDate(type);
        boolean isReachIntervalTime = false;
        if ((System.currentTimeMillis() - date) >= INTERVAL_TIME) {
            //当时间间隔超过指定数值 则可以更新触发条件
            isReachIntervalTime = true;
        }
        if (isReachIntervalTime) {
            Logger.w(RateTriggeringFactorProvider.TAG, "isReachIntervalTime >> " + isReachIntervalTime);
            rateTriggeringFactorProvider.updateTrigger(type);
        }
    }

    // 是否满足引导条件
    public boolean isReachTriggerCondition() {
        Logger.w(TAG, "isReachTriggerCondition");
        //评分成功则不再触发
        if (isRateSuccess()) {
            return false;
        }
        Logger.w(TAG, "isRateSuccess >> false");
        //没安装GP
        if (!isGpAppExist()) {
            return false;
        }
        Logger.w(TAG, "isGpAppExist >> true");
        //没有网络
        if (!isNetworkConnection()) {
            return false;
        }
        Logger.w(TAG, "isNetworkConnection >> true");

        //安装时间小于八小时
        if (!isInstallTimeOver8Hours()) {
            return false;
        }
        Logger.w(TAG, "isInstallTimeOver8Hours >> true");
        int guideTimes;
        //评分引导次数 大于两次
        if ((guideTimes = getRateGuideTimes()) > 1) {
            return false;
        }
        Logger.w(TAG, "getRateGuideTimes >> " + guideTimes + " < 1");
        //上一次评分时间距离现在少于两天
        if (!isLastRateOver2Days()) {
            return false;
        }
        Logger.w(TAG, "isLastRateOver2Days >> true ");

        RateTriggeringFactorProvider rateTriggeringFactorProvider
                = new RateTriggeringFactorProvider(TheApplication.getAppContext());

        boolean isReachTriggerCondition = rateTriggeringFactorProvider.isTriggerCounterOverSomeTimes(3,
                CLEAN_FINISH,
                STORAGE_SUB_PAGE,
                FILE_OPERATE,
                SEARCH_RESULT_CLICK) || rateTriggeringFactorProvider.isTriggerCounterOverSomeTimes(2, APPLOCK_ENTER);

        Logger.w(TAG, "isReachTriggerCondition >> " + isReachTriggerCondition);
        if (isReachTriggerCondition) {
            //增加一次引导次数
            commitRateGuideTimes(++ guideTimes);
            //更新最近一次引导评论的时间
            commitLastRateDate(System.currentTimeMillis());
        }
        return isReachTriggerCondition;
    }

    //设置评分成功
    public void commitRateSuccess() {
        SharedPreferencesManager.getInstance(TheApplication.getAppContext()).commitBoolean(IPreferencesIds.KEY_RATE_SUCCESS, true);
    }

    //安装时间超过8小时
    private boolean isInstallTimeOver8Hours() {
        long installTime = getFirstInstallTime();
        if ((System.currentTimeMillis() - installTime) > 8 * 60 * 60 * 1000) {
            return true;
        }
        return false;
    }

    //上次评分时间离现在超过两天
    private boolean isLastRateOver2Days() {
        long rateDate = getLastRateDate();
        int days = 0;
        try {
            days = TimeUtil.calcDifferenceDays(rateDate, System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //第三天或者之后
        return days >= 2;
    }

    //是否Google play已经安装
    private boolean isGpAppExist() {
        return AppUtils.isAppExist(TheApplication.getAppContext(), Const.GP_PACKAGE);
    }

    //存在网络连接
    private boolean isNetworkConnection() {
        return NetworkUtil.isNetworkOK(TheApplication.getAppContext());
    }

    //评分是否成功
    private boolean isRateSuccess() {
        return SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getBoolean(IPreferencesIds.KEY_RATE_SUCCESS, false);
    }

    //安装时间
    private long getFirstInstallTime() {
        return SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getLong(IPreferencesIds.KEY_FIRST_INSTALL_TIME, System.currentTimeMillis());
    }

    //评分引导次数
    private int getRateGuideTimes() {
        return SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getInt(IPreferencesIds.KEY_RATE_GUIDE_TIMES, 0);
    }

    //更新评分引导的次数
    private void commitRateGuideTimes(int times) {
        SharedPreferencesManager.getInstance(TheApplication.getAppContext()).commitInt(IPreferencesIds.KEY_RATE_GUIDE_TIMES, times);
    }

    //上一次评分的时间
    private long getLastRateDate() {
        return SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getLong(IPreferencesIds.KEY_LAST_TIME, 0);
    }

    //更新上一次评分的时间
    private void commitLastRateDate(long date) {
        SharedPreferencesManager.getInstance(TheApplication.getAppContext()).commitLong(IPreferencesIds.KEY_LAST_TIME, date);
    }

}
