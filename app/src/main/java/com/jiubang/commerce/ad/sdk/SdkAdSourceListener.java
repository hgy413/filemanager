package com.jiubang.commerce.ad.sdk;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.ironscr.IronScrAd;
import com.jiubang.commerce.ad.manager.AdControlManager;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdInfoBean;
import com.jiubang.commerce.statistics.AdSdkOperationStatistic;
import com.jiubang.commerce.thread.AdSdkThreadExecutorProxy;
import com.jiubang.commerce.utils.AppUtils;
import com.jiubang.commerce.utils.NetworkUtils;
import com.jiubang.commerce.utils.StringUtils;
import com.jiubang.commerce.utils.SystemUtils;
import com.jiubang.commerce.utils.TimeOutGuard;
import com.loopme.LoopMeBanner;
import com.loopme.LoopMeError;
import com.loopme.LoopMeInterstitial;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.MoPubAdRenderer;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.RequestParameters;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

class SdkAdSourceListener implements SdkAdSourceInterface {
    private static SdkAdSourceListener sInstance;

    private SdkAdSourceListener() {
    }

    public static SdkAdSourceListener getInstance() {
        if (sInstance == null) {
            sInstance = new SdkAdSourceListener();
        }
        return sInstance;
    }

    public void loadFaceBookAdInfo(AdSdkParamsBuilder adSdkParams, BaseModuleDataItemBean moduleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener) {
        if (sdkAdSourceRequestListener != null) {
            final Context context = adSdkParams.mContext;
            final int virtualModuleId = moduleDataItemBean != null ? moduleDataItemBean.getVirtualModuleId() : -1;
            if (!(AppUtils.isAppExist(context, AdSdkContants.PACKAGE_NAME_FACEBOOK) || AppUtils.isAppExist(context, AdSdkContants.PACKAGE_NAME_FACEBOOK_LITE)) || !NetworkUtils.isNetworkOK(context)) {
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadFaceBookAdInfo(广告加载失败，因facebook未安装或网络问题，仅返回模块控制信息!");
                }
                sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
            } else if (!SystemUtils.IS_SDK_ABOVE_GBREAD) {
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadFaceBookAdInfo(version error, android sdk above 2.3 required!)");
                }
                sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
            } else {
                String adClassName = "";
                try {
                    if (BaseModuleDataItemBean.isNativeAd(moduleDataItemBean)) {
                        adClassName = "com.facebook.ads.NativeAd";
                    } else if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean)) {
                        adClassName = "com.facebook.ads.AdView";
                    } else if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
                        adClassName = "com.facebook.ads.InterstitialAd";
                    }
                    if (TextUtils.isEmpty(adClassName)) {
                        sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                        LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadFaceBookAdInfo(ad show type error, " + (moduleDataItemBean != null ? Integer.valueOf(moduleDataItemBean.getOnlineAdvType()) : "null") + ")");
                        return;
                    }
                    Class<?> adViewClass = Class.forName(adClassName);
                    if (LogUtils.isShowLog()) {
                        LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadFaceBookAdInfo(" + adClassName + ", " + adViewClass.getName() + ")");
                    }
                    String[] faceBookIds = moduleDataItemBean != null ? moduleDataItemBean.getFbIds() : null;
                    if (context == null || faceBookIds == null || faceBookIds.length < 1) {
                        LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadFaceBookAdInfo(faceBook id is null!)");
                        sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                        return;
                    }
                    final String[] tempFaceBookIds = faceBookIds;
                    final String tabCategory = adSdkParams.mTabCategory;
                    if (LogUtils.isShowLog()) {
                        LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]loadFaceBookAdInfo:tabCategory=" + tabCategory);
                    }
                    final FacebookAdConfig facebookAdConfig = adSdkParams.mFacebookAdConfig;
                    final AdSdkParamsBuilder adSdkParamsBuilder = adSdkParams;
                    final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener2 = sdkAdSourceRequestListener;
                    final BaseModuleDataItemBean baseModuleDataItemBean = moduleDataItemBean;
                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                Looper.prepare();
                            } catch (Exception e) {
                                LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadFaceBookAdInfo:looper error:" + e.getMessage());
                            }
                            Handler handler = new Handler(Looper.myLooper());
                            final TimeOutGuard timeOutGurad = new TimeOutGuard();
                            timeOutGurad.start(adSdkParamsBuilder.mTimeOut, new TimeOutGuard.TimeOutTask() {
                                public void onTimeOut() {
                                    LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadFaceBookAdInfo:time out");
                                    Object param = timeOutGurad != null ? timeOutGurad.getParam() : null;
                                    if (param instanceof Handler) {
                                        try {
                                            ((Handler) param).getLooper().quit();
                                        } catch (Exception e) {
                                            LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadFaceBookAdInfo:looper.quit", e);
                                        }
                                    }
                                    sdkAdSourceRequestListener2.onFinish((SdkAdSourceAdInfoBean) null);
                                }
                            }, handler);
                            SdkAdSourceListener.this.loadSingleFaceBookAdInfo(context, adSdkParamsBuilder, tempFaceBookIds, -1, baseModuleDataItemBean, new SdkAdSourceAdInfoBean(), tabCategory, handler, timeOutGurad, facebookAdConfig, sdkAdSourceRequestListener2);
                            try {
                                Looper.loop();
                            } catch (Exception e2) {
                                LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadFaceBookAdInfo:Looper.loop() error:" + e2.getMessage());
                            }
                        }
                    });
                    thread.setName("loadFaceBookAdInfo");
                    thread.start();
                } catch (Throwable thr) {
                    if (LogUtils.isShowLog()) {
                        LogUtils.w("Ad_SDK", "[vmId:" + virtualModuleId + "]loadFaceBookAdInfo(" + adClassName + ", FaceBook SDK does not exist " + (thr != null ? thr.getMessage() : "") + ")", thr);
                    }
                    sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                }
            }
        }
    }

    public void loadAdMobAdInfo(AdSdkParamsBuilder adSdkParams, BaseModuleDataItemBean moduleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener) {
        if (sdkAdSourceRequestListener != null) {
            final Context context = adSdkParams.mContext;
            int virtualModuleId = moduleDataItemBean != null ? moduleDataItemBean.getVirtualModuleId() : -1;
            if (!NetworkUtils.isNetworkOK(context)) {
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadAdMobAdInfo(广告加载失败，因网络问题，仅返回模块控制信息!");
                }
                sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
            } else if (!SystemUtils.IS_SDK_ABOVE_GBREAD) {
                if (LogUtils.isShowLog()) {
                    LogUtils.w("Ad_SDK", "[vmId:" + virtualModuleId + "]loadAdMobAdInfo(version error, android sdk above 2.3 required!)");
                }
                sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
            } else {
                List<String> adClassName = new ArrayList<>();
                try {
                    if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean) || BaseModuleDataItemBean.isBannerAd300_250(moduleDataItemBean)) {
                        adClassName.add("com.google.android.gms.ads.AdView");
                    } else if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
                        adClassName.add("com.google.android.gms.ads.InterstitialAd");
                    } else if (BaseModuleDataItemBean.isNativeAd(moduleDataItemBean)) {
                        adClassName.add("com.google.android.gms.ads.formats.NativeContentAd");
                        adClassName.add("com.google.android.gms.ads.formats.NativeAppInstallAd");
                    }
                    if (adClassName.isEmpty()) {
                        sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                        LogUtils.e("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadAdMobAdInfo(ad show type error, " + (moduleDataItemBean != null ? Integer.valueOf(moduleDataItemBean.getOnlineAdvType()) : "null") + ")");
                        return;
                    }
                    for (String classStr : adClassName) {
                        Class<?> adViewClass = Class.forName(classStr);
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadAdMobAdInfo(" + classStr + ", " + adViewClass.getName() + ")");
                        }
                    }
                    String[] adMobIds = moduleDataItemBean != null ? moduleDataItemBean.getFbIds() : null;
                    if (context == null || adMobIds == null || adMobIds.length < 1) {
                        LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadAdMobAdInfo(adMob id is null.)");
                        sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                        return;
                    }
                    final String[] tempAdMobIds = adMobIds;
                    final String tabCategory = adSdkParams.mTabCategory;
                    if (LogUtils.isShowLog()) {
                        LogUtils.d("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadAdMobAdInfo:tabCategory=" + tabCategory);
                    }
                    final BaseModuleDataItemBean baseModuleDataItemBean = moduleDataItemBean;
                    final AdSdkParamsBuilder adSdkParamsBuilder = adSdkParams;
                    final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener2 = sdkAdSourceRequestListener;
                    AdSdkThreadExecutorProxy.runOnMainThread(new Runnable() {
                        public void run() {
                            SdkAdSourceListener.this.loadSingleAdMobAdInfo(context, tempAdMobIds, -1, baseModuleDataItemBean, new SdkAdSourceAdInfoBean(), tabCategory, adSdkParamsBuilder.mAdmobAdConfig, adSdkParamsBuilder, sdkAdSourceRequestListener2);
                        }
                    });
                } catch (Throwable thr) {
                    LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadAdMobAdInfo(" + adClassName + ", AdMob SDK does not exist" + (thr != null ? thr.getMessage() : "") + ")", thr);
                    sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                }
            }
        }
    }

    public void loadLoopMeAdInfo(AdSdkParamsBuilder adSdkParams, BaseModuleDataItemBean moduleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener) {
        if (sdkAdSourceRequestListener != null) {
            final Context context = adSdkParams.mContext;
            int virtualModuleId = moduleDataItemBean != null ? moduleDataItemBean.getVirtualModuleId() : -1;
            if (!NetworkUtils.isNetworkOK(context)) {
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadLoopMeAdInfo(广告加载失败，因网络问题，仅返回模块控制信息!");
                }
                sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
            } else if (!SystemUtils.IS_SDK_ABOVE_ICS) {
                sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadLoopMeAdInfo(version error, android sdk above 4.0 required!)");
                }
            } else {
                String adClassName = "";
                try {
                    if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean)) {
                        adClassName = "com.loopme.LoopMeBanner";
                    } else if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
                        adClassName = "com.loopme.LoopMeInterstitial";
                    }
                    if (TextUtils.isEmpty(adClassName)) {
                        sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                        LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadLoopMeAdInfo(ad show type error, " + (moduleDataItemBean != null ? Integer.valueOf(moduleDataItemBean.getOnlineAdvType()) : "null") + ")");
                        return;
                    }
                    Class<?> adViewClass = Class.forName(adClassName);
                    if (LogUtils.isShowLog()) {
                        LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadLoopMeAdInfo(" + adClassName + ", " + adViewClass.getName() + ")");
                    }
                    String[] loopMeIds = moduleDataItemBean != null ? moduleDataItemBean.getFbIds() : null;
                    if (context == null || loopMeIds == null || loopMeIds.length < 1) {
                        LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadLoopMeAdInfo(loopMe id is null.)");
                        sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                        return;
                    }
                    final String[] tempLoopMeIds = loopMeIds;
                    final String tabCategory = adSdkParams.mTabCategory;
                    if (LogUtils.isShowLog()) {
                        LogUtils.d("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadLoopMeAdInfo:tabCategory=" + tabCategory);
                    }
                    final BaseModuleDataItemBean baseModuleDataItemBean = moduleDataItemBean;
                    final AdSdkParamsBuilder adSdkParamsBuilder = adSdkParams;
                    final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener2 = sdkAdSourceRequestListener;
                    AdSdkThreadExecutorProxy.runOnMainThread(new Runnable() {
                        public void run() {
                            SdkAdSourceListener.this.loadSingleLoopMeAdInfo(context, tempLoopMeIds, -1, baseModuleDataItemBean, new SdkAdSourceAdInfoBean(), tabCategory, adSdkParamsBuilder, sdkAdSourceRequestListener2);
                        }
                    });
                } catch (Throwable thr) {
                    LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadLoopMeAdInfo(" + adClassName + ", LoopMe SDK does not exist" + (thr != null ? thr.getMessage() : "") + ")", thr);
                    sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                }
            }
        }
    }

    public void loadMobileCoreAdInfo(AdSdkParamsBuilder adSdkParams, BaseModuleDataItemBean moduleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener) {
        if (LogUtils.isShowLog()) {
            LogUtils.i("Ad_SDK", "[vmId:" + (moduleDataItemBean != null ? moduleDataItemBean.getVirtualModuleId() : -1) + "]loadMobileCoreAdInfo()");
        }
        if (sdkAdSourceRequestListener != null) {
            sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v0, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v2, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v1, resolved type: com.jiubang.commerce.ad.tricks.fb.InterceptContext} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v2, resolved type: com.jiubang.commerce.ad.tricks.fb.InterceptContext} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: com.jiubang.commerce.ad.tricks.fb.InterceptContext} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadSingleFaceBookAdInfo(android.content.Context r57, com.jiubang.commerce.ad.params.AdSdkParamsBuilder r58, java.lang.String[] r59, int r60, com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean r61, com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdInfoBean r62, java.lang.String r63, android.os.Handler r64, com.jiubang.commerce.utils.TimeOutGuard r65, com.jiubang.commerce.ad.sdk.FacebookAdConfig r66, com.jiubang.commerce.ad.manager.AdControlManager.SdkAdSourceRequestListener r67) {
        /*
            r56 = this;
            if (r67 != 0) goto L_0x0003
        L_0x0002:
            return
        L_0x0003:
            int r13 = r60 + 1
            boolean r52 = r65.hadTimeOut()
            if (r61 == 0) goto L_0x0014
            if (r59 == 0) goto L_0x0014
            r0 = r59
            int r7 = r0.length
            if (r7 <= r13) goto L_0x0014
            if (r52 == 0) goto L_0x004f
        L_0x0014:
            if (r52 == 0) goto L_0x0044
            r7 = -1
            r0 = r60
            if (r0 <= r7) goto L_0x0042
            if (r59 == 0) goto L_0x0042
            r0 = r59
            int r7 = r0.length
            r0 = r60
            if (r0 >= r7) goto L_0x0042
            r7 = r59[r60]
            java.lang.String r8 = com.jiubang.commerce.utils.StringUtils.toString(r7)
        L_0x002a:
            r10 = -2
            r0 = r58
            long r12 = r0.mTimeOut
            r7 = r57
            r9 = r63
            r11 = r61
            r14 = r58
            com.jiubang.commerce.statistics.AdSdkOperationStatistic.uploadAdRequestResultStatistic(r7, r8, r9, r10, r11, r12, r14)
        L_0x003a:
            android.os.Looper r7 = r64.getLooper()
            r7.quit()
            goto L_0x0002
        L_0x0042:
            r8 = 0
            goto L_0x002a
        L_0x0044:
            r65.cancel()
            r0 = r67
            r1 = r62
            r0.onFinish(r1)
            goto L_0x003a
        L_0x004f:
            r7 = r59[r13]
            java.lang.String r19 = com.jiubang.commerce.utils.StringUtils.toString(r7)
            boolean r7 = android.text.TextUtils.isEmpty(r19)
            if (r7 == 0) goto L_0x0075
            r9 = r56
            r10 = r57
            r11 = r58
            r12 = r59
            r14 = r61
            r15 = r62
            r16 = r63
            r17 = r64
            r18 = r65
            r19 = r66
            r20 = r67
            r9.loadSingleFaceBookAdInfo(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            goto L_0x0002
        L_0x0075:
            long r22 = java.lang.System.currentTimeMillis()
            r0 = r57
            r1 = r19
            r2 = r63
            r3 = r61
            r4 = r58
            com.jiubang.commerce.statistics.AdSdkOperationStatistic.uploadAdRequestStatistic(r0, r1, r2, r3, r4)
            boolean r7 = com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean.isBannerAd(r61)
            if (r7 == 0) goto L_0x00bb
            android.os.Handler r53 = new android.os.Handler
            android.os.Looper r7 = android.os.Looper.getMainLooper()
            r0 = r53
            r0.<init>(r7)
            com.jiubang.commerce.ad.sdk.SdkAdSourceListener$4 r15 = new com.jiubang.commerce.ad.sdk.SdkAdSourceListener$4
            r16 = r56
            r17 = r66
            r18 = r57
            r20 = r63
            r21 = r61
            r24 = r58
            r25 = r59
            r26 = r13
            r27 = r62
            r28 = r64
            r29 = r65
            r30 = r67
            r15.<init>(r17, r18, r19, r20, r21, r22, r24, r25, r26, r27, r28, r29, r30)
            r0 = r53
            r0.post(r15)
            goto L_0x0002
        L_0x00bb:
            boolean r7 = com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean.isInterstitialAd(r61)
            if (r7 == 0) goto L_0x015e
            com.facebook.ads.InterstitialAd r32 = new com.facebook.ads.InterstitialAd
            r0 = r32
            r1 = r57
            r2 = r19
            r0.<init>(r1, r2)
            com.jiubang.commerce.ad.sdk.SdkAdSourceListener$5 r16 = new com.jiubang.commerce.ad.sdk.SdkAdSourceListener$5
            r17 = r56
            r18 = r57
            r20 = r63
            r21 = r61
            r24 = r58
            r25 = r59
            r26 = r13
            r27 = r62
            r28 = r64
            r29 = r65
            r30 = r66
            r31 = r67
            r16.<init>(r18, r19, r20, r21, r22, r24, r25, r26, r27, r28, r29, r30, r31, r32)
            r0 = r32
            r1 = r16
            r0.setAdListener(r1)
            r32.loadAd()     // Catch:{ Exception -> 0x00f5 }
            goto L_0x0002
        L_0x00f5:
            r51 = move-exception
            boolean r7 = com.jb.ga0.commerce.util.LogUtils.isShowLog()
            if (r7 == 0) goto L_0x012c
            java.lang.String r7 = "Ad_SDK"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "[vmId:"
            java.lang.StringBuilder r9 = r9.append(r10)
            int r10 = r61.getVirtualModuleId()
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = "]loadSingleFaceBookAdInfo(Exception---InterstitialAd, adId:"
            java.lang.StringBuilder r9 = r9.append(r10)
            r0 = r19
            java.lang.StringBuilder r9 = r9.append(r0)
            java.lang.String r10 = ")"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            r0 = r51
            com.jb.ga0.commerce.util.LogUtils.e(r7, r9, r0)
        L_0x012c:
            r36 = -1
            long r10 = java.lang.System.currentTimeMillis()
            long r38 = r10 - r22
            r33 = r57
            r34 = r19
            r35 = r63
            r37 = r61
            r40 = r58
            com.jiubang.commerce.statistics.AdSdkOperationStatistic.uploadAdRequestResultStatistic(r33, r34, r35, r36, r37, r38, r40)
            r33 = r56
            r34 = r57
            r35 = r58
            r36 = r59
            r37 = r13
            r38 = r61
            r39 = r62
            r40 = r63
            r41 = r64
            r42 = r65
            r43 = r66
            r44 = r67
            r33.loadSingleFaceBookAdInfo(r34, r35, r36, r37, r38, r39, r40, r41, r42, r43, r44)
            goto L_0x0002
        L_0x015e:
            com.jiubang.commerce.ad.tricks.fb.FbNativeAdTrick r7 = com.jiubang.commerce.ad.tricks.fb.FbNativeAdTrick.getInstance(r57)
            int r9 = r61.getVirtualModuleId()
            com.jiubang.commerce.ad.tricks.fb.FbNativeAdTrick$Plot r50 = r7.getPlot(r9)
            r0 = r57
            boolean r7 = r0 instanceof com.jiubang.commerce.ad.sdk.SdkAdContext
            if (r7 == 0) goto L_0x01bd
            r7 = r57
            com.jiubang.commerce.ad.sdk.SdkAdContext r7 = (com.jiubang.commerce.ad.sdk.SdkAdContext) r7
            r55 = r7
        L_0x0176:
            if (r55 == 0) goto L_0x01c0
            android.content.Context r6 = r55.getContext()
        L_0x017c:
            int r7 = r61.getFbAdvCount()
            if (r7 <= 0) goto L_0x01ce
            int r54 = r61.getFbAdvCount()
        L_0x0186:
            r7 = 1
            r0 = r54
            if (r0 <= r7) goto L_0x01d1
            com.facebook.ads.NativeAdsManager r17 = new com.facebook.ads.NativeAdsManager
            r0 = r17
            r1 = r19
            r2 = r54
            r0.<init>(r6, r1, r2)
            com.jiubang.commerce.ad.sdk.SdkAdSourceListener$6 r15 = new com.jiubang.commerce.ad.sdk.SdkAdSourceListener$6
            r16 = r56
            r18 = r57
            r20 = r63
            r21 = r61
            r24 = r58
            r25 = r67
            r26 = r62
            r27 = r59
            r28 = r13
            r29 = r64
            r30 = r65
            r31 = r66
            r15.<init>(r17, r18, r19, r20, r21, r22, r24, r25, r26, r27, r28, r29, r30, r31)
            r0 = r17
            r0.setListener(r15)
            r17.loadAds()
            goto L_0x0002
        L_0x01bd:
            r55 = 0
            goto L_0x0176
        L_0x01c0:
            r0 = r57
            boolean r7 = r0 instanceof android.app.Activity
            if (r7 == 0) goto L_0x01cb
            android.content.Context r6 = r57.getApplicationContext()
            goto L_0x017c
        L_0x01cb:
            r6 = r57
            goto L_0x017c
        L_0x01ce:
            r54 = 1
            goto L_0x0186
        L_0x01d1:
            if (r55 == 0) goto L_0x0203
            boolean r7 = r6 instanceof android.app.Activity
            if (r7 == 0) goto L_0x0203
            r35 = r6
        L_0x01d9:
            com.jiubang.commerce.ad.sdk.SdkAdSourceListener$7 r33 = new com.jiubang.commerce.ad.sdk.SdkAdSourceListener$7
            r34 = r56
            r36 = r19
            r37 = r57
            r38 = r63
            r39 = r61
            r40 = r22
            r42 = r58
            r43 = r59
            r44 = r13
            r45 = r62
            r46 = r64
            r47 = r65
            r48 = r66
            r49 = r67
            r33.<init>(r35, r36, r37, r38, r39, r40, r42, r43, r44, r45, r46, r47, r48, r49, r50)
            r0 = r35
            r1 = r33
            com.jiubang.commerce.ad.sdk.FbLoaderUtil.testLoad(r0, r1)
            goto L_0x0002
        L_0x0203:
            com.jiubang.commerce.ad.tricks.fb.InterceptContext r35 = new com.jiubang.commerce.ad.tricks.fb.InterceptContext
            r0 = r35
            r1 = r50
            r0.<init>(r6, r1)
            goto L_0x01d9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.ad.sdk.SdkAdSourceListener.loadSingleFaceBookAdInfo(android.content.Context, com.jiubang.commerce.ad.params.AdSdkParamsBuilder, java.lang.String[], int, com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean, com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdInfoBean, java.lang.String, android.os.Handler, com.jiubang.commerce.utils.TimeOutGuard, com.jiubang.commerce.ad.sdk.FacebookAdConfig, com.jiubang.commerce.ad.manager.AdControlManager$SdkAdSourceRequestListener):void");
    }

    public static class FBSingleNativeAdListener implements AdListener {
        private boolean mHasOnErrorCalled = false;

        /* access modifiers changed from: protected */
        public boolean hasOnErrorCalled() {
            return this.mHasOnErrorCalled;
        }

        /* access modifiers changed from: protected */
        public void setOnErrorCalled() {
            this.mHasOnErrorCalled = true;
        }

        public void onError(Ad ad, AdError adError) {
        }

        public void onAdLoaded(Ad ad) {
        }

        public void onAdClicked(Ad ad) {
        }

        public void onLoggingImpression(Ad ad) {
            LogUtils.i("Ad_SDK", "FaceBookAd:onLoggingImpression---AdView");
        }
    }

    /* access modifiers changed from: private */
    public void loadSingleAdMobAdInfo(Context context, String[] adMobIds, int idIndex, BaseModuleDataItemBean moduleDataItemBean, SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean, String tabCategory, AdmobAdConfig admobAdConfig, AdSdkParamsBuilder adSdkParams, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener) {
        Context adContext;
        if (sdkAdSourceRequestListener != null) {
            int finalIdIndex = idIndex + 1;
            if (adMobIds == null || adMobIds.length <= finalIdIndex) {
                sdkAdSourceRequestListener.onFinish(sdkAdSourceAdInfoBean);
                return;
            }
            final String adId = StringUtils.toString(adMobIds[finalIdIndex]);
            if (TextUtils.isEmpty(adId)) {
                loadSingleAdMobAdInfo(context, adMobIds, finalIdIndex, moduleDataItemBean, sdkAdSourceAdInfoBean, tabCategory, admobAdConfig, adSdkParams, sdkAdSourceRequestListener);
                return;
            }
            final long startTime = System.currentTimeMillis();
            AdSdkOperationStatistic.uploadAdRequestStatistic(context, adId, tabCategory, moduleDataItemBean, adSdkParams);
            if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean) || BaseModuleDataItemBean.isBannerAd300_250(moduleDataItemBean)) {
                final AdView adView = new AdView(context);
                AdSize adSize = AdSize.BANNER;
                if (BaseModuleDataItemBean.isBannerAd300_250(moduleDataItemBean)) {
                    adSize = AdSize.MEDIUM_RECTANGLE;
                }
                if (!(admobAdConfig == null || admobAdConfig.mBannerSize == null)) {
                    adSize = admobAdConfig.mBannerSize;
                }
                adView.setAdSize(adSize);
                adView.setAdUnitId(adId);
                final Context context2 = context;
                final String str = tabCategory;
                final BaseModuleDataItemBean baseModuleDataItemBean = moduleDataItemBean;
                final AdSdkParamsBuilder adSdkParamsBuilder = adSdkParams;
                final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean2 = sdkAdSourceAdInfoBean;
                final String[] strArr = adMobIds;
                final int i = finalIdIndex;
                final AdmobAdConfig admobAdConfig2 = admobAdConfig;
                final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener2 = sdkAdSourceRequestListener;
                adView.setAdListener(new com.google.android.gms.ads.AdListener() {
                    private boolean mIsAdLoaded = false;

                    public void onAdLoaded() {
                        SdkAdSourceListener.super.onAdLoaded();
                        if (!this.mIsAdLoaded) {
                            this.mIsAdLoaded = true;
                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context2, adId, str, 1, baseModuleDataItemBean, System.currentTimeMillis() - startTime, adSdkParamsBuilder);
                            try {
                                List<Object> adViewList = new ArrayList<>();
                                adViewList.add(adView);
                                sdkAdSourceAdInfoBean2.addAdViewList(adId, adViewList);
                                if (LogUtils.isShowLog()) {
                                    LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdLoaded---BannerAd, adId:" + adId + ", adId:" + adId + ", adViewSize:" + adViewList.size() + ", adView:" + adView + ")");
                                }
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context2, strArr, i, baseModuleDataItemBean, sdkAdSourceAdInfoBean2, str, admobAdConfig2, adSdkParamsBuilder, sdkAdSourceRequestListener2);
                            } catch (Exception e) {
                                e.printStackTrace();
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context2, strArr, i, baseModuleDataItemBean, sdkAdSourceAdInfoBean2, str, admobAdConfig2, adSdkParamsBuilder, sdkAdSourceRequestListener2);
                            } catch (Throwable th) {
                                Throwable th2 = th;
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context2, strArr, i, baseModuleDataItemBean, sdkAdSourceAdInfoBean2, str, admobAdConfig2, adSdkParamsBuilder, sdkAdSourceRequestListener2);
                                throw th2;
                            }
                        }
                    }

                    public void onAdFailedToLoad(int i) {
                        SdkAdSourceListener.super.onAdFailedToLoad(i);
                        AdSdkOperationStatistic.uploadAdRequestResultStatistic(context2, adId, str, -1, baseModuleDataItemBean, System.currentTimeMillis() - startTime, adSdkParamsBuilder);
                        if (LogUtils.isShowLog()) {
                            LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdFailedToLoad---BannerAd, adId:" + adId + ", i:" + i + ")");
                        }
                        SdkAdSourceListener.this.loadSingleAdMobAdInfo(context2, strArr, i, baseModuleDataItemBean, sdkAdSourceAdInfoBean2, str, admobAdConfig2, adSdkParamsBuilder, sdkAdSourceRequestListener2);
                    }

                    public void onAdClosed() {
                        sdkAdSourceRequestListener2.onAdClosed(adView);
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdClosed---BannerAd, adId:" + adId + ")");
                        }
                    }

                    public void onAdOpened() {
                        sdkAdSourceRequestListener2.onAdClicked(adView);
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdOpened---BannerAd, adId:" + adId + ")");
                        }
                    }
                });
                AdRequest.Builder adRequestBuilder = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                String url = admobAdConfig != null ? admobAdConfig.mContentUrl : null;
                if (!StringUtils.isEmpty(url)) {
                    try {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(AdView-setContentUrl---:" + url + ")");
                        }
                        adRequestBuilder.setContentUrl(url);
                    } catch (Throwable thr) {
                        LogUtils.w("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(AdView-exception)", thr);
                    }
                }
                adView.loadAd(adRequestBuilder.build());
            } else if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
                final InterstitialAd interstitialAd = new InterstitialAd(context);
                interstitialAd.setAdUnitId(adId);
                final Context context3 = context;
                final String str2 = adId;
                final String str3 = tabCategory;
                final BaseModuleDataItemBean baseModuleDataItemBean2 = moduleDataItemBean;
                final long j = startTime;
                final AdSdkParamsBuilder adSdkParamsBuilder2 = adSdkParams;
                final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean3 = sdkAdSourceAdInfoBean;
                final String[] strArr2 = adMobIds;
                final int i2 = finalIdIndex;
                final AdmobAdConfig admobAdConfig3 = admobAdConfig;
                final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener3 = sdkAdSourceRequestListener;
                interstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
                    public void onAdLoaded() {
                        SdkAdSourceListener.super.onAdLoaded();
                        AdSdkOperationStatistic.uploadAdRequestResultStatistic(context3, str2, str3, 1, baseModuleDataItemBean2, System.currentTimeMillis() - j, adSdkParamsBuilder2);
                        try {
                            List<Object> adViewList = new ArrayList<>();
                            adViewList.add(interstitialAd);
                            sdkAdSourceAdInfoBean3.addAdViewList(str2, adViewList);
                            if (LogUtils.isShowLog()) {
                                LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdLoaded---InterstitialAd, adId:" + str2 + ", adId:" + str2 + ", adViewSize:" + adViewList.size() + ", adView:" + interstitialAd + ")");
                            }
                            SdkAdSourceListener.this.loadSingleAdMobAdInfo(context3, strArr2, i2, baseModuleDataItemBean2, sdkAdSourceAdInfoBean3, str3, admobAdConfig3, adSdkParamsBuilder2, sdkAdSourceRequestListener3);
                        } catch (Exception e) {
                            e.printStackTrace();
                            SdkAdSourceListener.this.loadSingleAdMobAdInfo(context3, strArr2, i2, baseModuleDataItemBean2, sdkAdSourceAdInfoBean3, str3, admobAdConfig3, adSdkParamsBuilder2, sdkAdSourceRequestListener3);
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            SdkAdSourceListener.this.loadSingleAdMobAdInfo(context3, strArr2, i2, baseModuleDataItemBean2, sdkAdSourceAdInfoBean3, str3, admobAdConfig3, adSdkParamsBuilder2, sdkAdSourceRequestListener3);
                            throw th2;
                        }
                    }

                    public void onAdFailedToLoad(int i) {
                        SdkAdSourceListener.super.onAdFailedToLoad(i);
                        AdSdkOperationStatistic.uploadAdRequestResultStatistic(context3, str2, str3, -1, baseModuleDataItemBean2, System.currentTimeMillis() - j, adSdkParamsBuilder2);
                        if (LogUtils.isShowLog()) {
                            LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdFailedToLoad---InterstitialAd, adId:" + str2 + ", i:" + i + ")");
                        }
                        SdkAdSourceListener.this.loadSingleAdMobAdInfo(context3, strArr2, i2, baseModuleDataItemBean2, sdkAdSourceAdInfoBean3, str3, admobAdConfig3, adSdkParamsBuilder2, sdkAdSourceRequestListener3);
                    }

                    public void onAdClosed() {
                        sdkAdSourceRequestListener3.onAdClosed(interstitialAd);
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdClosed---InterstitialAd, adId:" + str2 + ")");
                        }
                    }

                    public void onAdOpened() {
                        sdkAdSourceRequestListener3.onAdShowed(interstitialAd);
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdOpened---InterstitialAd, adId:" + str2 + ")");
                        }
                    }

                    public void onAdLeftApplication() {
                        sdkAdSourceRequestListener3.onAdClicked(interstitialAd);
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdLeftApplication---InterstitialAd, adId:" + str2 + ")");
                        }
                    }
                });
                AdRequest.Builder adRequestBuilder2 = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                String url2 = admobAdConfig != null ? admobAdConfig.mContentUrl : null;
                if (!StringUtils.isEmpty(url2)) {
                    try {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(InterstitialAd-setContentUrl---:" + url2 + ")");
                        }
                        adRequestBuilder2.setContentUrl(url2);
                    } catch (Throwable thr2) {
                        LogUtils.w("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(InterstitialAd-exception)", thr2);
                    }
                }
                try {
                    interstitialAd.loadAd(adRequestBuilder2.build());
                } catch (Throwable e) {
                    if (LogUtils.isShowLog()) {
                        LogUtils.e("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(Exception---InterstitialAd, adId:" + adId + ")", e);
                    }
                    AdSdkOperationStatistic.uploadAdRequestResultStatistic(context, adId, tabCategory, -1, moduleDataItemBean, System.currentTimeMillis() - startTime, adSdkParams);
                    loadSingleAdMobAdInfo(context, adMobIds, finalIdIndex, moduleDataItemBean, sdkAdSourceAdInfoBean, tabCategory, admobAdConfig, adSdkParams, sdkAdSourceRequestListener);
                }
            } else if (admobAdConfig == null || !admobAdConfig.mUseNativeAdExpress) {
                boolean returnUrlsForImageAssets = admobAdConfig != null ? admobAdConfig.mReturnUrlsForImageAssets : false;
                final Context context4 = context;
                final String str4 = adId;
                final String str5 = tabCategory;
                final BaseModuleDataItemBean baseModuleDataItemBean3 = moduleDataItemBean;
                final long j2 = startTime;
                final AdSdkParamsBuilder adSdkParamsBuilder3 = adSdkParams;
                final String[] strArr3 = adMobIds;
                final int i3 = finalIdIndex;
                final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean4 = sdkAdSourceAdInfoBean;
                final AdmobAdConfig admobAdConfig4 = admobAdConfig;
                final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener4 = sdkAdSourceRequestListener;
                AdMobNativeAdListener adListener = new AdMobNativeAdListener() {
                    public void onAdFailedToLoad(int errorCode) {
                        AdSdkOperationStatistic.uploadAdRequestResultStatistic(context4, str4, str5, -1, baseModuleDataItemBean3, System.currentTimeMillis() - j2, adSdkParamsBuilder3);
                        if (LogUtils.isShowLog()) {
                            LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadSingleAdMobAdInfo(NativeAd---Failed to load NativeAd:, adId:" + str4 + ", errorCode:" + errorCode + ")");
                        }
                        SdkAdSourceListener.this.loadSingleAdMobAdInfo(context4, strArr3, i3, baseModuleDataItemBean3, sdkAdSourceAdInfoBean4, str5, admobAdConfig4, adSdkParamsBuilder3, sdkAdSourceRequestListener4);
                    }

                    public void onAdClosed() {
                        sdkAdSourceRequestListener4.onAdClosed(getAdObject());
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdClosed---NativeAd, adId:" + str4 + ")");
                        }
                    }

                    public void onAdOpened() {
                        sdkAdSourceRequestListener4.onAdShowed(getAdObject());
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdOpened---NativeAd, adId:" + str4 + ")");
                        }
                    }

                    public void onAdLeftApplication() {
                        sdkAdSourceRequestListener4.onAdClicked(getAdObject());
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdLeftApplication---NativeAd, adId:" + str4 + ")");
                        }
                    }
                };
                try {
                    if (context instanceof Activity) {
                        adContext = context.getApplicationContext();
                    } else {
                        adContext = context;
                    }
                    final Context context5 = context;
                    final String str6 = adId;
                    final String str7 = tabCategory;
                    final BaseModuleDataItemBean baseModuleDataItemBean4 = moduleDataItemBean;
                    final long j3 = startTime;
                    final AdSdkParamsBuilder adSdkParamsBuilder4 = adSdkParams;
                    final AdMobNativeAdListener adMobNativeAdListener = adListener;
                    final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean5 = sdkAdSourceAdInfoBean;
                    final String[] strArr4 = adMobIds;
                    final int i4 = finalIdIndex;
                    final AdmobAdConfig admobAdConfig5 = admobAdConfig;
                    final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener5 = sdkAdSourceRequestListener;
                    final Context context6 = context;
                    final String str8 = adId;
                    final String str9 = tabCategory;
                    final BaseModuleDataItemBean baseModuleDataItemBean5 = moduleDataItemBean;
                    final long j4 = startTime;
                    final AdSdkParamsBuilder adSdkParamsBuilder5 = adSdkParams;
                    final AdMobNativeAdListener adMobNativeAdListener2 = adListener;
                    final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean6 = sdkAdSourceAdInfoBean;
                    final String[] strArr5 = adMobIds;
                    final int i5 = finalIdIndex;
                    final AdmobAdConfig admobAdConfig6 = admobAdConfig;
                    final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener6 = sdkAdSourceRequestListener;
                    AdLoader adLoader = new AdLoader.Builder(adContext, adId).forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                        public void onContentAdLoaded(NativeContentAd ad) {
                            try {
                                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context5, str6, str7, 1, baseModuleDataItemBean4, System.currentTimeMillis() - j3, adSdkParamsBuilder4);
                                adMobNativeAdListener.setAdObject(ad);
                                List<Object> adViewList = new ArrayList<>();
                                adViewList.add(ad);
                                sdkAdSourceAdInfoBean5.addAdViewList(str6, adViewList);
                                if (LogUtils.isShowLog()) {
                                    LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean4.getVirtualModuleId() + "]loadSingleAdMobAdInfo(NativeAd---onContentAdLoaded, adId:" + str6 + "NativeContentAd:" + ad + ")");
                                }
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context5, strArr4, i4, baseModuleDataItemBean4, sdkAdSourceAdInfoBean5, str7, admobAdConfig5, adSdkParamsBuilder4, sdkAdSourceRequestListener5);
                            } catch (Exception e) {
                                e.printStackTrace();
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context5, strArr4, i4, baseModuleDataItemBean4, sdkAdSourceAdInfoBean5, str7, admobAdConfig5, adSdkParamsBuilder4, sdkAdSourceRequestListener5);
                            } catch (Throwable th) {
                                Throwable th2 = th;
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context5, strArr4, i4, baseModuleDataItemBean4, sdkAdSourceAdInfoBean5, str7, admobAdConfig5, adSdkParamsBuilder4, sdkAdSourceRequestListener5);
                                throw th2;
                            }
                        }
                    }).forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                        public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                            try {
                                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context6, str8, str9, 1, baseModuleDataItemBean5, System.currentTimeMillis() - j4, adSdkParamsBuilder5);
                                adMobNativeAdListener2.setAdObject(ad);
                                List<Object> adViewList = new ArrayList<>();
                                adViewList.add(ad);
                                sdkAdSourceAdInfoBean6.addAdViewList(str8, adViewList);
                                if (LogUtils.isShowLog()) {
                                    LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean5.getVirtualModuleId() + "]loadSingleAdMobAdInfo(NativeAd---NativeAppInstallAd, adId:" + str8 + "NativeAppInstallAd:" + ad + ")");
                                }
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context6, strArr5, i5, baseModuleDataItemBean5, sdkAdSourceAdInfoBean6, str9, admobAdConfig6, adSdkParamsBuilder5, sdkAdSourceRequestListener6);
                            } catch (Exception e) {
                                e.printStackTrace();
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context6, strArr5, i5, baseModuleDataItemBean5, sdkAdSourceAdInfoBean6, str9, admobAdConfig6, adSdkParamsBuilder5, sdkAdSourceRequestListener6);
                            } catch (Throwable th) {
                                Throwable th2 = th;
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context6, strArr5, i5, baseModuleDataItemBean5, sdkAdSourceAdInfoBean6, str9, admobAdConfig6, adSdkParamsBuilder5, sdkAdSourceRequestListener6);
                                throw th2;
                            }
                        }
                    }).withNativeAdOptions(new NativeAdOptions.Builder().setReturnUrlsForImageAssets(returnUrlsForImageAssets).build()).withAdListener(adListener).build();
                    PublisherAdRequest.Builder adRequestBuilder3 = new PublisherAdRequest.Builder();
                    String url3 = admobAdConfig != null ? admobAdConfig.mContentUrl : null;
                    if (!StringUtils.isEmpty(url3)) {
                        try {
                            if (LogUtils.isShowLog()) {
                                LogUtils.i("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(NativeAd-setContentUrl---:" + url3 + ")");
                            }
                            adRequestBuilder3.setContentUrl(url3);
                        } catch (Throwable thr3) {
                            LogUtils.w("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(NativeAd-exception)", thr3);
                        }
                    }
                    adLoader.loadAd(adRequestBuilder3.build());
                } catch (NullPointerException e2) {
                    LogUtils.e("Ad_SDK", "gms AdLoader.Builder error", e2);
                    AdSdkOperationStatistic.uploadAdRequestResultStatistic(context, adId, tabCategory, -1, moduleDataItemBean, System.currentTimeMillis() - startTime, adSdkParams);
                    if (LogUtils.isShowLog()) {
                        LogUtils.w("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(NativeAd---Failed to load NativeAd:, adId:" + adId + ")");
                    }
                    loadSingleAdMobAdInfo(context, adMobIds, finalIdIndex, moduleDataItemBean, sdkAdSourceAdInfoBean, tabCategory, admobAdConfig, adSdkParams, sdkAdSourceRequestListener);
                }
            } else {
                final AdView adView2 = new AdView(context);
                AdSize adSize2 = AdSize.BANNER;
                if (!(admobAdConfig == null || admobAdConfig.mBannerSize == null)) {
                    adSize2 = admobAdConfig.mBannerSize;
                }
                adView2.setAdSize(adSize2);
                adView2.setAdUnitId(adId);
                final Context context7 = context;
                final String str10 = tabCategory;
                final BaseModuleDataItemBean baseModuleDataItemBean6 = moduleDataItemBean;
                final AdSdkParamsBuilder adSdkParamsBuilder6 = adSdkParams;
                final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean7 = sdkAdSourceAdInfoBean;
                final String[] strArr6 = adMobIds;
                final int i6 = finalIdIndex;
                final AdmobAdConfig admobAdConfig7 = admobAdConfig;
                final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener7 = sdkAdSourceRequestListener;
                adView2.setAdListener(new com.google.android.gms.ads.AdListener() {
                    private boolean mIsAdLoaded = false;

                    public void onAdLoaded() {
                        SdkAdSourceListener.super.onAdLoaded();
                        if (!this.mIsAdLoaded) {
                            this.mIsAdLoaded = true;
                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context7, adId, str10, 1, baseModuleDataItemBean6, System.currentTimeMillis() - startTime, adSdkParamsBuilder6);
                            try {
                                List<Object> adViewList = new ArrayList<>();
                                adViewList.add(adView2);
                                sdkAdSourceAdInfoBean7.addAdViewList(adId, adViewList);
                                if (LogUtils.isShowLog()) {
                                    LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean6.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdLoaded---NativeExpressBannerAd, adId:" + adId + ", adId:" + adId + ", adViewSize:" + adViewList.size() + ", adView:" + adView2 + ")");
                                }
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context7, strArr6, i6, baseModuleDataItemBean6, sdkAdSourceAdInfoBean7, str10, admobAdConfig7, adSdkParamsBuilder6, sdkAdSourceRequestListener7);
                            } catch (Exception e) {
                                e.printStackTrace();
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context7, strArr6, i6, baseModuleDataItemBean6, sdkAdSourceAdInfoBean7, str10, admobAdConfig7, adSdkParamsBuilder6, sdkAdSourceRequestListener7);
                            } catch (Throwable th) {
                                Throwable th2 = th;
                                SdkAdSourceListener.this.loadSingleAdMobAdInfo(context7, strArr6, i6, baseModuleDataItemBean6, sdkAdSourceAdInfoBean7, str10, admobAdConfig7, adSdkParamsBuilder6, sdkAdSourceRequestListener7);
                                throw th2;
                            }
                        }
                    }

                    public void onAdFailedToLoad(int i) {
                        SdkAdSourceListener.super.onAdFailedToLoad(i);
                        AdSdkOperationStatistic.uploadAdRequestResultStatistic(context7, adId, str10, -1, baseModuleDataItemBean6, System.currentTimeMillis() - startTime, adSdkParamsBuilder6);
                        if (LogUtils.isShowLog()) {
                            LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean6.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdFailedToLoad---NativeExpressBannerAd, adId:" + adId + ", i:" + i + ")");
                        }
                        SdkAdSourceListener.this.loadSingleAdMobAdInfo(context7, strArr6, i6, baseModuleDataItemBean6, sdkAdSourceAdInfoBean7, str10, admobAdConfig7, adSdkParamsBuilder6, sdkAdSourceRequestListener7);
                    }

                    public void onAdClosed() {
                        sdkAdSourceRequestListener7.onAdClosed(adView2);
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean6.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdClosed---NativeExpressBannerAd, adId:" + adId + ")");
                        }
                    }

                    public void onAdOpened() {
                        sdkAdSourceRequestListener7.onAdClicked(adView2);
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean6.getVirtualModuleId() + "]loadSingleAdMobAdInfo(onAdOpened---NativeExpressBannerAd, adId:" + adId + ")");
                        }
                    }
                });
                AdRequest.Builder adRequestBuilder4 = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                String url4 = admobAdConfig != null ? admobAdConfig.mContentUrl : null;
                if (!StringUtils.isEmpty(url4)) {
                    try {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(ExpressAdView-setContentUrl---:" + url4 + ")");
                        }
                        adRequestBuilder4.setContentUrl(url4);
                    } catch (Throwable thr4) {
                        LogUtils.w("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadSingleAdMobAdInfo(ExpressAdView-exception)", thr4);
                    }
                }
                adView2.loadAd(adRequestBuilder4.build());
            }
        }
    }

    private static class AdMobNativeAdListener extends com.google.android.gms.ads.AdListener {
        private Object mAdObject;

        private AdMobNativeAdListener() {
        }

        public Object getAdObject() {
            return this.mAdObject;
        }

        public void setAdObject(Object mAdObject2) {
            this.mAdObject = mAdObject2;
        }
    }

    /* access modifiers changed from: private */
    public void loadSingleLoopMeAdInfo(Context context, String[] loopMeIds, int idIndex, BaseModuleDataItemBean moduleDataItemBean, SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean, String tabCategory, AdSdkParamsBuilder adSdkParams, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener) {
        if (sdkAdSourceRequestListener != null) {
            int finalIdIndex = idIndex + 1;
            if (loopMeIds == null || loopMeIds.length <= finalIdIndex) {
                sdkAdSourceRequestListener.onFinish(sdkAdSourceAdInfoBean);
                return;
            }
            final String adId = StringUtils.toString(loopMeIds[finalIdIndex]);
            if (TextUtils.isEmpty(adId)) {
                loadSingleLoopMeAdInfo(context, loopMeIds, finalIdIndex, moduleDataItemBean, sdkAdSourceAdInfoBean, tabCategory, adSdkParams, sdkAdSourceRequestListener);
                return;
            }
            final long startTime = System.currentTimeMillis();
            AdSdkOperationStatistic.uploadAdRequestStatistic(context, adId, tabCategory, moduleDataItemBean, adSdkParams);
            if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
                LoopMeInterstitial loopMeInterstitial = LoopMeInterstitial.getInstance(adId, context);
                final Context context2 = context;
                final String str = tabCategory;
                final BaseModuleDataItemBean baseModuleDataItemBean = moduleDataItemBean;
                final AdSdkParamsBuilder adSdkParamsBuilder = adSdkParams;
                final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean2 = sdkAdSourceAdInfoBean;
                final String[] strArr = loopMeIds;
                final int i = finalIdIndex;
                final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener2 = sdkAdSourceRequestListener;
                loopMeInterstitial.setListener(new LoopMeInterstitial.Listener() {
                    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
                        try {
                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context2, adId, str, 1, baseModuleDataItemBean, System.currentTimeMillis() - startTime, adSdkParamsBuilder);
                            List<Object> adViewList = new ArrayList<>();
                            adViewList.add(interstitial);
                            sdkAdSourceAdInfoBean2.addAdViewList(adId, adViewList);
                            if (LogUtils.isShowLog()) {
                                LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadSingleLoopMeAdInfo(onLoopMeInterstitialLoadSuccess---InterstitialAd, adId:" + adId + ", adViewSize:" + adViewList.size() + ", adView:" + interstitial + "," + (sdkAdSourceAdInfoBean2.getAdViewList() != null ? sdkAdSourceAdInfoBean2.getAdViewList().size() : -2) + ")");
                            }
                            SdkAdSourceListener.this.loadSingleLoopMeAdInfo(context2, strArr, i, baseModuleDataItemBean, sdkAdSourceAdInfoBean2, str, adSdkParamsBuilder, sdkAdSourceRequestListener2);
                        } catch (Exception e) {
                            e.printStackTrace();
                            SdkAdSourceListener.this.loadSingleLoopMeAdInfo(context2, strArr, i, baseModuleDataItemBean, sdkAdSourceAdInfoBean2, str, adSdkParamsBuilder, sdkAdSourceRequestListener2);
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            SdkAdSourceListener.this.loadSingleLoopMeAdInfo(context2, strArr, i, baseModuleDataItemBean, sdkAdSourceAdInfoBean2, str, adSdkParamsBuilder, sdkAdSourceRequestListener2);
                            throw th2;
                        }
                    }

                    public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, int error) {
                        AdSdkOperationStatistic.uploadAdRequestResultStatistic(context2, adId, str, -1, baseModuleDataItemBean, System.currentTimeMillis() - startTime, adSdkParamsBuilder);
                        if (LogUtils.isShowLog()) {
                            LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadSingleLoopMeAdInfo(onError---InterstitialAd, adId:" + adId + ", ad:" + interstitial + ", aderror:" + error + ")");
                        }
                        SdkAdSourceListener.this.loadSingleLoopMeAdInfo(context2, strArr, i, baseModuleDataItemBean, sdkAdSourceAdInfoBean2, str, adSdkParamsBuilder, sdkAdSourceRequestListener2);
                    }

                    public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadSingleLoopMeAdInfo(onInterstitialDisplayed---InterstitialAd, adId:" + adId + ", ad:" + interstitial + ")");
                        }
                        sdkAdSourceRequestListener2.onAdShowed(interstitial);
                    }

                    public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadSingleLoopMeAdInfo(onInterstitialDismissed---InterstitialAd, adId:" + adId + ", ad:" + interstitial + ")");
                        }
                        sdkAdSourceRequestListener2.onAdClosed(interstitial);
                    }

                    public void onLoopMeInterstitialClicked(LoopMeInterstitial interstitial) {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadSingleLoopMeAdInfo(onAdClicked---InterstitialAd, adId:" + adId + ", ad:" + interstitial + ")");
                        }
                        sdkAdSourceRequestListener2.onAdClicked(interstitial);
                    }

                    public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial interstitial) {
                    }

                    public void onLoopMeInterstitialExpired(LoopMeInterstitial interstitial) {
                    }

                    public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial arg0) {
                    }
                });
                loopMeInterstitial.load();
            } else if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean)) {
                try {
                    LoopMeBanner loopMeBanner = LoopMeBanner.getInstance(adId, context);
                    final Context context3 = context;
                    final String str2 = tabCategory;
                    final BaseModuleDataItemBean baseModuleDataItemBean2 = moduleDataItemBean;
                    final AdSdkParamsBuilder adSdkParamsBuilder2 = adSdkParams;
                    final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean3 = sdkAdSourceAdInfoBean;
                    final String[] strArr2 = loopMeIds;
                    final int i2 = finalIdIndex;
                    final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener3 = sdkAdSourceRequestListener;
                    loopMeBanner.setListener(new LoopMeBanner.Listener() {
                        public void onLoopMeBannerLoadSuccess(LoopMeBanner banner) {
                            try {
                                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context3, adId, str2, 1, baseModuleDataItemBean2, System.currentTimeMillis() - startTime, adSdkParamsBuilder2);
                                List<Object> adViewList = new ArrayList<>();
                                adViewList.add(banner);
                                sdkAdSourceAdInfoBean3.addAdViewList(adId, adViewList);
                                if (LogUtils.isShowLog()) {
                                    LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadSingleLoopMeAdInfo(onAdLoaded---LoopMeBanner, adId:" + adId + ")");
                                }
                                SdkAdSourceListener.this.loadSingleLoopMeAdInfo(context3, strArr2, i2, baseModuleDataItemBean2, sdkAdSourceAdInfoBean3, str2, adSdkParamsBuilder2, sdkAdSourceRequestListener3);
                            } catch (Exception e) {
                                e.printStackTrace();
                                SdkAdSourceListener.this.loadSingleLoopMeAdInfo(context3, strArr2, i2, baseModuleDataItemBean2, sdkAdSourceAdInfoBean3, str2, adSdkParamsBuilder2, sdkAdSourceRequestListener3);
                            } catch (Throwable th) {
                                Throwable th2 = th;
                                SdkAdSourceListener.this.loadSingleLoopMeAdInfo(context3, strArr2, i2, baseModuleDataItemBean2, sdkAdSourceAdInfoBean3, str2, adSdkParamsBuilder2, sdkAdSourceRequestListener3);
                                throw th2;
                            }
                        }

                        public void onLoopMeBannerLoadFail(LoopMeBanner banner, int error) {
                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context3, adId, str2, -1, baseModuleDataItemBean2, System.currentTimeMillis() - startTime, adSdkParamsBuilder2);
                            if (LogUtils.isShowLog()) {
                                LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadSingleLoopMeAdInfo(onError---LoopMeBanner, adId:" + adId + ", ad:" + banner + ", aderror:" + LoopMeError.getCodeMessage(error) + ")");
                            }
                            SdkAdSourceListener.this.loadSingleLoopMeAdInfo(context3, strArr2, i2, baseModuleDataItemBean2, sdkAdSourceAdInfoBean3, str2, adSdkParamsBuilder2, sdkAdSourceRequestListener3);
                        }

                        public void onLoopMeBannerShow(LoopMeBanner banner) {
                            if (LogUtils.isShowLog()) {
                                LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadSingleLoopMeAdInfo(onLoopMeBannerShow---LoopMeBanner, adId:" + adId + ", ad:" + banner + ")");
                            }
                            sdkAdSourceRequestListener3.onAdShowed(banner);
                        }

                        public void onLoopMeBannerHide(LoopMeBanner banner) {
                            if (LogUtils.isShowLog()) {
                                LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadSingleLoopMeAdInfo(onLoopMeBannerHide---LoopMeBanner, adId:" + adId + ", ad:" + banner + ")");
                            }
                            sdkAdSourceRequestListener3.onAdClosed(banner);
                        }

                        public void onLoopMeBannerClicked(LoopMeBanner banner) {
                            if (LogUtils.isShowLog()) {
                                LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadSingleLoopMeAdInfo(onLoopMeBannerClicked---LoopMeBanner, adId:" + adId + ", ad:" + banner + ")");
                            }
                            sdkAdSourceRequestListener3.onAdClicked(banner);
                        }

                        public void onLoopMeBannerLeaveApp(LoopMeBanner banner) {
                        }

                        public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner banner) {
                        }

                        public void onLoopMeBannerExpired(LoopMeBanner banner) {
                        }
                    });
                    loopMeBanner.load();
                } catch (Exception e) {
                    AdSdkOperationStatistic.uploadAdRequestResultStatistic(context, adId, tabCategory, -1, moduleDataItemBean, System.currentTimeMillis() - startTime, adSdkParams);
                    if (LogUtils.isShowLog()) {
                        LogUtils.w("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadSingleLoopMeAdInfo(onError---LoopMeBanner, adId:" + adId + ")", e);
                    }
                    loadSingleLoopMeAdInfo(context, loopMeIds, finalIdIndex, moduleDataItemBean, sdkAdSourceAdInfoBean, tabCategory, adSdkParams, sdkAdSourceRequestListener);
                }
            } else {
                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context, adId, tabCategory, -1, moduleDataItemBean, System.currentTimeMillis() - startTime, adSdkParams);
                loadSingleLoopMeAdInfo(context, loopMeIds, finalIdIndex, moduleDataItemBean, sdkAdSourceAdInfoBean, tabCategory, adSdkParams, sdkAdSourceRequestListener);
            }
        }
    }

    public void loadIronScrAdInfo(AdSdkParamsBuilder adSdkParams, BaseModuleDataItemBean moduleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener) {
        if (sdkAdSourceRequestListener != null) {
            final Context context = adSdkParams.mContext;
            final int virtualModuleId = moduleDataItemBean != null ? moduleDataItemBean.getVirtualModuleId() : -1;
            if (!NetworkUtils.isNetworkOK(context)) {
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadIronScrAdInfo(广告加载失败，因网络问题，仅返回模块控制信息!");
                }
                sdkAdSourceRequestListener.onFinish(new SdkAdSourceAdInfoBean());
                return;
            }
            final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener2 = sdkAdSourceRequestListener;
            final AdSdkParamsBuilder adSdkParamsBuilder = adSdkParams;
            final BaseModuleDataItemBean baseModuleDataItemBean = moduleDataItemBean;
            IronScrAd.systemSupportWebView(context, new IronScrAd.IWebViewCheckListener() {
                public void onChecked(boolean isSupport) {
                    if (!isSupport) {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadIronScrAdInfo(广告加载失败，因手机系统不支持WebView!");
                        }
                        sdkAdSourceRequestListener2.onFinish(new SdkAdSourceAdInfoBean());
                        return;
                    }
                    final TimeOutGuard timeOutGurad = new TimeOutGuard();
                    timeOutGurad.start(IronScrAd.IronScrAdConfig.getTimeOut(adSdkParamsBuilder.mIronScrAdConfig), new TimeOutGuard.TimeOutTask() {
                        public void onTimeOut() {
                            LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadIronScrAdInfo:time out");
                            sdkAdSourceRequestListener2.onFinish((SdkAdSourceAdInfoBean) null);
                        }
                    }, (Object) null);
                    final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean = new SdkAdSourceAdInfoBean();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            IronScrAd ad = new IronScrAd(context, adSdkParamsBuilder.mIronScrAdConfig);
                            ad.setAdListener(new IronScrAd.IIronScrListener() {
                                public void onSuccess(IronScrAd adView) {
                                    if (!timeOutGurad.hadTimeOut()) {
                                        timeOutGurad.cancel();
                                        List<Object> adViewList = new ArrayList<>();
                                        adViewList.add(adView);
                                        sdkAdSourceAdInfoBean.addAdViewList("ironScr", adViewList);
                                        if (LogUtils.isShowLog()) {
                                            LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadIronScrAdInfo(onAdLoaded)");
                                        }
                                        sdkAdSourceRequestListener2.onFinish(sdkAdSourceAdInfoBean);
                                    } else if (adView != null) {
                                        adView.destroy();
                                    }
                                }

                                public void onFail(String msg) {
                                    if (LogUtils.isShowLog()) {
                                        LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadIronScrAdInfo(Failed to load Ad)" + ", errorMsg:" + msg + ")");
                                    }
                                    if (!timeOutGurad.hadTimeOut()) {
                                        timeOutGurad.cancel();
                                        sdkAdSourceRequestListener2.onFinish(new SdkAdSourceAdInfoBean());
                                    }
                                }

                                public void onComplete() {
                                    if (LogUtils.isShowLog()) {
                                        LogUtils.d("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]loadIronScrAdInfo onComplete)");
                                    }
                                    sdkAdSourceRequestListener2.onAdClosed((Object) null);
                                }
                            });
                            ad.loadAd();
                        }
                    });
                }
            });
        }
    }

    public void loadMoPubAdInfo(AdSdkParamsBuilder adSdkParams, BaseModuleDataItemBean moduleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener) {
        final MoPubNativeConfig mnc;
        if (sdkAdSourceRequestListener != null) {
            final Context context = adSdkParams.mContext;
            final int virtualModuleId = moduleDataItemBean != null ? moduleDataItemBean.getVirtualModuleId() : -1;
            if (!NetworkUtils.isNetworkOK(context)) {
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadMoPubAdInfo(广告加载失败，因网络问题，仅返回模块控制信息!");
                }
                sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                return;
            }
            String adClassName = "com.mopub.nativeads.MoPubNative";
            if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
                adClassName = "com.mopub.mobileads.MoPubInterstitial";
            }
            if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean)) {
                adClassName = "com.mopub.mobileads.MoPubView";
            }
            try {
                Class<?> adViewClass = Class.forName(adClassName);
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadMoPubAdInfo(" + adClassName + ", " + adViewClass.getName() + ")");
                }
                String[] faceBookIds = moduleDataItemBean != null ? moduleDataItemBean.getFbIds() : null;
                final String adRequestId = (faceBookIds == null || faceBookIds.length <= 0) ? null : faceBookIds[0];
                if (context == null || StringUtils.isEmpty(adRequestId)) {
                    LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadMoPubAdInfo(ad id is null!)");
                    sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                    return;
                }
                final long startTime = System.currentTimeMillis();
                final TimeOutGuard timeOutGurad = new TimeOutGuard();
                final AdSdkParamsBuilder adSdkParamsBuilder = adSdkParams;
                final BaseModuleDataItemBean baseModuleDataItemBean = moduleDataItemBean;
                final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener2 = sdkAdSourceRequestListener;
                timeOutGurad.start(AbsAdConfig.getTimeOut(adSdkParams.mMoPubAdConfig), new TimeOutGuard.TimeOutTask() {
                    public void onTimeOut() {
                        LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadMoPubAdInfo:time out, adId=" + adRequestId);
                        AdSdkOperationStatistic.uploadAdRequestResultStatistic(context, adRequestId, adSdkParamsBuilder.mTabCategory, -2, baseModuleDataItemBean, System.currentTimeMillis() - startTime, adSdkParamsBuilder);
                        sdkAdSourceRequestListener2.onFinish((SdkAdSourceAdInfoBean) null);
                    }
                }, (Object) null);
                AdSdkOperationStatistic.uploadAdRequestStatistic(context, adRequestId, adSdkParams.mTabCategory, moduleDataItemBean, adSdkParams);
                if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
                    final SdkAdContext sdkAdContext = context instanceof SdkAdContext ? (SdkAdContext) context : null;
                    final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean = new SdkAdSourceAdInfoBean();
                    final Context context2 = context;
                    final BaseModuleDataItemBean baseModuleDataItemBean2 = moduleDataItemBean;
                    final String str = adRequestId;
                    final AdSdkParamsBuilder adSdkParamsBuilder2 = adSdkParams;
                    final long j = startTime;
                    final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener3 = sdkAdSourceRequestListener;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            Activity activity = context2 instanceof Activity ? (Activity) context2 : null;
                            if (activity == null) {
                                activity = sdkAdContext != null ? sdkAdContext.getActivity() : null;
                            }
                            if (activity == null) {
                                LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadMoPubInterstitialAdInfo fail InterstitialAd needs Activity!)");
                                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context2, str, adSdkParamsBuilder2.mTabCategory, -1, baseModuleDataItemBean2, System.currentTimeMillis() - j, adSdkParamsBuilder2);
                                sdkAdSourceRequestListener3.onFinish((SdkAdSourceAdInfoBean) null);
                                return;
                            }
                            MoPubAdConfig mac = adSdkParamsBuilder2.mMoPubAdConfig;
                            String keywords = mac != null ? mac.mKeyWords : null;
                            MoPubInterstitial mpi = null;
                            try {
                                mpi = new MoPubInterstitial(activity, str);
                            } catch (Throwable thr) {
                                LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadMoPubInterstitialAdInfo(Throwable)", thr);
                            }
                            if (mpi == null) {
                                LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadMoPubInterstitialAdInfo(MoPubInterstitial Failed to load Ad)");
                                if (!timeOutGurad.hadTimeOut()) {
                                    timeOutGurad.cancel();
                                    AdSdkOperationStatistic.uploadAdRequestResultStatistic(context2, str, adSdkParamsBuilder2.mTabCategory, -1, baseModuleDataItemBean2, System.currentTimeMillis() - j, adSdkParamsBuilder2);
                                    sdkAdSourceRequestListener3.onFinish((SdkAdSourceAdInfoBean) null);
                                    return;
                                }
                                return;
                            }
                            mpi.setKeywords(keywords);
                            mpi.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                                private boolean mHasReturned = false;

                                public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                                    if (!this.mHasReturned) {
                                        this.mHasReturned = true;
                                        if (!timeOutGurad.hadTimeOut()) {
                                            timeOutGurad.cancel();
                                            List<Object> adViewList = new ArrayList<>();
                                            adViewList.add(interstitial);
                                            sdkAdSourceAdInfoBean.addAdViewList(str, adViewList);
                                            if (LogUtils.isShowLog()) {
                                                LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadMoPubInterstitialAdInfo(onAdLoaded)");
                                            }
                                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context2, str, adSdkParamsBuilder2.mTabCategory, 1, baseModuleDataItemBean2, System.currentTimeMillis() - j, adSdkParamsBuilder2);
                                            sdkAdSourceRequestListener3.onFinish(sdkAdSourceAdInfoBean);
                                        } else if (interstitial != null) {
                                            interstitial.destroy();
                                        }
                                    }
                                }

                                public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                                    if (!this.mHasReturned) {
                                        this.mHasReturned = true;
                                        if (LogUtils.isShowLog()) {
                                            LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadMoPubInterstitialAdInfo(Failed to load Ad)" + ", errorMsg:" + (errorCode != null ? errorCode.toString() : "") + ")");
                                        }
                                        if (interstitial != null) {
                                            interstitial.destroy();
                                        }
                                        if (!timeOutGurad.hadTimeOut()) {
                                            timeOutGurad.cancel();
                                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context2, str, adSdkParamsBuilder2.mTabCategory, -1, baseModuleDataItemBean2, System.currentTimeMillis() - j, adSdkParamsBuilder2);
                                            sdkAdSourceRequestListener3.onFinish((SdkAdSourceAdInfoBean) null);
                                        }
                                    }
                                }

                                public void onInterstitialShown(MoPubInterstitial interstitial) {
                                    if (LogUtils.isShowLog()) {
                                        LogUtils.d("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadMoPubInterstitialAdInfo onInterstitialShown)");
                                    }
                                    sdkAdSourceRequestListener3.onAdShowed(interstitial);
                                }

                                public void onInterstitialClicked(MoPubInterstitial interstitial) {
                                    if (LogUtils.isShowLog()) {
                                        LogUtils.d("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadMoPubInterstitialAdInfo onInterstitialClicked)");
                                    }
                                    sdkAdSourceRequestListener3.onAdClicked(interstitial);
                                }

                                public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                                    if (LogUtils.isShowLog()) {
                                        LogUtils.d("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadMoPubInterstitialAdInfo onInterstitialDismissed)");
                                    }
                                    sdkAdSourceRequestListener3.onAdClosed(interstitial);
                                }
                            });
                            try {
                                mpi.load();
                            } catch (Throwable thr2) {
                                if (LogUtils.isShowLog()) {
                                    LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean2.getVirtualModuleId() + "]loadMoPubInterstitialAdInfo(Exception)", thr2);
                                }
                                if (!timeOutGurad.hadTimeOut()) {
                                    timeOutGurad.cancel();
                                    AdSdkOperationStatistic.uploadAdRequestResultStatistic(context2, str, adSdkParamsBuilder2.mTabCategory, -1, baseModuleDataItemBean2, System.currentTimeMillis() - j, adSdkParamsBuilder2);
                                    sdkAdSourceRequestListener3.onFinish((SdkAdSourceAdInfoBean) null);
                                }
                            }
                        }
                    });
                } else if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean)) {
                    final AdSdkParamsBuilder adSdkParamsBuilder3 = adSdkParams;
                    final Context context3 = context;
                    final BaseModuleDataItemBean baseModuleDataItemBean3 = moduleDataItemBean;
                    final TimeOutGuard timeOutGuard = timeOutGurad;
                    final String str2 = adRequestId;
                    final long j2 = startTime;
                    final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener4 = sdkAdSourceRequestListener;
                    final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean2 = new SdkAdSourceAdInfoBean();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            MoPubAdConfig mac = adSdkParamsBuilder3.mMoPubAdConfig;
                            String keywords = mac != null ? mac.mKeyWords : null;
                            MoPubView mpv = null;
                            try {
                                mpv = new MoPubView(context3);
                            } catch (Throwable thr) {
                                LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadMoPubBannerAdInfo(Throwable)", thr);
                            }
                            if (mpv == null) {
                                LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadMoPubBannerAdInfo(Failed to load Ad)");
                                if (!timeOutGuard.hadTimeOut()) {
                                    timeOutGuard.cancel();
                                    AdSdkOperationStatistic.uploadAdRequestResultStatistic(context3, str2, adSdkParamsBuilder3.mTabCategory, -1, baseModuleDataItemBean3, System.currentTimeMillis() - j2, adSdkParamsBuilder3);
                                    sdkAdSourceRequestListener4.onFinish((SdkAdSourceAdInfoBean) null);
                                    return;
                                }
                                return;
                            }
                            mpv.setAdUnitId(str2);
                            mpv.setKeywords(keywords);
                            mpv.setBannerAdListener(new MoPubView.BannerAdListener() {
                                private boolean mHasReturned = false;

                                public void onBannerLoaded(MoPubView moPubView) {
                                    if (!this.mHasReturned) {
                                        this.mHasReturned = true;
                                        if (!timeOutGuard.hadTimeOut()) {
                                            timeOutGuard.cancel();
                                            List<Object> adViewList = new ArrayList<>();
                                            adViewList.add(moPubView);
                                            sdkAdSourceAdInfoBean2.addAdViewList(str2, adViewList);
                                            if (LogUtils.isShowLog()) {
                                                LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadMoPubBannerAdInfo(onAdLoaded)");
                                            }
                                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context3, str2, adSdkParamsBuilder3.mTabCategory, 1, baseModuleDataItemBean3, System.currentTimeMillis() - j2, adSdkParamsBuilder3);
                                            sdkAdSourceRequestListener4.onFinish(sdkAdSourceAdInfoBean2);
                                        } else if (moPubView != null) {
                                            moPubView.destroy();
                                        }
                                    }
                                }

                                public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
                                    if (!this.mHasReturned) {
                                        this.mHasReturned = true;
                                        if (LogUtils.isShowLog()) {
                                            LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadMoPubBannerAdInfo(Failed to load Ad)" + ", errorMsg:" + (moPubErrorCode != null ? moPubErrorCode.toString() : "") + ")");
                                        }
                                        if (moPubView != null) {
                                            moPubView.destroy();
                                        }
                                        if (!timeOutGuard.hadTimeOut()) {
                                            timeOutGuard.cancel();
                                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context3, str2, adSdkParamsBuilder3.mTabCategory, -1, baseModuleDataItemBean3, System.currentTimeMillis() - j2, adSdkParamsBuilder3);
                                            sdkAdSourceRequestListener4.onFinish((SdkAdSourceAdInfoBean) null);
                                        }
                                    }
                                }

                                public void onBannerClicked(MoPubView moPubView) {
                                    if (LogUtils.isShowLog()) {
                                        LogUtils.d("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadMoPubAdInfo onBannerClicked)");
                                    }
                                    sdkAdSourceRequestListener4.onAdClicked(moPubView);
                                }

                                public void onBannerExpanded(MoPubView moPubView) {
                                    if (LogUtils.isShowLog()) {
                                        LogUtils.d("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadMoPubAdInfo onBannerExpanded)");
                                    }
                                }

                                public void onBannerCollapsed(MoPubView moPubView) {
                                    if (LogUtils.isShowLog()) {
                                        LogUtils.d("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadMoPubAdInfo onBannerCollapsed)");
                                    }
                                }
                            });
                            try {
                                mpv.loadAd();
                            } catch (Throwable thr2) {
                                if (LogUtils.isShowLog()) {
                                    LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean3.getVirtualModuleId() + "]loadMoPubBannerAdInfo(Exception)", thr2);
                                }
                                if (!timeOutGuard.hadTimeOut()) {
                                    timeOutGuard.cancel();
                                    AdSdkOperationStatistic.uploadAdRequestResultStatistic(context3, str2, adSdkParamsBuilder3.mTabCategory, -1, baseModuleDataItemBean3, System.currentTimeMillis() - j2, adSdkParamsBuilder3);
                                    sdkAdSourceRequestListener4.onFinish((SdkAdSourceAdInfoBean) null);
                                }
                            }
                        }
                    });
                } else {
                    MoPubAdConfig mac = adSdkParams.mMoPubAdConfig;
                    final String keywords = mac != null ? mac.mKeyWords : null;
                    if (mac != null) {
                        mnc = mac.mMoPubNativeConfig;
                    } else {
                        mnc = new MoPubNativeConfig((MoPubAdRenderer) null, (EnumSet<RequestParameters.NativeAdAsset>) null);
                    }
                    final MoPubAdRenderer renderer = mnc.mMoPubAdRenderer;
                    if (renderer == null) {
                        if (LogUtils.isShowLog()) {
                            LogUtils.w("Ad_SDK", "[vmId:" + moduleDataItemBean.getVirtualModuleId() + "]loadMoPubNativeAdInfo(Failed to load Ad, MoPubAdRenderer is null, you should pass MoPubAdRenderer" + ")");
                        }
                        if (!timeOutGurad.hadTimeOut()) {
                            timeOutGurad.cancel();
                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context, adRequestId, adSdkParams.mTabCategory, -1, moduleDataItemBean, System.currentTimeMillis() - startTime, adSdkParams);
                            sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
                            return;
                        }
                        return;
                    }
                    final SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean3 = new SdkAdSourceAdInfoBean();
                    final Context context4 = context;
                    final String str3 = adRequestId;
                    final BaseModuleDataItemBean baseModuleDataItemBean4 = moduleDataItemBean;
                    final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener5 = sdkAdSourceRequestListener;
                    final AdSdkParamsBuilder adSdkParamsBuilder4 = adSdkParams;
                    final long j3 = startTime;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            EnumSet<RequestParameters.NativeAdAsset> assetsSet;
                            if (mnc.mAssetsSet != null) {
                                assetsSet = mnc.mAssetsSet;
                            } else {
                                assetsSet = EnumSet.of(RequestParameters.NativeAdAsset.TITLE, new RequestParameters.NativeAdAsset[]{RequestParameters.NativeAdAsset.TEXT, RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT, RequestParameters.NativeAdAsset.MAIN_IMAGE, RequestParameters.NativeAdAsset.ICON_IMAGE, RequestParameters.NativeAdAsset.STAR_RATING});
                            }
                            Location location = mnc.mLocation;
                            MoPubNative moPubNative = new MoPubNative(context4, str3, new MoPubNative.MoPubNativeNetworkListener() {
                                private boolean mHasReturned = false;

                                public void onNativeLoad(final NativeAd nativeAd) {
                                    if (!this.mHasReturned) {
                                        this.mHasReturned = true;
                                        if (!timeOutGurad.hadTimeOut()) {
                                            timeOutGurad.cancel();
                                            List<Object> adViewList = new ArrayList<>();
                                            adViewList.add(nativeAd);
                                            sdkAdSourceAdInfoBean3.addAdViewList(str3, adViewList);
                                            if (LogUtils.isShowLog()) {
                                                LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean4.getVirtualModuleId() + "]loadMoPubNativeAdInfo(onAdLoaded)");
                                            }
                                            nativeAd.setMoPubNativeEventListener(new NativeAd.MoPubNativeEventListener() {
                                                public void onImpression(View view) {
                                                    if (LogUtils.isShowLog()) {
                                                        LogUtils.d("Ad_SDK", "[vmId:" + baseModuleDataItemBean4.getVirtualModuleId() + "]loadMoPubNativeAdInfo onImpression)");
                                                    }
                                                    sdkAdSourceRequestListener5.onAdShowed(nativeAd);
                                                }

                                                public void onClick(View view) {
                                                    if (LogUtils.isShowLog()) {
                                                        LogUtils.d("Ad_SDK", "[vmId:" + baseModuleDataItemBean4.getVirtualModuleId() + "]loadMoPubNativeAdInfo onClick)");
                                                    }
                                                    sdkAdSourceRequestListener5.onAdClicked(nativeAd);
                                                }
                                            });
                                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context4, str3, adSdkParamsBuilder4.mTabCategory, 1, baseModuleDataItemBean4, System.currentTimeMillis() - j3, adSdkParamsBuilder4);
                                            sdkAdSourceRequestListener5.onFinish(sdkAdSourceAdInfoBean3);
                                        } else if (nativeAd != null) {
                                            nativeAd.destroy();
                                        }
                                    }
                                }

                                public void onNativeFail(NativeErrorCode errorCode) {
                                    if (!this.mHasReturned) {
                                        this.mHasReturned = true;
                                        if (LogUtils.isShowLog()) {
                                            LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean4.getVirtualModuleId() + "]loadMoPubNativeAdInfo(Failed to load Ad)" + ", errorMsg:" + (errorCode != null ? errorCode.toString() : "") + ")");
                                        }
                                        if (!timeOutGurad.hadTimeOut()) {
                                            timeOutGurad.cancel();
                                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context4, str3, adSdkParamsBuilder4.mTabCategory, -1, baseModuleDataItemBean4, System.currentTimeMillis() - j3, adSdkParamsBuilder4);
                                            sdkAdSourceRequestListener5.onFinish((SdkAdSourceAdInfoBean) null);
                                        }
                                    }
                                }
                            });
                            moPubNative.registerAdRenderer(renderer);
                            try {
                                moPubNative.makeRequest(new RequestParameters.Builder().keywords(keywords).location(location).desiredAssets(assetsSet).build());
                            } catch (Throwable thr) {
                                if (LogUtils.isShowLog()) {
                                    LogUtils.w("Ad_SDK", "[vmId:" + baseModuleDataItemBean4.getVirtualModuleId() + "]loadMoPubNativeAdInfo(Exception)", thr);
                                }
                                if (!timeOutGurad.hadTimeOut()) {
                                    timeOutGurad.cancel();
                                    AdSdkOperationStatistic.uploadAdRequestResultStatistic(context4, str3, adSdkParamsBuilder4.mTabCategory, -1, baseModuleDataItemBean4, System.currentTimeMillis() - j3, adSdkParamsBuilder4);
                                    sdkAdSourceRequestListener5.onFinish((SdkAdSourceAdInfoBean) null);
                                }
                            }
                        }
                    });
                }
            } catch (Throwable thr) {
                if (LogUtils.isShowLog()) {
                    LogUtils.w("Ad_SDK", "[vmId:" + virtualModuleId + "]loadMoPubAdInfo(" + adClassName + ", MoPub SDK does not exist " + (thr != null ? thr.getMessage() : "") + ")", thr);
                }
                sdkAdSourceRequestListener.onFinish((SdkAdSourceAdInfoBean) null);
            }
        }
    }
}
