package com.jiubang.commerce.ad.url;

import android.content.Context;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.http.bean.ParamsBean;
import com.jiubang.commerce.database.model.AdUrlInfoBean;
import com.jiubang.commerce.database.table.AdUrlTable;
import com.jiubang.commerce.thread.AdSdkThread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvanceParseRedirectUrl {
    private static final long AD_URL_DURATION = 259200000;
    private static final long AD_URL_DURATION_ONE = 86400000;
    private static AdvanceParseRedirectUrl sInstance;
    private AdUrlTable mAdUrlTable;
    /* access modifiers changed from: private */
    public Context mContext;
    private Map<String, AdUrlInfoBean> mFinalAdInfoMap;
    private ParseRedirectUrlRunnable mParseRedirectUrlRunnable;
    private AdSdkThread mParseRedirectUrlThread;
    /* access modifiers changed from: private */
    public String mParsingUrl;
    /* access modifiers changed from: private */
    public volatile Map<String, AdInfoBean> mRedirectAdInfoMap;
    /* access modifiers changed from: private */
    public volatile List<String> mRedirectUrlList;

    private AdvanceParseRedirectUrl(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
        this.mParseRedirectUrlRunnable = new ParseRedirectUrlRunnable();
        this.mAdUrlTable = AdUrlTable.getInstance(this.mContext);
        initData();
    }

    public static AdvanceParseRedirectUrl getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AdvanceParseRedirectUrl(context);
        }
        return sInstance;
    }

    private void initData() {
        this.mAdUrlTable.deleteInvalidAdUrlData(AD_URL_DURATION);
        List<AdUrlInfoBean> adUrlInfoList = this.mAdUrlTable.getAdUrlData((AdUrlInfoBean) null);
        if (adUrlInfoList != null && !adUrlInfoList.isEmpty()) {
            if (this.mFinalAdInfoMap == null) {
                this.mFinalAdInfoMap = new HashMap();
            }
            for (AdUrlInfoBean adUrlInfo : adUrlInfoList) {
                this.mFinalAdInfoMap.put(adUrlInfo.getRedirectUrl(), adUrlInfo);
            }
        }
    }

    public void addRedirectUrl(AdInfoBean adInfoBean) {
        if (adInfoBean != null && !TextUtils.isEmpty(adInfoBean.getAdUrl())) {
            if (this.mRedirectUrlList == null) {
                this.mRedirectUrlList = new ArrayList();
            }
            String adUrl = adInfoBean.getAdUrl();
            if (this.mRedirectUrlList.size() > 0 && this.mRedirectUrlList.contains(adUrl)) {
                this.mRedirectUrlList.remove(adUrl);
            }
            if (this.mRedirectAdInfoMap != null) {
                this.mRedirectAdInfoMap.put(adUrl, adInfoBean);
            }
            this.mRedirectUrlList.add(0, adUrl);
            if (this.mParseRedirectUrlThread == null || !this.mParseRedirectUrlThread.isAlive()) {
                this.mParseRedirectUrlThread = new AdSdkThread(this.mParseRedirectUrlRunnable);
                this.mParseRedirectUrlThread.start();
            }
        }
    }

    public void removeRedirectUrl(String redirectUrl) {
        if (!TextUtils.isEmpty(redirectUrl) && this.mRedirectUrlList != null && this.mRedirectUrlList.contains(redirectUrl)) {
            this.mRedirectUrlList.remove(redirectUrl);
        }
    }

    public String getFinalUrl(String redirectUrl, long... customDuration) {
        AdUrlInfoBean adUrlInfoBean = (this.mFinalAdInfoMap == null || TextUtils.isEmpty(redirectUrl)) ? null : this.mFinalAdInfoMap.get(redirectUrl);
        if (adUrlInfoBean == null || ((TextUtils.isEmpty(adUrlInfoBean.getAdUrl()) || adUrlInfoBean.getAdUrl().startsWith(redirectUrl)) && adUrlInfoBean.getUpdateTime() <= System.currentTimeMillis() - 86400000)) {
            return "";
        }
        long customD = (customDuration == null || customDuration.length <= 0 || customDuration[0] <= 0) ? -1 : customDuration[0];
        if (adUrlInfoBean.getUpdateTime() > System.currentTimeMillis() - (customD > 0 ? customD : AD_URL_DURATION)) {
            return adUrlInfoBean.getAdUrl();
        }
        return "";
    }

    public boolean isPreResolveSuccess(String redirectUrl) {
        if (!TextUtils.isEmpty(getFinalUrl(redirectUrl, new long[0]))) {
            return true;
        }
        return false;
    }

    public synchronized void saveFinalUrl(String pkgName, String redirectUrl, String finalUrl) {
        if (!TextUtils.isEmpty(redirectUrl)) {
            if (TextUtils.isEmpty(finalUrl)) {
                finalUrl = redirectUrl;
            }
            removeRedirectUrl(redirectUrl);
            if (this.mFinalAdInfoMap == null) {
                this.mFinalAdInfoMap = new HashMap();
            }
            AdUrlInfoBean adUrlInfoBean = new AdUrlInfoBean();
            adUrlInfoBean.setRedirectUrl(redirectUrl);
            adUrlInfoBean.setAdUrl(finalUrl);
            adUrlInfoBean.setUpdateTime(System.currentTimeMillis());
            this.mFinalAdInfoMap.put(redirectUrl, adUrlInfoBean);
            this.mAdUrlTable.insertAdUrlData(AdUrlTable.getAdUrlInfoList(pkgName, redirectUrl, finalUrl, adUrlInfoBean.getUpdateTime()));
        }
    }

    public boolean isParsing(String redirectUrl) {
        if (TextUtils.isEmpty(redirectUrl) || !redirectUrl.equals(this.mParsingUrl)) {
            return false;
        }
        return true;
    }

    class ParseRedirectUrlRunnable implements Runnable {
        ParseRedirectUrlRunnable() {
        }

        public void run() {
            while (AdvanceParseRedirectUrl.this.mRedirectUrlList != null && AdvanceParseRedirectUrl.this.mRedirectUrlList.size() > 0) {
                String unused = AdvanceParseRedirectUrl.this.mParsingUrl = (String) AdvanceParseRedirectUrl.this.mRedirectUrlList.remove(0);
                if (TextUtils.isEmpty(AdvanceParseRedirectUrl.this.getFinalUrl(AdvanceParseRedirectUrl.this.mParsingUrl, new long[0]))) {
                    AdInfoBean adInfoBean = AdvanceParseRedirectUrl.this.mRedirectAdInfoMap != null ? (AdInfoBean) AdvanceParseRedirectUrl.this.mRedirectAdInfoMap.get(AdvanceParseRedirectUrl.this.mParsingUrl) : null;
                    ParamsBean paramsBean = null;
                    if (adInfoBean != null) {
                        paramsBean = new ParamsBean();
                        paramsBean.setUASwitcher(adInfoBean.getUASwitcher());
                    }
                    String finalUrl = AdRedirectUrlUtils.getHttpRedirectUrlFromLocation(AdvanceParseRedirectUrl.this.mContext, paramsBean, adInfoBean != null ? String.valueOf(adInfoBean.getModuleId()) : "-1", adInfoBean != null ? String.valueOf(adInfoBean.getMapId()) : "-1", adInfoBean != null ? String.valueOf(adInfoBean.getAdId()) : "-1", AdRedirectUrlUtils.handlePreloadAdUrl(AdvanceParseRedirectUrl.this.mParsingUrl));
                    LogUtils.e("Ad_SDK", "getHttpRedirectUrl(" + AdvanceParseRedirectUrl.this.mRedirectUrlList.size() + ", " + AdRedirectUrlUtils.handlePreloadAdUrl(AdvanceParseRedirectUrl.this.mParsingUrl) + "------------------->>" + finalUrl + ")");
                    AdvanceParseRedirectUrl.this.saveFinalUrl(adInfoBean.getPackageName(), AdvanceParseRedirectUrl.this.mParsingUrl, finalUrl);
                }
                String unused2 = AdvanceParseRedirectUrl.this.mParsingUrl = "";
            }
        }
    }
}
