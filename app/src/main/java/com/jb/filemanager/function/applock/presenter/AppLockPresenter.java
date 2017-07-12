package com.jb.filemanager.function.applock.presenter;

import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.applock.model.bean.AppLockGroupData;
import com.jb.filemanager.function.applock.model.bean.LockerItem;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by nieyh on 2017/1/3.
 */

public class AppLockPresenter implements AppLockContract.Presenter {

    private AppLockContract.View mView;

    private AppLockContract.Support mSupport;
    /**
     * 所有应用的列表
     */
    private List<LockerItem> mAllLockItems;
    private final String TAG = "AppLockPresenter";
    private int mPhotoSize = 0;

    public AppLockPresenter(AppLockContract.View mView, AppLockContract.Support mSupport) {
        this.mView = mView;
        this.mSupport = mSupport;
    }


    @Override
    public void loadData() {
        mView.showDataLoading();
        if (mSupport != null) {
            //获取图片数目
            mSupport.updateIntruderPhoto();
            final String appGroupName = mSupport.getAppLockGroupName();
            //获取锁信息
            mSupport.toAsynWork(new Runnable() {
                @Override
                public void run() {
                    mAllLockItems = mSupport.getAppLockAppDatas();
                    sortForCheckState(mAllLockItems);
                    AppLockGroupData appLockGroupData = new AppLockGroupData(mAllLockItems, appGroupName);
                    boolean isAllLocked = true;
                    boolean isSelected = false;
                    for (int i = 0; i < mAllLockItems.size(); i++) {
                        if (!mAllLockItems.get(i).isChecked) {
                            isAllLocked = false;
                            break;
                        } else {
                            isSelected = true;
                        }
                    }
                    appLockGroupData.setAllChecked(isAllLocked);
                    final List<AppLockGroupData> lockGroupDatas = new ArrayList<>(1);
                    final boolean isEnable = isSelected;
                    lockGroupDatas.add(appLockGroupData);
                    mSupport.toUiWork(new Runnable() {
                        @Override
                        public void run() {
                            boolean isOpen = mSupport.getIntruderSwitcherState();
                            if (isOpen) {
                                mView.showIntruderTipOpened();
                            } else {
                                mView.showIntruderTipClosed();
                            }
                            // 筛选默认选择的应用
                            mView.showAppLockGroupData(lockGroupDatas);
                            mView.showDataLoaded();
                        }
                    }, 0);
                }
            });
        }
    }

    @Override
    public void search(String keyWord) {
        if (mView != null) {
            if (TextUtils.isEmpty(keyWord)) {
                AppLockGroupData appLockGroupData = new AppLockGroupData(mAllLockItems, mSupport.getAppLockGroupName());
                List<AppLockGroupData> lockGroupDatas = new ArrayList<>(1);
                lockGroupDatas.add(appLockGroupData);
                mView.showAppLockGroupData(lockGroupDatas);
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
                            tempSearchList.add(lockerItem);
                        }
                    }
                }
                AppLockGroupData appLockGroupData = new AppLockGroupData(tempSearchList, mSupport.getAppLockGroupName());
                List<AppLockGroupData> lockGroupDatas = new ArrayList<>(1);
                lockGroupDatas.add(appLockGroupData);
                mView.showAppLockGroupData(lockGroupDatas);
            }
        }
    }

    @Override
    public void release() {
        if (mSupport != null) {
            mSupport.release();
        }
        if (mView != null) {
            mView = null;
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
    public void refreshOperateButState() {
        if (mView != null) {
            if (mAllLockItems != null && mAllLockItems.size() != 0) {
                boolean isEnable = true;
                for (LockerItem lockerItem : mAllLockItems) {
                    isEnable = lockerItem.isChecked;
                    if (isEnable) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void dealiIntruderEntranceOnclick() {
        if (mSupport != null) {
            if (mView != null && !mSupport.getIntruderSwitcherState()) {
                mView.showIntruderTipDialog();
            } else {
                mView.gotoIntruderVertGallery();
            }
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
