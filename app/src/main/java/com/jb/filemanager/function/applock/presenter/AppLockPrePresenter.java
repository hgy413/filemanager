package com.jb.filemanager.function.applock.presenter;

import android.text.TextUtils;

import com.jb.filemanager.function.applock.model.bean.LockerItem;
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by nieyh on 2016/12/30.
 */

public class AppLockPrePresenter implements AppLockPreContract.Presenter {

    private final String TAG = "AppLockPrePresenter";

    /**
     * 推荐需要加锁的应用列表
     */
    private List<LockerItem> mRecommedLockItems;

    /**
     * 所有应用的列表
     */
    private List<LockerItem> mAllLockItems;
    private AppLockPreContract.View mView;
    private AppLockPreContract.Support mSupport;

    private String mPasscode, mQuestion, mAnswer;
    private boolean isPatternPsd = true;

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
                    int size = 0;
                    if (mRecommedLockItems != null) {
                        for (int i = 0; i < mAllLockItems.size(); i++) {
                            for (int j = 0; j < mRecommedLockItems.size(); j++) {
                                if (mRecommedLockItems.get(j).mPackageName.equals(mAllLockItems.get(i).mPackageName)) {
                                    // 是推荐的
                                    mAllLockItems.get(i).isChecked = true;
                                    size++;
                                    break;
                                }
                            }
                        }
                    }
                    sortForCheckState(mAllLockItems);
                    final int defaultRecommedSize = size;
                    mSupport.toUiWork(new Runnable() {
                        @Override
                        public void run() {
                            // 筛选默认选择的应用
                            mView.showDefaultRecommendAppsNum(defaultRecommedSize);
                            mView.showAppDatas(mAllLockItems);
                            mView.showDataLoadFinish();
                            if (defaultRecommedSize != 0) {
                                mView.showButtonEnableToLock();
                            } else {
                                mView.showButtonDisEnable();
                            }
                        }
                    }, 0);
                }
            });
        }
    }

    /**
     * 排序，选中的放前面，其他按照原本排序
     */
    private void sortForCheckState(List<LockerItem> lockerItems) {
        Collections.sort(lockerItems, new Comparator<LockerItem>() {

            @Override
            public int compare(LockerItem lhs, LockerItem rhs) {
                if (lhs.isChecked) {
                    if (rhs.isChecked) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    if (rhs.isChecked) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });
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
            mSupport.release();
        }
    }

    @Override
    public void search(String keyWord) {
        if (mView != null) {
            if (TextUtils.isEmpty(keyWord)) {
                mView.showAppDatas(mAllLockItems);
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
                                searchStartPos ++;
                            }
                        }
                        if (searchStartPos != -1) {
                            Logger.w(TAG, title);
                            tempSearchList.add(lockerItem);
                        }
                    }
                }
                mView.showAppDatas(tempSearchList);
            }
        }
    }

    @Override
    public void refreshOperateButState() {
        if (mView != null) {
            if (mAllLockItems == null || mAllLockItems.size() == 0) {
                mView.showButtonDisEnable();
            } else {
                boolean isEnable = true;
                for (LockerItem lockerItem : mAllLockItems) {
                    isEnable = lockerItem.isChecked;
                    if (isEnable) {
                        break;
                    }
                }
                if (isEnable) {
                    mView.showButtonEnableToLock();
                } else {
                    mView.showButtonDisEnable();
                }
            }
        }
    }

    @Override
    public void cacheInitInfo(boolean isPatternPsd, String passcode, String answer, String question) {
        mPasscode = passcode;
        mAnswer = answer;
        mQuestion = question;
        this.isPatternPsd = isPatternPsd;
        //当没有权限的时候需要获取权限
        if (mView != null) {
            if (mSupport != null && !mSupport.isHaveUsageStatePremisstion()) {
                mView.showPermisstionGuideView();
            } else {
                mSupport.saveLockerInfo(mAllLockItems);
                mSupport.savePasscodeAndQuestion(isPatternPsd, mPasscode, mQuestion, mAnswer);
                mSupport.setLockerEnable();
                mView.gotoAppLockerView();
            }
        }
    }

    @Override
    public boolean isShouldShowBackTipDialog() {
        if (mSupport != null) {
            return mSupport.isBackTipDialogPop();
        }
        return false;
    }

    @Override
    public void dealResume() {
        if (!TextUtils.isEmpty(mPasscode) && mSupport.isHaveUsageStatePremisstion()) {
            mSupport.removeUiWork(mDelayCheckWork);
            mSupport.removeUiWork(mCheckTimeOutWork);
            mSupport.saveLockerInfo(mAllLockItems);
            mSupport.savePasscodeAndQuestion(isPatternPsd, mPasscode, mQuestion, mAnswer);
            mSupport.setLockerEnable();
            mView.gotoAppLockerView();
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
                    mSupport.savePasscodeAndQuestion(isPatternPsd, mPasscode, mQuestion, mAnswer);
                    mSupport.setLockerEnable();
                    mView.gotoAppLockerView();
                } else {
                    mSupport.toUiWork(mDelayCheckWork, CHECK_TIME);
                }
            }
        }
    };
}
