package com.jiubang.commerce.ad.ironscr;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.utils.AppUtils;
import com.jiubang.commerce.utils.StringUtils;
import com.jiubang.commerce.utils.SystemUtils;

public class IronScrAd extends WebView {
    static final String COMPLETE = "passback";
    static final String DIV = "','";
    static final String FAIL = "error";
    static final String GP_PREFIX = "https://play.google.com/store/apps/details?id=";
    static final String HTMLSTR = "<head><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'><style>body {margin: 0;padding: 0;}</style></head><body><script>var width;var height;var bundleId;var appStoreUrl;var appName;var appVersion;var uid;function loadParams(w, h, bId, asUrl, aName, aVersion, aUid){window.AndroidWebView.showInfoFromJs('loadParams');width = w;height = h;bundleId = bId;appStoreUrl = asUrl;appName = aName;appVersion = aVersion;uid = aUid;}function loadVideo(){var result = 'width=' + width + ' height=' + height + ' bundleId=' + bundleId +' appStoreUrl=' + appStoreUrl + ' appName=' + appName + ' appVersion=' + appVersion + ' uid=' + uid;window.AndroidWebView.showInfoFromJs('loadVideo--param=' + result);var ifr = document.createElement('iframe');ifr.width = width;ifr.height = height;ifr.scrolling = 'no';ifr.marginHeight = 0;ifr.marginWidth = 0;ifr.frameBorder = 0;ifr.src = 'http://www.isvd-jhn.com/integ/sungy.html?w=' + width + '&h='+ height + '&bundleId=' + bundleId + '&appStoreUrl=' +encodeURIComponent(appStoreUrl) + '&appName=' +encodeURIComponent(appName) + '&appVersion=' + appVersion + '&uid=' +uid + '&cb=' + new Date().getTime();document.body.appendChild(ifr);}function displayMessage(evt){window.AndroidWebView.handleDisplayMessage(evt.data);}window.addEventListener('message', displayMessage, false);</script></body>";
    public static final String IRON_ID = "IronScrAd";
    static final String SUCCESS = "impression";
    static short sSystemSupportWebView = 0;
    /* access modifiers changed from: private */
    public IronScrAdConfig mAdConfig;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public boolean mFileLoadFinish = false;
    /* access modifiers changed from: private */
    public boolean mHasComplete = false;
    /* access modifiers changed from: private */
    public IIronScrListener mIIronScrListener;
    /* access modifiers changed from: private */
    public boolean mIsLoading = false;
    /* access modifiers changed from: private */
    public boolean mLoaded = false;
    /* access modifiers changed from: private */
    public Runnable mTask = new Runnable() {
        public void run() {
            String appName;
            String pkgName = IronScrAd.this.mContext.getPackageName();
            String width = "300";
            String height = "250";
            if (IronScrAd.this.mAdConfig != null) {
                width = IronScrAd.this.mAdConfig.getWidthStr();
                height = IronScrAd.this.mAdConfig.getHeightStr();
            }
            String uid = AdSdkManager.getInstance().getGoogleId();
            if (StringUtils.isEmpty(uid) || uid.toUpperCase().contains(AdSdkApi.UNABLE_TO_RETRIEVE)) {
                IronScrAd.this.mIIronScrListener.onFail("uid is invalid!");
                return;
            }
            String bundleId = pkgName;
            String appStoreUrl = IronScrAd.GP_PREFIX + pkgName + "&hl=" + StringUtils.toLowerCase(SystemUtils.getLanguage(IronScrAd.this.mContext));
            String engAppName = IronScrUtil.getAppNameInEnglish(IronScrAd.this.mContext);
            if (StringUtils.isEmpty(engAppName)) {
                appName = AppUtils.getAppLabel(IronScrAd.this.mContext, pkgName);
            } else {
                appName = engAppName;
            }
            IronScrAd.this.loadParams(width, height, bundleId, appStoreUrl, appName, AppUtils.getAppVersionName(IronScrAd.this.mContext, pkgName), uid);
            IronScrAd.this.loadVideo();
        }
    };

    public interface IIronScrListener {
        void onComplete();

        void onFail(String str);

        void onSuccess(IronScrAd ironScrAd);
    }

    public interface IWebViewCheckListener {
        void onChecked(boolean z);
    }

