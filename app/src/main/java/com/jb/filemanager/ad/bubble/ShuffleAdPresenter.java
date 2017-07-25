package com.jb.filemanager.ad.bubble;

import android.os.Handler;
import android.view.ViewGroup;

import com.jb.filemanager.BaseActivity;

/**
 * Created by bill wang on 2017/7/21.
 * 试试手气广告管理类，负责起气球页面，加载广告，热推专题。
 */

public class ShuffleAdPresenter implements AdPresetView.Listener{

    private static final int MAX_BUBBLE_DURATION = 15000;

//    private NativeAdsManager mFacebookNativeAdManager;
    private AdScrollView mAdScrollView;
    private AdPresetView mRecommendAdView;

    private BaseActivity mActivity;

    private ShuffleView mShuffleView;
    private Handler mShuffleHandler;
    private Runnable mShuffleRunnable;

    private boolean mIsPlayAnimation;
    private boolean mIsWorking;
//    private boolean mIsWaitingFacebookAd;

    private Listener mListener;

    public ShuffleAdPresenter(BaseActivity activity, Listener listener) {
        mActivity = activity;
        mListener = listener;
    }

    // TODO 获取广告成功，失败后的处理 @wangzq
//    @Override
//    public void onAdsLoaded() {
//        if (mIsWaitingFacebookAd) {
//            mIsWaitingFacebookAd = false;
//            showAd();
//        }
//    }
//
//
//    @Override
//    public void onAdError(AdError adError) {
//        // 什么都不做，等待气球飘完。
//    }
    // TODO end

    public boolean isPlayAnimation() {
        return mIsPlayAnimation;
    }

    public boolean isWorking() {
        return mIsWorking;
    }

    public void cancel() {
        cancelShuffleHandler();
        dismissShuffle();
        dismissAdScrollView();
        dismissRecommend();
        resetProperty();
    }

    public boolean onBackPressed() {
        boolean result = false;
        if (mIsWorking) {
            result = true;

            if (mAdScrollView != null && mListener != null) {
                dismissAdScrollView();
                mListener.onBack(0);
                mIsWorking = false;
            } else if (mRecommendAdView != null && mListener != null) {
                dismissRecommend();
                mListener.onBack(1);
                mIsWorking = false;
            } else {
                cancel();
            }

        }
        return result;
    }

    public synchronized void bubble() {
        if (mActivity != null && mShuffleView == null) {
            mIsPlayAnimation = true;
            mIsWorking = true;

            mShuffleView = new ShuffleView(mActivity);
            ViewGroup.LayoutParams layout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            ViewGroup parent = (ViewGroup)mShuffleView.getParent();
            if (parent != null) {
                parent.removeView(mShuffleView);
            }
            mActivity.addContentView(mShuffleView, layout);

            mShuffleView.start();

            if (mShuffleRunnable == null) {
                mShuffleRunnable = new Runnable() {

                    @Override
                    public void run() {
                        dismissShuffle();
//                        mIsWaitingFacebookAd = false;
                        mIsPlayAnimation = false;

                        // TODO @wangzq 飘完气球后的处理
//                        showRecommendAd();
                    }
                };
            }

            if (mShuffleHandler == null) {
                mShuffleHandler = new Handler();
            } else {
                mShuffleHandler.removeCallbacksAndMessages(null);
            }
            mShuffleHandler.postDelayed(mShuffleRunnable, MAX_BUBBLE_DURATION);

            // facebook广告
            // TODO @wangzq 获取广告
//            mIsWaitingFacebookAd = true;
//            if (mFacebookNativeAdManager == null) {
//                mFacebookNativeAdManager = new NativeAdsManager(mActivity, mActivity.getString(R.string.fb_native_id_for_lucky), Macro.FB_NATIVE_AD_COUNT);
//                mFacebookNativeAdManager.setListener(this);
//            }
//            mFacebookNativeAdManager.loadAds(NativeAd.MediaCacheFlag.ALL);
        }
    }

    private void resetProperty() {
        mIsPlayAnimation = false;
        mIsWorking = false;
//        mIsWaitingFacebookAd = false;
    }

    private void showAd() {
        cancelShuffleHandler();
        dismissShuffle();

        // TODO @wangzq 展示广告
//        if (mActivity.isDestroyed()) {
//            mIsPlayAnimation = false;
//
//            if (mAdScrollView == null) {
//                mAdScrollView = new AdScrollView(mActivity);
//            }
//            mAdScrollView.show(mActivity, mFacebookNativeAdManager);
//        }
    }

//    private void showRecommendAd() {
//        cancelShuffleHandler();
//        dismissShuffle();
//
//        if (AppKit.isAvailable(mActivity)) {
//            mIsPlayAnimation = false;
//
//            if (mRecommendAdView == null) {
//                mRecommendAdView = new AdPresetView(mActivity);
//                mRecommendAdView.setListener(this);
//            }
//
//            mRecommendAdView.show(mActivity, R.drawable.img_preset_ad_zero);
//        }
//    }

    private void cancelShuffleHandler() {
        if (mShuffleHandler != null) {
            mShuffleHandler.removeCallbacksAndMessages(null);
            mShuffleHandler = null;
            mShuffleRunnable = null;
        }
    }

    private void dismissShuffle() {
        if (mShuffleView != null) {
            mShuffleView.detach();
            mShuffleView = null;
        }
    }

    private void dismissAdScrollView() {
        if (mAdScrollView != null) {
            mAdScrollView.dismiss();
            mAdScrollView = null;
        }
    }

    private void dismissRecommend() {
        if (mRecommendAdView != null) {
            mRecommendAdView.dismiss();
            mRecommendAdView = null;
        }
    }

    @Override
    public void onClick() {
        // TODO
    }

    public interface Listener {
        void onBack(int result);
        void onError();
    }
}
