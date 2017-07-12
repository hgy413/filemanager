package com.jb.filemanager.function.applock.presenter;

import android.text.TextUtils;

import com.jb.filemanager.function.applock.model.bean.AppLockGroupData;
import com.jb.filemanager.function.applock.model.bean.LockerItem;
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by nieyh on 2016/12/30.
 */

public class AppLockPrePresenter implements AppLockPreContract.Presenter {

    private final String TAG = "AppLockPrePresenter";

    //推荐需要加锁的应用列表
    private List<LockerItem> mRecommedLockItems;
    //所有应用的列表
    private List<LockerItem> mAllLockItems;
    //除去推荐的其他列表
    private List<LockerItem> mOtherLockItems;
    //分组数据
    private List<AppLockGroupData> mAppLockGroupDataList;
    //搜索结果分组
    private List<AppLockGroupData> mSearchResultGroupDataList;
    private AppLockPreContract.View mView;
    private AppLockPreContract.Support mSupport;

    private String mPasscode, mQuestion, mAnswer;
    private boolean isLockForLeave;

    public AppLockPrePresenter(AppLockPreContract.View view, AppLockPreContract.Support support) {
        this.mView = view;
        this.mSupport = support;
    }

    @Override
    public void loadData() {
        mView.showDataLoading();
        if (mSupport != null) {
            mSupport.toAsynWork(new Runnable() {
                @Override
                public void run() {
                    mAllLockItems = mSupport.getAppLockAppDatas();
                    mRecommedLockItems = mSupport.getRecommedAppDatas();
                    mOtherLockItems = new ArrayList<>();
                    int size = 0;
                    if (mRecommedLockItems != null) {
                        for (int j = 0; j < mRecommedLockItems.size(); j++) {
                            LockerItem lockerItem = mRecommedLockItems.get(j);
                            lockerItem.isChecked = true;
                            size++;
                        }
                    }
                    Iterator<LockerItem> itemIterator = mAllLockItems.iterator();
                    while (itemIterator.hasNext()) {
                        LockerItem lockerItem = itemIterator.next();
                        if (!mRecommedLockItems.contains(lockerItem)) {
                            mOtherLockItems.add(lockerItem);
                        }
                    }
                    mAllLockItems.clear();
                    mAllLockItems.addAll(mRecommedLockItems);
                    mAllLockItems.addAll(mOtherLockItems);
                    mAppLockGroupDataList = new ArrayList<>();
                    String[] groupTitleArray = null;
                    if (mSupport != null) {
                        groupTitleArray = mSupport.getFloatListGroupTitle();
                    }
                    if (groupTitleArray != null && groupTitleArray.length >= 2) {
                        mAppLockGroupDataList.add(new AppLockGroupData(mRecommedLockItems, groupTitleArray[0]));
                        mAppLockGroupDataList.add(new AppLockGroupData(mOtherLockItems, groupTitleArray[1]));
                    }
                    final int defaultRecommedSize = size;
                    mSupport.toUiWork(new Runnable() {
                        @Override
                        public void run() {
                            // 筛选默认选择的应用
                            mView.showDefaultRecommendAppsNum(defaultRecommedSize);
                            mView.showAppDatas(mAppLockGroupDataList);
                            mView.showDataLoadFinish();
                        }
                    }, 0);
                }
            });
        }
    }

    @Override
    public void release() {
        if (mAllLockItems != null) {
            mAllLockItems.clear();
        }
        if (mRecommedLockItems != null) {
            mRecommedLockItems.clear();
        }
        if (mSupport != null) {
            mView = null;
            mSupport = null;
        }
    }

