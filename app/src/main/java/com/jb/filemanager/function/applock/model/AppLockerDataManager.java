package com.jb.filemanager.function.applock.model;

import android.content.ComponentName;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.event.AppLockerDataChangedEvent;
import com.jb.filemanager.function.applock.model.bean.LockerGroup;
import com.jb.filemanager.function.applock.model.bean.LockerItem;
import com.jb.filemanager.function.applock.model.dao.LockerDao;
import com.jb.filemanager.function.applock.model.dao.LockerSecureDao;

import java.util.ArrayList;
import java.util.List;


/**
 * 应用锁数据接口
 * 包括以下数据：<br>
 * 1、锁定应用的数据
 * 2、管理安全表的数据
 *
 * @author zhanghuijun
 */
public class AppLockerDataManager {

    private static AppLockerDataManager sAppLockerDataManager = null;
    /**
     * 管理应用锁表的Dao
     */
    private LockerDao mLockerDao = null;
    /**
     * 管理安全表的Dao
     */
    private LockerSecureDao mLockerSecureDao = null;
    /**
     * 密码
     */
    private String mLockerPassword = null;

    private AppLockerDataManager() {
        mLockerDao = new LockerDao(TheApplication.getAppContext());
        mLockerSecureDao = new LockerSecureDao(TheApplication.getAppContext());
        //初始化密码
        updatePassWord();
    }

    public static AppLockerDataManager getInstance() {
        if (sAppLockerDataManager == null) {
            sAppLockerDataManager = new AppLockerDataManager();
        }
        return sAppLockerDataManager;
    }

    /**
     * 获取被锁应用的信息
     */
    public ArrayList<String> getLockAppsNamesInfo() {
        ArrayList<String> appLockerDatas = new ArrayList<>();
        ArrayList<ComponentName> specialDatas = new ArrayList<>();
        DefaultLockerFilter defaultLockerFilter = new DefaultLockerFilter();
        List<ComponentName> mDataList = mLockerDao.queryLockerInfo();
        if (null != mDataList) {
            for (ComponentName componentName : mDataList) {
                defaultLockerFilter.doFilter(appLockerDatas, specialDatas, componentName);
            }
        }
        return appLockerDatas;
    }

    /**
     * 获取应用列表锁信息 <br>
     * <B> 此方法没有异步执行 <B/>
     * 请调用者选择合适的线程进行获取数据
     */
    public LockerGroup getAppLockAppsData() {
        return mLockerDao.getLockerInfos();
    }

    /**
     * 获取推荐需要加锁的信息列表 <br>
     * <B> 此方法没有异步执行 <B/>
     * 请调用者选择合适的线程进行获取数据
     */
    public List<LockerItem> getRecommendLockAppDatas() {
        return mLockerDao.getRecommendLockerData();
    }


    /**
     * 获取应用列表锁信息
     */
    public List<LockerItem> getAppLockInfos() {
        final LockerGroup lockerGroup = mLockerDao.getLockerInfos();
        if (lockerGroup != null) {
            return lockerGroup.getLockerItems();
        }
        return null;
    }

    /**
     * 更新密码 获取数据库中的密码
     */
    public void updatePassWord() {
        mLockerPassword = queryLockerPassWord();
    }

    /**
     * 获取应用锁密码
     */
    private String queryLockerPassWord() {
        return mLockerSecureDao.getLockerPassWord();
    }

    /**
     * 修改应用锁密码
     */
    public void modifyLockerPassword(final String password, boolean isPatternPsd) {
        mLockerSecureDao.modifyLockerPassword(password);
        mLockerSecureDao.modifylockerPsdType(isPatternPsd);
    }

    /**
     * 保存问题题目
     */
    public void saveLockerQuestion(String question) {
        mLockerSecureDao.saveLockerSecureQuestionName(question);
    }

    /**
     * 保存问题题目
     */
    public String getLockerQuestion() {
        return mLockerSecureDao.getLockerSecureQuestionName();
    }

    /**
     * 保存问题题目
     */
    public String getLockerAnswer() {
        return mLockerSecureDao.getLockerSecureQuestionResult();
    }

    /**
     * 保存问题题目
     */
    public void saveLockerAnswer(String answer) {
        mLockerSecureDao.saveLockerSecureQuestionResult(answer);
    }

    /**
     * 加锁一组选项
     */
    public void lockItem(final LockerItem... lockerItems) {
        mLockerDao.lockItem(lockerItems);
        // 通知更新
        TheApplication.getGlobalEventBus().post(new AppLockerDataChangedEvent());
    }

    /**
     * 解锁一组选项
     */
    public void unlockItem(final LockerItem... lockerItems) {
        mLockerDao.unlockItem(lockerItems);
        // 通知更新
        TheApplication.getGlobalEventBus().post(new AppLockerDataChangedEvent());
    }

    /**
     * 解锁一组选项
     */
    public void unlockItem(final String packageName) {
        mLockerDao.unlockItem(packageName);
        // 通知更新
        TheApplication.getGlobalEventBus().post(new AppLockerDataChangedEvent());
    }

    /**
     * 查看是否是图案密码
     * */
    public boolean isPatternPsd() {
        return mLockerSecureDao.isPatternPsd();
    }

    public String getLockerPassword() {
        return mLockerPassword;
    }

    /**
     * 是否存在锁定应用
     * */
    public boolean isHaveLockerApp() {
        return mLockerDao.isHaveLockerApp();
    }

    public void onDestory() {
        sAppLockerDataManager = null;
    }
}
