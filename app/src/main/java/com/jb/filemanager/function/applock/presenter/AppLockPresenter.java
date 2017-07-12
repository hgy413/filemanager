package com.jb.filemanager.function.applock.presenter;

import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.applock.model.bean.AppLockGroupData;
import com.jb.filemanager.function.applock.model.bean.LockerItem;
import com.jb.filemanager.util.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by nieyh on 2017/1/3.
 */

public class AppLockPresenter implements AppLockContract.Presenter {

    private AppLockContract.View mView;
    private AppLockContract.Support mSupport;
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
    private final String TAG = "AppLockPresenter";

    public AppLockPresenter(AppLockContract.View mView, AppLockContract.Support mSupport) {
        this.mView = mView;
        this.mSupport = mSupport;
    }


    @Override
    public void loadData() {
        mView.showDataLoading();
        if (mSupport != null) {
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
                    mView.showLockAppsNum(defaultRecommedSize);
                    mView.showAppLockGroupData(mAppLockGroupDataList);
                    mView.showDataLoaded();
                }
            }, 0);
        }
    }

    @Override
    public void search(String keyWord) {
        if (mView != null) {
            if (TextUtils.isEmpty(keyWord)) {
                mView.showAppLockGroupData(mAppLockGroupDataList);
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
                mView.showAppLockGroupData(mSearchResultGroupDataList);
            }
        }
    }

    @Override
    public void release() {
        if (mSupport != null) {
            mSupport = null;
        }
        if (mView != null) {
            mView = null;
        }
    }

    @Override
    public void dealUpdateLockerInfo() {
        if (mSupport != null) {
            List<LockerItem> lockerSelectBeans = new ArrayList<>();
            List<LockerItem> unLockerSelectBeans = new ArrayList<>();
            if (mAllLockItems == null || mAllLockItems.size() == 0) {
                return;
            }
            for (LockerItem lockerItem : mAllLockItems) {
                if (lockerItem.isChecked) {
                    lockerSelectBeans.add(lockerItem);
                } else {
                    unLockerSelectBeans.add(lockerItem);
                }
            }
            mSupport.saveLockerInfo(lockerSelectBeans, unLockerSelectBeans);
            if (mView != null) {
                mView.showLockAppsNum(lockerSelectBeans.size());
            }
            if (lockerSelectBeans.size() > 0) {
                //打开
                mSupport.startAppLockerMonitor();
            } else {
                //关闭
                mSupport.stopAppLockerMonitor();
            }
        }
    }
}
