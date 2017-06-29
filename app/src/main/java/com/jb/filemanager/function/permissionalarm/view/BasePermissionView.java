package com.jb.filemanager.function.permissionalarm.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.ad.AdEntry;
import com.jb.filemanager.ad.AdFrequencyManager;
import com.jb.filemanager.ad.AdManager;
import com.jb.filemanager.ad.data.AdAdapter;
import com.jb.filemanager.ad.data.AdViewBean;
import com.jb.filemanager.ad.data.AdWrapper;
import com.jb.filemanager.ad.data.BindAdHelper;
import com.jb.filemanager.ad.event.NormalAdLoadCompleteEvent;
import com.jb.filemanager.ad.event.OnAdClickEvent;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.permissionalarm.event.PermissionViewDismissEvent;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.QuickClickGuard;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.mopub.mobileads.MoPubView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by nieyh on 2017/2/10.
 */

public abstract class BasePermissionView extends RelativeLayout {

    private View mAdContainer;
    private FrameLayout mAdRoot;
    private ImageView mAdCoverView;
    private ImageView mAdIconView;
    private TextView mAdTitleView;
    private TextView mAdDetailView;
    private TextView mInstallButton;
    protected QuickClickGuard mQuickClickGuard;
    private AdViewBean mAd;
    public static final int mAdEntrance = AdEntry.ENTRANCE_PERMISSION_ALERT;

    public BasePermissionView(Context context) {
        super(context);
        mQuickClickGuard = new QuickClickGuard();
        initView();
    }

