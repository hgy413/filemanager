package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.applock.event.AntiPeepUnreadUpdateDoneEvent;
import com.jb.filemanager.function.applock.event.AppLockImageDeleteEvent;
import com.jb.filemanager.function.applock.model.bean.AntiPeepBean;
import com.jb.filemanager.function.applock.model.bean.IntruderDisplaySubBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by nieyh on 2017/1/6.
 */

public class IntruderHoriGalleryPersenter implements IntruderHoriGalleryContract.Persenter {

    private IntruderHoriGalleryContract.View mView;

    private IntruderHoriGalleryContract.Support mSupport;

    private List<IntruderDisplaySubBean> mUnreadPhotoList;

    private List<AntiPeepBean> mAllPhotoList;
    //图片总数
    private int mTotalImgCount, mCurrentPosition = 0;

    private IOnEventMainThreadSubscriber<AntiPeepUnreadUpdateDoneEvent> mIoAntiPeepUnreadUpdateDoneEvtSubscriber = new IOnEventMainThreadSubscriber<AntiPeepUnreadUpdateDoneEvent>() {
        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(AntiPeepUnreadUpdateDoneEvent event) {
            TheApplication.getGlobalEventBus().unregister(mIoAntiPeepUnreadUpdateDoneEvtSubscriber);
            loadingData();
        }
    };

    private IOnEventMainThreadSubscriber<AppLockImageDeleteEvent> mIoAppLockImageDeleteEvtSubscriber = new IOnEventMainThreadSubscriber<AppLockImageDeleteEvent>() {
        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(AppLockImageDeleteEvent event) {
            deleteData(event.getPath());
        }
    };

    public IntruderHoriGalleryPersenter(IntruderHoriGalleryContract.View view, IntruderHoriGalleryContract.Support support) {
        this.mView = view;
        this.mSupport = support;
    }

    @Override
    public void loadData() {
        if (mView != null) {
            mView.showDataLoading();
        }
        if (!TheApplication.getGlobalEventBus().isRegistered(mIoAntiPeepUnreadUpdateDoneEvtSubscriber)) {
            TheApplication.getGlobalEventBus().register(mIoAntiPeepUnreadUpdateDoneEvtSubscriber);
        }
        if (!TheApplication.getGlobalEventBus().isRegistered(mIoAppLockImageDeleteEvtSubscriber)) {
            TheApplication.getGlobalEventBus().register(mIoAppLockImageDeleteEvtSubscriber);
        }
        mSupport.updatePhotoData();
    }

    @Override
    public void dealViewDestory() {
        if (TheApplication.getGlobalEventBus().isRegistered(mIoAppLockImageDeleteEvtSubscriber)) {
            TheApplication.getGlobalEventBus().unregister(mIoAppLockImageDeleteEvtSubscriber);
        }
        if (TheApplication.getGlobalEventBus().isRegistered(mIoAntiPeepUnreadUpdateDoneEvtSubscriber)) {
            TheApplication.getGlobalEventBus().unregister(mIoAntiPeepUnreadUpdateDoneEvtSubscriber);
        }
        if (mSupport != null) {
            mSupport.updatePhotoAllToReaded();
        }
    }

    /**
     * 处理数据
     */
    private void loadingData() {
        if (mSupport != null && mView != null) {
            mAllPhotoList = mSupport.gainUnreadPhotos();
            if (mAllPhotoList == null || mAllPhotoList.size() == 0) {
                mView.onFinish();
            } else {
                // 降序排序
                sortPhotosReverse(mAllPhotoList);

                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                Calendar calendar = Calendar.getInstance();
                mUnreadPhotoList = new ArrayList<>();
                for (AntiPeepBean bean : mAllPhotoList) {
                    calendar.setTimeInMillis(bean.getCreateTime());
                    IntruderDisplaySubBean tempBean = new IntruderDisplaySubBean(bean.getPath(), formatter.format(calendar.getTime()));
                    mUnreadPhotoList.add(tempBean);
                }

                mTotalImgCount = mAllPhotoList.size();
                mView.showPhotoData(mUnreadPhotoList);
            }

            mView.refreshNotice((mCurrentPosition + 1 ) + "/" + mTotalImgCount);
            mView.showDataLoaded();
        }
    }

    /**
     * 删除图片
     * */
    private void deleteData(String path) {
        if (mView != null) {
            for (Iterator<IntruderDisplaySubBean> iterator = mUnreadPhotoList.iterator(); iterator.hasNext(); ) {
                IntruderDisplaySubBean intruderDisplaySubBean = iterator.next();
                if (intruderDisplaySubBean.getPath().equals(path)) {
                    iterator.remove();
                    break;
                }
            }

            for (Iterator<AntiPeepBean> iterator = mAllPhotoList.iterator(); iterator.hasNext(); ) {
                AntiPeepBean antiPeepBean = iterator.next();
                if (antiPeepBean.getPath().equals(path)) {
                    if (mSupport != null) {
                        //删除图片
                        mSupport.deletePhoto(antiPeepBean);
                    }
                    iterator.remove();
                    break;
                }
            }

            // 顺势显示下张，没下张逆势显示上张
            if (mUnreadPhotoList.size() > 0 && !(mCurrentPosition < mUnreadPhotoList.size())) {
                mCurrentPosition--;
            }
            mTotalImgCount = mUnreadPhotoList.size();

            if (mTotalImgCount == 0) {
                mView.onFinish();
            } else {
                mView.showPhotoData(mUnreadPhotoList);
                mView.scrollToPosition(mCurrentPosition, 600);
            }
            mView.refreshNotice((mCurrentPosition + 1 ) + "/" + mTotalImgCount);
        }
    }

    /**
     * 排序
     * */
    private void sortPhotosReverse(List<AntiPeepBean> tempList) {
        Collections.sort(tempList, new Comparator<AntiPeepBean>() {
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

    @Override
    public void dealSlideToLastPhoto() {
        if (mCurrentPosition <= 0) {
            return;
        } else {
            mView.scrollToPosition(--mCurrentPosition, 600);
            mView.refreshNotice((mCurrentPosition + 1 ) + "/" + mTotalImgCount);
        }
    }

    @Override
    public void dealSlideToNextPhoto() {
        if (mCurrentPosition >= mTotalImgCount - 1) {
            return;
        } else {
            mView.scrollToPosition(++ mCurrentPosition, 600);
            mView.refreshNotice((mCurrentPosition + 1 ) + "/" + mTotalImgCount);
        }
    }

    @Override
    public String getPhotoPath() {
        if (mCurrentPosition >= 0 && mAllPhotoList.size() > mCurrentPosition) {
            return mAllPhotoList.get(mCurrentPosition).getPath();
        }
        return null;
    }

    @Override
    public int getCurrentPos() {
        return mCurrentPosition;
    }
}
