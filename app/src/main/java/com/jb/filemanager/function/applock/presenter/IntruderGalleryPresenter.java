package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.applock.event.AntiPeepAllUpdateDoneEvent;
import com.jb.filemanager.function.applock.event.AppLockImageDeleteEvent;
import com.jb.filemanager.function.applock.event.AppLockImageReadEvent;
import com.jb.filemanager.function.applock.manager.AntiPeepDataManager;
import com.jb.filemanager.function.applock.model.bean.AntiPeepBean;
import com.jb.filemanager.function.applock.model.bean.IntruderDisplayBean;
import com.jb.filemanager.function.applock.model.bean.IntruderDisplaySubBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by nieyh on 2017/1/4.
 */

public class IntruderGalleryPresenter implements IntruderGalleryContract.Presenter {

    private ITaskSupport mITaskSupport;

    private IntruderGalleryContract.View mView;

    private List<AntiPeepBean> mAllPhoto;

    private List<IntruderDisplayBean> mIntruderDisplayBeanList = new ArrayList<>();

    private IOnEventMainThreadSubscriber<AntiPeepAllUpdateDoneEvent> mAntiPeepAllUpdateDoneEvtSubscriber = new IOnEventMainThreadSubscriber<AntiPeepAllUpdateDoneEvent>() {
        @Override
        @Subscribe
        public void onEventMainThread(AntiPeepAllUpdateDoneEvent event) {
            mAllPhoto = AntiPeepDataManager.getInstance(TheApplication.getAppContext()).getAllPhotoAfterUpdate();
            onDataLoaded(mAllPhoto);
            TheApplication.getGlobalEventBus().unregister(this);
        }
    };

    private IOnEventMainThreadSubscriber<AppLockImageReadEvent> mAppLockImageReadEvtSubscriber = new IOnEventMainThreadSubscriber<AppLockImageReadEvent>() {
        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(AppLockImageReadEvent event) {
            dealDataReaded(event.getPath());
        }
    };

    private IOnEventMainThreadSubscriber<AppLockImageDeleteEvent> mAppLockImageDeleteEvtSubscriber = new IOnEventMainThreadSubscriber<AppLockImageDeleteEvent>() {
        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(AppLockImageDeleteEvent event) {
            dealDataDelete(event.getPath());
        }
    };

    public IntruderGalleryPresenter(IntruderGalleryContract.View mView) {
        this.mView = mView;
        this.mITaskSupport = new TaskSupportImpl();
    }

    @Override
    public void start() {
        if (!TheApplication.getGlobalEventBus().isRegistered(mAntiPeepAllUpdateDoneEvtSubscriber)) {
            TheApplication.getGlobalEventBus().register(mAntiPeepAllUpdateDoneEvtSubscriber);
        }
        if (!TheApplication.getGlobalEventBus().isRegistered(mAppLockImageReadEvtSubscriber)) {
            TheApplication.getGlobalEventBus().register(mAppLockImageReadEvtSubscriber);
        }
        if (!TheApplication.getGlobalEventBus().isRegistered(mAppLockImageDeleteEvtSubscriber)) {
            TheApplication.getGlobalEventBus().register(mAppLockImageDeleteEvtSubscriber);
        }
        if (mView != null) {
            mView.showDataLoading();
        }
        AntiPeepDataManager.getInstance(TheApplication.getAppContext()).updateAllPhoto();
    }

    /**
     * 处理加载数据
     */
    private void onDataLoaded(List<AntiPeepBean> allPhoto) {
        if (mView != null && mITaskSupport != null) {
            if (allPhoto == null || allPhoto.size() == 0) {
                mITaskSupport.toUiWork(new Runnable() {
                    @Override
                    public void run() {
                        mView.showNoDataLayout();
                    }
                }, 0);
            } else {
                // 更正创建时间
                for (AntiPeepBean bean : allPhoto) {
                    String path = bean.getPath();
                    String createTime = path.substring(path.lastIndexOf("/") + 1, path.length() - 4);
                    bean.setCreateTime(Long.valueOf(createTime));
                }

                //逆序排序
                sortPhoto(allPhoto);

                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                Calendar calendar = Calendar.getInstance();
                ArrayList<IntruderDisplaySubBean> unSortList = new ArrayList<>();

                for (AntiPeepBean bean : allPhoto) {
                    String path = bean.getPath();
                    String createTime = path.substring(path.lastIndexOf("/") + 1, path.length() - 4);
                    calendar.setTimeInMillis(Long.parseLong(createTime));
                    IntruderDisplaySubBean tempBean = new IntruderDisplaySubBean(path, formatter.format(calendar.getTime()));
                    tempBean.setIsReaded(bean.isRead());
                    unSortList.add(tempBean);
                }

                for (IntruderDisplaySubBean subBean : unSortList) {
                    boolean isAdded = false;
                    // 没有添加的则添加到该队列
                    for (IntruderDisplayBean bean : mIntruderDisplayBeanList) {
                        if (bean.getTimeTitle().equals(subBean.getDate())) {
                            bean.getChildren().add(subBean);
                            isAdded = true;
                        }
                    }
                    if (!isAdded) {
                        IntruderDisplayBean tempBean = new IntruderDisplayBean(subBean.getDate(), new ArrayList<IntruderDisplaySubBean>());
                        tempBean.getChildren().add(subBean);
                        mIntruderDisplayBeanList.add(tempBean);
                    }
                }
                mITaskSupport.toUiWork(new Runnable() {
                   @Override
                   public void run() {
                       mView.showGalleryData(mIntruderDisplayBeanList);
                   }}, 0);
            }
            mITaskSupport.toUiWork(new Runnable() {
                @Override
                public void run() {
                    mView.showDataLoaded();
                }
            }, 0);
        }
    }