    public BasePermissionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mQuickClickGuard = new QuickClickGuard();
        initView();
    }

    public BasePermissionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mQuickClickGuard = new QuickClickGuard();
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BasePermissionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mQuickClickGuard = new QuickClickGuard();
        initView();
    }

    protected abstract void initView();

    protected abstract FrameLayout getAdRootView();

    public abstract void buildView(String pkgName, List<String> permissionLists);

    public abstract boolean onBackPress();

    public abstract void onOutSideTouch();

    private final IOnEventMainThreadSubscriber<OnAdClickEvent> mOnAdClickEvent = new IOnEventMainThreadSubscriber<OnAdClickEvent>() {

        @Subscribe(threadMode = ThreadMode.MAIN)
        @Override
        public void onEventMainThread(OnAdClickEvent event) {
            if (mAd != null && mAd.getHashCode() == event.getHashCode()) {
                //关闭 页面
                TheApplication.getGlobalEventBus().post(new PermissionViewDismissEvent());
                BindAdHelper.helpAppCenterClickStatistics(TheApplication.getAppContext(), mAd);
//                statisticsAdClick("4");
            }
        }
    };

    /**
     * 广告加载完毕事件
     */
    private final IOnEventMainThreadSubscriber<NormalAdLoadCompleteEvent> mNormalAdLoadCompleteEvtSubscriber = new IOnEventMainThreadSubscriber<NormalAdLoadCompleteEvent>() {

        /**
         * 加载广告完成<br>
         * @param event
         */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(NormalAdLoadCompleteEvent event) {
            int entrance = event.getEntrance();
            // TODO: 2017/2/13 更换广告id
            if (entrance == mAdEntrance) {
                Logger.w("permission", "NormalAdLoadCompleteEvent");
                if (EventBus.getDefault().isRegistered(mNormalAdLoadCompleteEvtSubscriber)) {
                    EventBus.getDefault().unregister(mNormalAdLoadCompleteEvtSubscriber);
                }

                ArrayList<AdWrapper> adsList = event.getAdsList();
                AdModuleInfoBean adModuleInfoBean = event.getAdModuleInfoBean();
                AdManager.getInstance().removeAdFromCache(entrance);
                if (adsList != null && adsList.size() > 0) {
                    mAd = AdAdapter.boxData(adsList.get(0), adModuleInfoBean);
                    inflateAd();
                    updateViews();
                }

                // 降低广告点击率
                AdFrequencyManager.getInstance().handleAdFrequency(adModuleInfoBean);
            }
        }
    };

    @CallSuper
    public void loadAd() {
        if (!TheApplication.getGlobalEventBus().isRegistered(mOnAdClickEvent)) {
            TheApplication.getGlobalEventBus().register(mOnAdClickEvent);
        }
        if (!TheApplication.getGlobalEventBus().isRegistered(mNormalAdLoadCompleteEvtSubscriber)) {
            TheApplication.getGlobalEventBus().register(mNormalAdLoadCompleteEvtSubscriber);
        }
        // TODO: 17-6-29 @nieyh 广告需要添加请求
//        if (AdManager.getInstance().hasAdInCache(mAdEntrance)) {
//            AdManager.getInstance().getAdFromCache(mAdEntrance);
//        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        toRelease();
    }

    /**
     * 将广告数据绑定到ui上
     */
    private void updateViews() {
        if (mAd != null) {
            final Context applicationContext = TheApplication.getAppContext();
            BindAdHelper.setName(mAd, mAdTitleView);
            BindAdHelper.setDesc(mAd, mAdDetailView);
            BindAdHelper.setButton(mAd, mInstallButton);
            BindAdHelper.setIcon(applicationContext, mAd, mAdIconView);
            BindAdHelper.setBanner(applicationContext, mAd, mAdCoverView);
            BindAdHelper.doSthSpeciallyOnShown(mAd);
            BindAdHelper.setClick(applicationContext, mAd, mAd.getEntranceId(),
                    mAdContainer, mAdCoverView, mAdIconView, mAdDetailView, mAdTitleView,
                    mInstallButton);
            BindAdHelper.helpAppCenterShowStaticstic(TheApplication.getAppContext(), mAd);
            statisticsAdShow("4");
        }
    }

    private void inflateAd() {
        if (mAd == null) {
            return;
        }
        ImageView ivAdClose = null;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mAdRoot = getAdRootView();
        mAdRoot.setVisibility(VISIBLE);
        mAdRoot.removeAllViews();
        if (mAd.isAdmobNativeInsallAd()) {
            Logger.w("permission", "isAdmobNativeInsallAd");
            NativeAppInstallAdView adView = (NativeAppInstallAdView) inflater.inflate(
                    R.layout.layout_permission_alarm_ad_admob_app_install, mAdRoot, false);
            mAdContainer = adView.findViewById(R.id.ad_container);
            mAdCoverView = (ImageView) adView.findViewById(R.id.ad_head);
            mAdIconView = (ImageView) adView.findViewById(R.id.ad_icon);
            mAdTitleView = (TextView) adView.findViewById(R.id.ad_info_title);
            mAdDetailView = (TextView) adView.findViewById(R.id.ad_info_detail);
            mInstallButton = (TextView) adView.findViewById(R.id.ad_action);
            mInstallButton.setText(TheApplication.getAppContext().getResources().getString(R.string.ad_install_now));
            ivAdClose = (ImageView) adView.findViewById(R.id.iv_wifi_state_ad_close);
            adView.setHeadlineView(mAdTitleView);
            adView.setImageView(mAdCoverView);
            adView.setBodyView(mAdDetailView);
            adView.setCallToActionView(mInstallButton);
            adView.setIconView(mAdIconView);
            adView.setNativeAd(mAd.getAdmobNativeInstallAd());
            mAdRoot.addView(adView);

        } else if (mAd.isAdmobNativeContentAd()) {
            Logger.w("permission", "isAdmobNativeContentAd");
            NativeContentAdView adView = (NativeContentAdView) inflater.inflate(
                    R.layout.layout_permission_alarm_ad_admob_content, mAdRoot, false);
            mAdContainer = adView.findViewById(R.id.ad_container);
            mAdCoverView = (ImageView) adView.findViewById(R.id.ad_head);
            mAdIconView = (ImageView) adView.findViewById(R.id.ad_icon);
            mAdTitleView = (TextView) adView.findViewById(R.id.ad_info_title);
            mAdDetailView = (TextView) adView.findViewById(R.id.ad_info_detail);
            mInstallButton = (TextView) adView.findViewById(R.id.ad_action);
            mInstallButton.setText(TheApplication.getAppContext().getResources().getString(R.string.ad_install_now));
            ivAdClose = (ImageView) adView.findViewById(R.id.iv_wifi_state_ad_close);
            adView.setHeadlineView(mAdTitleView);
            adView.setImageView(mAdCoverView);
            adView.setBodyView(mAdDetailView);
            adView.setCallToActionView(mInstallButton);
            adView.setLogoView(mAdIconView);
            adView.setNativeAd(mAd.getAdmobNativeContentAd());
            mAdRoot.addView(adView);

        } else if (mAd.isFbNativeAd()) {
            FrameLayout adView = (FrameLayout) inflater.inflate(
                    R.layout.layout_permission_alarm_ad, mAdRoot, false);

            ImageView adChoice = (ImageView) adView.findViewById(R.id.iv_ad_choice);
            if (adChoice != null) {
                adChoice.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppUtils.openBrowser(TheApplication.getAppContext(), Const.FACEBOOK_AD_CHOICE_URL);
                    }
                });
            }

            mAdContainer = adView.findViewById(R.id.ad_container);
            mAdCoverView = (ImageView) adView.findViewById(R.id.ad_head);
            mAdIconView = (ImageView) adView.findViewById(R.id.ad_icon);
            mAdTitleView = (TextView) adView.findViewById(R.id.ad_info_title);
            mAdDetailView = (TextView) adView.findViewById(R.id.ad_info_detail);
            mInstallButton = (TextView) adView.findViewById(R.id.ad_action);
            mInstallButton.setText(TheApplication.getAppContext().getResources().getString(R.string.ad_install_now));
            //facebook 广告隐藏close
            ivAdClose = (ImageView) adView.findViewById(R.id.iv_wifi_state_ad_close);
            mAdRoot.addView(adView);
        } else if (mAd.isAppCenterAd()) {
            FrameLayout adView = (FrameLayout) inflater.inflate(
                    R.layout.layout_permission_alarm_ad, mAdRoot, false);
            //非facebook广告将 新跳转标示隐藏
            ImageView adChoice = (ImageView) adView.findViewById(R.id.iv_ad_choice);
            if (adChoice != null) {
                adChoice.setVisibility(View.GONE);
            }
            //gomo广告隐藏ad
            View mAdFlat = adView.findViewById(R.id.iv_ad_flat);
            mAdFlat.setVisibility(View.GONE);

            mAdContainer = adView.findViewById(R.id.ad_container);
            mAdCoverView = (ImageView) adView.findViewById(R.id.ad_head);
            mAdIconView = (ImageView) adView.findViewById(R.id.ad_icon);
            mAdTitleView = (TextView) adView.findViewById(R.id.ad_info_title);
            mAdDetailView = (TextView) adView.findViewById(R.id.ad_info_detail);
            mInstallButton = (TextView) adView.findViewById(R.id.ad_action);
            mInstallButton.setText(TheApplication.getAppContext().getResources().getString(R.string.ad_install_now));
            ivAdClose = (ImageView) adView.findViewById(R.id.iv_wifi_state_ad_close);
            mAdRoot.addView(adView);
        } else if (mAd.isMoPubNative()) {
            // TODO: 17-6-29 权限检测mopub广告
          /*  com.mopub.nativeads.NativeAd nativeAd = mAd.getMoPubNativeAd();
            View adView = nativeAd.createAdView(TheApplication.getInstance(), null);
            nativeAd.renderAdView(adView);
            //mopub广告显示标示以及文字提示
            adView.findViewById(R.id.ad_mopub_tip_layout).setVisibility(View.VISIBLE);
            mAdContainer = adView.findViewById(R.id.ad_container);
            mAdCoverView = (ImageView) adView.findViewById(R.id.iv_ad_cover);
            mAdIconView = (ImageView) adView.findViewById(R.id.iv_ad_icon);
            mAdTitleView = (TextView) adView.findViewById(R.id.tv_ad_title);
            mAdDetailView = (TextView) adView.findViewById(R.id.tv_ad_desc);
            mInstallButton = (TextView) adView.findViewById(R.id.btn_ad_download);
            ivAdClose = (ImageView) adView.findViewById(R.id.iv_wifi_state_ad_close);

            ImageView ad = (ImageView) adView.findViewById(R.id.iv_ad_flat);
            ad.setVisibility(View.GONE);

            ImageView normalAdPrivacyInfo = (ImageView) adView.findViewById(R.id.iv_ad_choice);
            normalAdPrivacyInfo.setVisibility(View.GONE);

            nativeAd.prepare(adView);
            mAdRoot.addView(adView);*/
        } else if (mAd.isMoPubBannerAd()) {
            MoPubView moPubBannerAd = mAd.getMoPubBannerAd();
            mAdRoot.addView(moPubBannerAd);
        } else {
            FrameLayout adView = (FrameLayout) inflater.inflate(
                    R.layout.layout_permission_alarm_ad, mAdRoot, false);
            //非facebook广告将 新跳转标示隐藏
            ImageView adChoice = (ImageView) adView.findViewById(R.id.iv_ad_choice);
            if (adChoice != null) {
                adChoice.setVisibility(View.GONE);
            }
            mAdContainer = adView.findViewById(R.id.ad_container);
            mAdCoverView = (ImageView) adView.findViewById(R.id.ad_head);
            mAdIconView = (ImageView) adView.findViewById(R.id.ad_icon);
            mAdTitleView = (TextView) adView.findViewById(R.id.ad_info_title);
            mAdDetailView = (TextView) adView.findViewById(R.id.ad_info_detail);
            mInstallButton = (TextView) adView.findViewById(R.id.ad_action);
            mInstallButton.setText(TheApplication.getAppContext().getResources().getString(R.string.ad_install_now));
            ivAdClose = (ImageView) adView.findViewById(R.id.iv_wifi_state_ad_close);
            mAdRoot.addView(adView);
        }
        // TODO: 2017/3/29 mopub广告的话 ad_mopub_tip_layout这个View需要显示出来
        if (ivAdClose != null) {
            ivAdClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mQuickClickGuard.isQuickClick(v.getId())) {
                        TheApplication.getGlobalEventBus().post(new PermissionViewDismissEvent());
                    }
                }
            });
        }
    }

    @CallSuper
    public void toRelease() {
        if (TheApplication.getGlobalEventBus().isRegistered(mOnAdClickEvent)) {
            TheApplication.getGlobalEventBus().unregister(mOnAdClickEvent);
        }
        if (TheApplication.getGlobalEventBus().isRegistered(mNormalAdLoadCompleteEvtSubscriber)) {
            TheApplication.getGlobalEventBus().unregister(mNormalAdLoadCompleteEvtSubscriber);
        }
    }

    private void statisticsAdShow(String entrance) {
//        Statistics101Bean bean = Statistics101Bean.builder();
//        bean.mOpeateId = StatisticsConstants.F000_AD_SHOW;
//        bean.mEntrance = entrance;
//        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsAdClick(String entrance) {
//        Statistics101Bean bean = Statistics101Bean.builder();
//        bean.mOpeateId = StatisticsConstants.C000_AD_CLICK;
//        bean.mEntrance = entrance;
//        StatisticsTools.upload101InfoNew(bean);
    }
}