    @Override
    public void search(String keyWord) {
        if (mView != null) {
            if (TextUtils.isEmpty(keyWord)) {
                mView.showAppDatas(mAppLockGroupDataList);
            } else {
                /**
                 * 搜索描述：<br/>
                 * 1、搜索关键字长度必须小于名字长度
                 * 2、从搜索位置（默认为 0） 连续向后搜索 如果字符都存在则为正确
                 * */
                keyWord = keyWord.toLowerCase(Locale.US);
                List<LockerItem> tempSearchList = new ArrayList<>();
                for (LockerItem lockerItem : mAllLockItems) {
                    String title = lockerItem.getTitle().toLowerCase(Locale.US);
                    if (keyWord.length() <= title.length()) {
                        int searchStartPos = 0;
                        for (int i = 0; i < keyWord.length(); i++) {
                            searchStartPos = title.indexOf(keyWord.charAt(i), searchStartPos);
                            if (searchStartPos == -1) {
                                break;
                            } else {
                                searchStartPos++;
                            }
                        }
                        if (searchStartPos != -1) {
                            Logger.w(TAG, title);
                            tempSearchList.add(lockerItem);
                        }
                    }
                }
                if (mSearchResultGroupDataList == null) {
                    mSearchResultGroupDataList = new ArrayList<>();
                } else {
                    mSearchResultGroupDataList.clear();
                }
                String[] groupTitleArray = null;
                if (mSupport != null) {
                    groupTitleArray = mSupport.getFloatListGroupTitle();
                }
                if (tempSearchList.size() > 0) {
                    if (groupTitleArray != null && groupTitleArray.length >= 3) {
                        mSearchResultGroupDataList.add(new AppLockGroupData(tempSearchList, groupTitleArray[2]));
                    }
                } else {
                    if (groupTitleArray != null && groupTitleArray.length >= 4) {
                        mSearchResultGroupDataList.add(new AppLockGroupData(tempSearchList, groupTitleArray[3]));
                    }
                }
                mView.showAppDatas(mSearchResultGroupDataList);
            }
        }
    }

    @Override
    public void cacheInitInfo(String passcode, String answer, String question, boolean isLockForLeave) {
        mPasscode = passcode;
        mAnswer = answer;
        mQuestion = question;
        this.isLockForLeave = isLockForLeave;
        //当没有权限的时候需要获取权限
        if (mView != null) {
            if (mSupport != null && !mSupport.isHaveUsageStatePremisstion()) {
                mView.showPermisstionGuideView();
            } else {
                mSupport.saveLockerInfo(mAllLockItems);
                mSupport.savePasscodeAndQuestion(true, mPasscode, mQuestion, mAnswer, isLockForLeave);
                mSupport.setLockerEnable();
                mView.gotoAppLockerView();
            }
        }
    }

    @Override
    public void dealResume() {
        if (!TextUtils.isEmpty(mPasscode) && mSupport.isHaveUsageStatePremisstion()) {
            mSupport.removeUiWork(mDelayCheckWork);
            mSupport.removeUiWork(mCheckTimeOutWork);
            mSupport.saveLockerInfo(mAllLockItems);
            mSupport.savePasscodeAndQuestion(true, mPasscode, mQuestion, mAnswer, isLockForLeave);
            mSupport.setLockerEnable();
            mView.gotoAppLockerView();
        }
    }

    @Override
    public void handleOperateClick() {
        // TODO: 17-7-11 操作按钮点击
        if (mView != null) {
            mView.gotoSetPsdView();
        }
    }

    @Override
    public void dealPermissionCheck() {
        if (mSupport != null) {
            mSupport.toUiWork(mDelayCheckWork, CHECK_TIME);
            mSupport.toUiWork(mCheckTimeOutWork, TIMEOUT_TIME);
        }
    }

    private final long CHECK_TIME = 500;
    private final long TIMEOUT_TIME = 60000;

    private Runnable mCheckTimeOutWork = new Runnable() {
        @Override
        public void run() {
            if (mSupport != null) {
                mSupport.removeUiWork(mDelayCheckWork);
                mSupport.removeUiWork(mCheckTimeOutWork);
            }
        }
    };

    private Runnable mDelayCheckWork = new Runnable() {
        @Override
        public void run() {
            if (mSupport != null && mView != null) {
                if (mSupport.isHaveUsageStatePremisstion()) {
                    mSupport.removeUiWork(mDelayCheckWork);
                    mSupport.removeUiWork(mCheckTimeOutWork);
                    mSupport.saveLockerInfo(mAllLockItems);
                    mSupport.savePasscodeAndQuestion(true, mPasscode, mQuestion, mAnswer, isLockForLeave);
                    mSupport.setLockerEnable();
                    mView.gotoAppLockerView();
                } else {
                    mSupport.toUiWork(mDelayCheckWork, CHECK_TIME);
                }
            }
        }
    };
}