    /**
     * 排序所有图片
     */
    private void sortPhoto(List<AntiPeepBean> allPhoto) {
        Collections.sort(allPhoto, new Comparator<AntiPeepBean>() {
            @Override
            public int compare(AntiPeepBean lhs, AntiPeepBean rhs) {
                if (lhs.getCreateTime() < rhs.getCreateTime()) {
                    return 1;
                }
                if (lhs.getCreateTime() > rhs.getCreateTime()) {
                    return -1;
                }
                return 0;
            }
        });
    }

    /**
     * 处理数据删除
     */
    private void dealDataDelete(String path) {
        if (mView != null) {
            IntruderDisplayBean deleteBean = null;
            IntruderDisplaySubBean deleteSubBean = null;
            for (IntruderDisplayBean bean : mIntruderDisplayBeanList) {
                boolean shuoldBreak = false;
                for (IntruderDisplaySubBean subBean : bean.getChildren()) {
                    if (subBean.getPath().equals(path)) {
                        deleteBean = bean;
                        deleteSubBean = subBean;
                        shuoldBreak = true;
                        break;
                    }
                }
                if (shuoldBreak) {
                    if (deleteSubBean != null) {
                        bean.getChildren().remove(deleteSubBean);
                        AntiPeepBean deletedAntiPeedBean = null;
                        for (AntiPeepBean antiPeedBean : mAllPhoto) {
                            if (antiPeedBean.getPath().equals(deleteSubBean.getPath())) {
                                AntiPeepDataManager.getInstance(TheApplication.getAppContext()).deletePhoto(antiPeedBean);
                                deletedAntiPeedBean = antiPeedBean;
                                break;
                            }
                        }
                        if (deletedAntiPeedBean != null) {
                            mAllPhoto.remove(deletedAntiPeedBean);
                        }
                    }
                    break;
                }
            }
            if (deleteBean != null && deleteBean.getChildren().size() == 0) {
                mIntruderDisplayBeanList.remove(deleteBean);
            }
            if (mIntruderDisplayBeanList.size() == 0) {
                mView.showNoDataLayout();
            } else {
                mView.showGalleryData(mIntruderDisplayBeanList);
            }
        }
    }

    /**
     * 处理数据阅读
     */
    private void dealDataReaded(String path) {
        for (IntruderDisplayBean bean : mIntruderDisplayBeanList) {
            boolean shouldBreak = false;
            for (IntruderDisplaySubBean subBean : bean.getChildren()) {
                if (subBean.getPath().equals(path)) {
                    subBean.setIsReaded(true);
                    shouldBreak = true;
                }
            }
            if (shouldBreak) {
                break;
            }
        }
        if (mView != null) {
            //刷新界面
            mView.showGalleryData(mIntruderDisplayBeanList);
        }
    }

    @Override
    public void release() {
        if (TheApplication.getGlobalEventBus().isRegistered(mAntiPeepAllUpdateDoneEvtSubscriber)) {
            TheApplication.getGlobalEventBus().unregister(mAntiPeepAllUpdateDoneEvtSubscriber);
        }
        if (TheApplication.getGlobalEventBus().isRegistered(mAppLockImageReadEvtSubscriber)) {
            TheApplication.getGlobalEventBus().unregister(mAppLockImageReadEvtSubscriber);
        }
        if (TheApplication.getGlobalEventBus().isRegistered(mAppLockImageDeleteEvtSubscriber)) {
            TheApplication.getGlobalEventBus().unregister(mAppLockImageDeleteEvtSubscriber);
        }
        // 设置目前的入侵者图片都已读，下次不再弹出
        AntiPeepDataManager.getInstance(TheApplication.getAppContext()).setAllPhotoRead();
        mITaskSupport.release();
    }
}