    public static void systemSupportWebView(final Context context, final IWebViewCheckListener listener) {
        boolean z = true;
        if (listener != null) {
            if (sSystemSupportWebView == 0) {
                CustomThreadExecutorProxy.getInstance().runOnMainThread(new Runnable() {
                    /* JADX WARNING: Removed duplicated region for block: B:17:0x0032  */
                    /* JADX WARNING: Removed duplicated region for block: B:21:0x0045  */
                    /* JADX WARNING: Removed duplicated region for block: B:24:0x004a  */
                    /* JADX WARNING: Removed duplicated region for block: B:29:0x005d  */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        /*
                            r9 = this;
                            r5 = 0
                            r4 = 1
                            r2 = 0
                            android.webkit.WebView r3 = new android.webkit.WebView     // Catch:{ Throwable -> 0x0025 }
                            android.content.Context r6 = r2     // Catch:{ Throwable -> 0x0025 }
                            r3.<init>(r6)     // Catch:{ Throwable -> 0x0025 }
                            r6 = 1
                            com.jiubang.commerce.ad.ironscr.IronScrAd.sSystemSupportWebView = r6     // Catch:{ Throwable -> 0x0062, all -> 0x005f }
                            if (r3 == 0) goto L_0x0018
                            r0 = r3
                            com.jiubang.commerce.ad.ironscr.IronScrAd$2$1 r6 = new com.jiubang.commerce.ad.ironscr.IronScrAd$2$1
                            r6.<init>(r0)
                            r3.post(r6)
                        L_0x0018:
                            com.jiubang.commerce.ad.ironscr.IronScrAd$IWebViewCheckListener r6 = r3
                            short r7 = com.jiubang.commerce.ad.ironscr.IronScrAd.sSystemSupportWebView
                            if (r4 != r7) goto L_0x0023
                        L_0x001e:
                            r6.onChecked(r4)
                            r2 = r3
                        L_0x0022:
                            return
                        L_0x0023:
                            r4 = r5
                            goto L_0x001e
                        L_0x0025:
                            r1 = move-exception
                        L_0x0026:
                            r6 = 2
                            com.jiubang.commerce.ad.ironscr.IronScrAd.sSystemSupportWebView = r6     // Catch:{ all -> 0x0047 }
                            java.lang.String r6 = "wbq"
                            java.lang.String r7 = "systemSupportWebView"
                            android.util.Log.w(r6, r7, r1)     // Catch:{ all -> 0x0047 }
                            if (r2 == 0) goto L_0x003b
                            r0 = r2
                            com.jiubang.commerce.ad.ironscr.IronScrAd$2$1 r6 = new com.jiubang.commerce.ad.ironscr.IronScrAd$2$1
                            r6.<init>(r0)
                            r2.post(r6)
                        L_0x003b:
                            com.jiubang.commerce.ad.ironscr.IronScrAd$IWebViewCheckListener r6 = r3
                            short r7 = com.jiubang.commerce.ad.ironscr.IronScrAd.sSystemSupportWebView
                            if (r4 != r7) goto L_0x0045
                        L_0x0041:
                            r6.onChecked(r4)
                            goto L_0x0022
                        L_0x0045:
                            r4 = r5
                            goto L_0x0041
                        L_0x0047:
                            r6 = move-exception
                        L_0x0048:
                            if (r2 == 0) goto L_0x0053
                            r0 = r2
                            com.jiubang.commerce.ad.ironscr.IronScrAd$2$1 r7 = new com.jiubang.commerce.ad.ironscr.IronScrAd$2$1
                            r7.<init>(r0)
                            r2.post(r7)
                        L_0x0053:
                            com.jiubang.commerce.ad.ironscr.IronScrAd$IWebViewCheckListener r7 = r3
                            short r8 = com.jiubang.commerce.ad.ironscr.IronScrAd.sSystemSupportWebView
                            if (r4 != r8) goto L_0x005d
                        L_0x0059:
                            r7.onChecked(r4)
                            throw r6
                        L_0x005d:
                            r4 = r5
                            goto L_0x0059
                        L_0x005f:
                            r6 = move-exception
                            r2 = r3
                            goto L_0x0048
                        L_0x0062:
                            r1 = move-exception
                            r2 = r3
                            goto L_0x0026
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.ad.ironscr.IronScrAd.AnonymousClass2.run():void");
                    }
                });
                return;
            }
            if (1 != sSystemSupportWebView) {
                z = false;
            }
            listener.onChecked(z);
        }
    }

    public IronScrAd(Context context, IronScrAdConfig config) {
        super(context);
        constructWebView();
        this.mContext = context.getApplicationContext();
        this.mAdConfig = config;
    }

    public void setAdListener(IIronScrListener listener) {
        this.mIIronScrListener = listener;
    }

    public void loadAd() {
        if (this.mIIronScrListener != null && !this.mLoaded && !this.mIsLoading) {
            this.mIsLoading = true;
            if (checkAssetsFileExist()) {
                LogUtils.d("Ad_SDK", "IronScr-loadHtmlStr");
                this.mFileLoadFinish = false;
                loadData(HTMLSTR, "text/html", "utf-8");
                return;
            }
            LogUtils.w("Ad_SDK", "checkAssetsFileExist fail");
            this.mIsLoading = false;
            this.mIIronScrListener.onFail("checkAssetsFileExist fail");
        }
    }

    private void constructWebView() {
        getSettings().setJavaScriptEnabled(true);
        setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (LogUtils.isShowLog()) {
                    LogUtils.d("Ad_SDK", "IronScrAd:onReceivedError=" + description);
                }
            }
        });
        setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                LogUtils.d("Ad_SDK", "IronScrAd newProgress=" + newProgress);
                if (!IronScrAd.this.mFileLoadFinish && newProgress >= 100) {
                    LogUtils.d("Ad_SDK", "IronScrAd file loaded");
                    boolean unused = IronScrAd.this.mFileLoadFinish = true;
                    IronScrAd.this.mTask.run();
                }
            }
        });
        addJavascriptInterface(new JsInterface(), "AndroidWebView");
    }

    /* access modifiers changed from: private */
    public void loadParams(String w, String h, String bId, String asUrl, String aName, String aVersion, String aUid) {
        LogUtils.d("Ad_SDK", "IronScr-loadParams");
        StringBuffer sb = new StringBuffer("javascript:loadParams('");
        sb.append(w);
        sb.append(DIV);
        sb.append(h);
        sb.append(DIV);
        sb.append(bId);
        sb.append(DIV);
        sb.append(asUrl);
        sb.append(DIV);
        sb.append(aName);
        sb.append(DIV);
        sb.append(aVersion);
        sb.append(DIV);
        sb.append(aUid);
        sb.append("')");
        loadUrl(sb.toString());
    }

    /* access modifiers changed from: private */
    public void loadVideo() {
        LogUtils.d("Ad_SDK", "IronScr-loadVideo");
        loadUrl("javascript:loadVideo()");
    }

    private boolean checkAssetsFileExist() {
        return true;
    }

    private class JsInterface {
        private JsInterface() {
        }

        @JavascriptInterface
        public void showInfoFromJs(String w) {
            LogUtils.d("Ad_SDK", "JsInterface:showInfoFromJs=" + w);
        }

        @JavascriptInterface
        public void handleDisplayMessage(String msg) {
            LogUtils.d("Ad_SDK", "JsInterface:handleDisplayMessage=" + msg);
            if (IronScrAd.this.mIIronScrListener != null) {
                boolean old = IronScrAd.this.mLoaded;
                if (IronScrAd.SUCCESS.equals(msg)) {
                    boolean unused = IronScrAd.this.mIsLoading = false;
                    boolean unused2 = IronScrAd.this.mLoaded = true;
                    if (!old) {
                        IronScrAd.this.mIIronScrListener.onSuccess(IronScrAd.this);
                    }
                } else if (IronScrAd.FAIL.equals(msg)) {
                    boolean unused3 = IronScrAd.this.mIsLoading = false;
                    boolean unused4 = IronScrAd.this.mLoaded = true;
                    IronScrAd.this.post(new Runnable() {
                        public void run() {
                            IronScrAd.this.destroy();
                        }
                    });
                    if (!old) {
                        IronScrAd.this.mIIronScrListener.onFail("IronScr fail");
                    }
                } else if (IronScrAd.COMPLETE.equals(msg) && !IronScrAd.this.mHasComplete) {
                    boolean unused5 = IronScrAd.this.mHasComplete = true;
                    IronScrAd.this.mIIronScrListener.onComplete();
                }
            }
        }
    }

    public static class IronScrAdConfig {
        static final long DEFAULT_TIMEOUT = 35000;
        private int mHeight;
        private long mTimeOut = DEFAULT_TIMEOUT;
        private int mWidth;

        public IronScrAdConfig(int w, int h) {
            if (w <= 0 || h <= 0) {
                throw new IllegalArgumentException("IronScrAdConfig--invalid width or height");
            }
            this.mWidth = w;
            this.mHeight = h;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public String getWidthStr() {
            return "" + this.mWidth;
        }

        public String getHeightStr() {
            return "" + this.mHeight;
        }

        public void setTimeOut(long time) {
            if (time <= 0) {
                throw new IllegalArgumentException("IronScrAdConfig--invalid timeout time");
            }
            this.mTimeOut = time;
        }

        public static long getTimeOut(IronScrAdConfig config) {
            if (config != null) {
                return config.mTimeOut;
            }
            return DEFAULT_TIMEOUT;
        }
    }
}
