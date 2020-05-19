package com.jiubang.commerce.statistics.adinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import com.facebook.ads.NativeAd;
import com.jb.ga0.commerce.util.AppUtils;
import com.jb.ga0.commerce.util.CustomAlarm;
import com.jb.ga0.commerce.util.CustomAlarmManager;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.encrypt.MD5;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.ad.alarm.AlarmConstant;
import com.jiubang.commerce.statistics.adinfo.bean.AdInfo;
import com.jiubang.commerce.statistics.adinfo.bean.AppInstallStatisInfo;
import com.jiubang.commerce.statistics.adinfo.http.AdInfoController;
import com.jiubang.commerce.statistics.adinfo.http.BaseHttpConnector;
import com.jiubang.commerce.utils.NetStateMonitor;
import com.jiubang.commerce.utils.ProcessUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdInfoStatisticManager {
    private static final long DELAY = 3000;
    public static final String GP_PKG_PREFIX = "details?id=";
    private static long INTERVAL = 43200000;
    private static final String KEY_AD_TITLE_LIST = "key_ad_title_list";
    private static final String KEY_LAST_START_ALARM_TIME = "key_last_start_alarm_time";
    private static final String KEY_LAST_UPDATE_AD_TITLE_LIST_SUCCESS = "key_last_update_ad_title_list_success";
    public static final String SHARE_AD_SHOW_STATISTIC = "_share_ad_show_statistic";
    private static final String SHARE_PREFERENCES_AD_INFO = "share_preferences_ad_info";
    private static final String TAG = "hzw";
    private static AdInfoStatisticManager sInstance;
    /* access modifiers changed from: private */
    public CopyOnWriteArrayList<String> mAdInfoList = new CopyOnWriteArrayList<>();
    private SharedPreferences mAdShowStatisticPrefs = this.mContext.getSharedPreferences(SHARE_AD_SHOW_STATISTIC, 0);
    private ConcurrentHashMap<String, Long> mAllShowingAdIdList = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public String mBannerUrl;
    private Runnable mClearRunnalbe;
    private Context mContext;
    /* access modifiers changed from: private */
    public String mDesc;
    /* access modifiers changed from: private */
    public boolean mHasClicked;
    private boolean mHasInited;
    /* access modifiers changed from: private */
    public String mId;
    private AppInstallMonitorTable mInstallMonitor = new AppInstallMonitorTable(this.mContext);
    /* access modifiers changed from: private */
    public Uri mLastUri;
    private ConcurrentHashMap<String, Long> mPhase2ShowingAdIdList = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public String mPkgName;
    /* access modifiers changed from: private */
    public SharedPreferences mPreferences = this.mContext.getSharedPreferences(SHARE_PREFERENCES_AD_INFO, 0);
    /* access modifiers changed from: private */
    public String mTitle;
    private int mType;

    public static AdInfoStatisticManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AdInfoStatisticManager.class) {
                if (sInstance == null) {
                    sInstance = new AdInfoStatisticManager(context);
                }
            }
        }
        return sInstance;
    }

    private AdInfoStatisticManager(Context context) {
        this.mContext = context.getApplicationContext();
        String jsonArrayString = this.mPreferences.getString(KEY_AD_TITLE_LIST, (String) null);
        if (jsonArrayString != null) {
            LogUtils.i(TAG, "cache:" + jsonArrayString);
            try {
                parseData(this.mAdInfoList, new JSONArray(jsonArrayString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.mClearRunnalbe = new Runnable() {
            public void run() {
                Uri unused = AdInfoStatisticManager.this.mLastUri = null;
                boolean unused2 = AdInfoStatisticManager.this.mHasClicked = false;
                String unused3 = AdInfoStatisticManager.this.mPkgName = null;
                String unused4 = AdInfoStatisticManager.this.mDesc = null;
                String unused5 = AdInfoStatisticManager.this.mTitle = null;
                String unused6 = AdInfoStatisticManager.this.mBannerUrl = null;
                String unused7 = AdInfoStatisticManager.this.mId = null;
            }
        };
        checkAdShowStatistic();
    }

    private void checkAdShowStatistic() {
        LogUtils.i(TAG, "checkAdShowStatistic");
        SharedPreferences.Editor editor = this.mAdShowStatisticPrefs.edit();
        Iterator i$ = new HashSet<>(this.mAdShowStatisticPrefs.getAll().keySet()).iterator();
        while (i$.hasNext()) {
            String adId = i$.next();
            AdInfoStatistic.uploadPhase2ImpressionNow(this.mContext, this.mAdShowStatisticPrefs.getString(adId, (String) null));
            editor.remove(adId);
        }
        editor.commit();
    }

    public void addAdShowStatistic(String adId, String info) {
        this.mAdShowStatisticPrefs.edit().putString(adId, info).commit();
    }

    public String getAdShowStatistic(String adId) {
        String info = this.mAdShowStatisticPrefs.getString(adId, (String) null);
        this.mAdShowStatisticPrefs.edit().remove(adId).commit();
        return info;
    }

    public void setLastUri(Context context, Uri lastUri, boolean statisticIfCan) {
        LogUtils.i(TAG, "setLastUri:");
        String pkgName = getPackagenameFromGp(lastUri.toString());
        if (TextUtils.isEmpty(pkgName)) {
            LogUtils.i(TAG, "跳转链接中不包含包名，不统计：" + lastUri.toString());
            return;
        }
        this.mLastUri = lastUri;
        this.mPkgName = pkgName;
        CustomThreadExecutorProxy.getInstance().runOnMainThread(this.mClearRunnalbe, DELAY);
        if (statisticIfCan) {
            statisticClickIfCan(context);
        }
    }

    public void setHasClicked(Context context, Object adObject, boolean statisticIfCan) {
        String str;
        LogUtils.i(TAG, "setHasClicked");
        if (adObject instanceof NativeAd) {
            NativeAd ad = (NativeAd) adObject;
            this.mId = getAdId(adObject);
            this.mType = 511;
            this.mTitle = ad.getAdTitle();
            this.mDesc = ad.getAdBody();
            if (ad.getAdCoverImage() != null) {
                str = ad.getAdCoverImage().getUrl();
            } else {
                str = "";
            }
            this.mBannerUrl = str;
        }
        this.mHasClicked = true;
        CustomThreadExecutorProxy.getInstance().runOnMainThread(this.mClearRunnalbe, DELAY);
        if (statisticIfCan) {
            statisticClickIfCan(context);
        }
    }

    public void statisticImpressionIfCan(Context context, Object adObject) {
        LogUtils.i(TAG, "statisticImpressionIfCan");
        if (adObject instanceof NativeAd) {
            NativeAd ad = (NativeAd) adObject;
            String title = ad.getAdTitle();
            String adId = getAdId(adObject);
            addShow(adId);
            if (containsInAdInfoList(AdInfoStatistic.replaceAdInfo(title))) {
                LogUtils.i(TAG, "需要统计第二阶段展示");
                AdInfoStatistic.uploadPhase2Impression(adId, context, title, ad.getAdCoverImage() != null ? ad.getAdCoverImage().getUrl() : "", ad.getAdBody(), 511);
                addPhase2Show(adId);
            }
        }
    }

    private boolean containsInAdInfoList(String title) {
        Iterator i$ = this.mAdInfoList.iterator();
        while (i$.hasNext()) {
            if (title.contains(i$.next())) {
                return true;
            }
        }
        return false;
    }

    private boolean isNeedPhase2Click(String adId) {
        Long time = this.mPhase2ShowingAdIdList.get(adId);
        if (time == null) {
            return false;
        }
        if (System.currentTimeMillis() - time.longValue() <= INTERVAL) {
            return true;
        }
        this.mPhase2ShowingAdIdList.remove(adId);
        return false;
    }

    public void statisticClickIfCan(Context context) {
        LogUtils.i(TAG, "statisticClickIfCan");
        if (this.mLastUri != null && this.mHasClicked) {
            CustomThreadExecutorProxy.getInstance().cancel(this.mClearRunnalbe);
            if (isNeedClick(this.mId)) {
                LogUtils.i(TAG, "需要统计第一阶段点击");
                AdInfoStatistic.uploadPhase1Click(this.mContext, this.mTitle, this.mBannerUrl, this.mDesc, this.mPkgName, this.mType);
                AppInstallStatisInfo appInstallStatisInfo = new AppInstallStatisInfo(this.mBannerUrl, this.mDesc, this.mId, this.mLastUri, this.mPkgName, this.mTitle, this.mType, System.currentTimeMillis());
                if (isNeedPhase2Click(this.mId)) {
                    LogUtils.i(TAG, "需要统计第二阶段点击");
                    AdInfoStatistic.uploadPhase2Click(this.mId, this.mContext, this.mTitle, this.mBannerUrl, this.mDesc, this.mPkgName, this.mType);
                    appInstallStatisInfo.mNeedPhase2Install = true;
                    removePhase2Show(this.mId);
                }
                this.mInstallMonitor.put(this.mPkgName, appInstallStatisInfo.toJsonString());
                this.mClearRunnalbe.run();
            }
        }
    }

    private void addPhase2Show(String adId) {
        this.mPhase2ShowingAdIdList.put(adId, Long.valueOf(System.currentTimeMillis()));
    }

    private void removePhase2Show(String adId) {
        this.mPhase2ShowingAdIdList.remove(adId);
    }

    private boolean isNeedClick(String adId) {
        Long time = this.mAllShowingAdIdList.get(adId);
        if (time == null) {
            return false;
        }
        if (System.currentTimeMillis() - time.longValue() <= INTERVAL) {
            return true;
        }
        this.mAllShowingAdIdList.remove(adId);
        return false;
    }

    private void addShow(String adId) {
        this.mAllShowingAdIdList.put(adId, Long.valueOf(System.currentTimeMillis()));
    }

    private void removeShow(String adId) {
        this.mAllShowingAdIdList.remove(adId);
    }

    public static String getAdId(Object adObject) {
        String key = null;
        if (adObject instanceof NativeAd) {
            NativeAd ad = (NativeAd) adObject;
            key = "511" + ad.getAdTitle() + ad.getAdBody() + adObject.hashCode();
        }
        if (key != null) {
            return MD5.to32BitString(key, false, (String) null);
        }
        return key;
    }

    public static final String getPackagenameFromGp(String url) {
        int start = url.indexOf(GP_PKG_PREFIX);
        if (start < 0) {
            return null;
        }
        int andIndex = url.indexOf("&", start);
        if (andIndex == -1) {
            andIndex = url.length();
        }
        return url.substring(GP_PKG_PREFIX.length() + start, andIndex);
    }

    public void init() {
        if (this.mHasInited) {
            LogUtils.w(TAG, "请勿重复初始化");
            return;
        }
        this.mHasInited = true;
        LogUtils.i(TAG, "初始化广告信息统计");
        long lastTime = this.mPreferences.getLong(KEY_LAST_START_ALARM_TIME, -1);
        if (lastTime == -1) {
            LogUtils.i(TAG, "第一次打开：获取title序列");
            lastTime = System.currentTimeMillis();
            this.mPreferences.edit().putLong(KEY_LAST_START_ALARM_TIME, lastTime).commit();
            updateAdTitleList();
        }
        long delay = System.currentTimeMillis() - lastTime;
        long period = INTERVAL;
        CustomAlarmManager.getInstance(this.mContext).getAlarm(AlarmConstant.MODULE_NAME).alarmRepeat(2, delay >= period ? 0 : period - delay, period, false, new AdTitleListAlarmListener());
        NetStateMonitor.getInstance(this.mContext).registerListener(new NetStateChangedListener());
        if (ProcessUtil.isMainProcess(this.mContext)) {
            checkInstallState(this.mContext);
        }
    }

    private void checkInstallState(Context context) {
        ArrayList<String[]> list = this.mInstallMonitor.getAll();
        if (list == null || list.size() <= 0) {
            LogUtils.i(TAG, "遍历应用安装监听列表:列表为空");
            return;
        }
        LogUtils.i(TAG, "遍历应用安装监听列表:" + list.size());
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            String[] set = i$.next();
            String pkgName = set[0];
            String jsonObjectString = set[1];
            LogUtils.i(TAG, "检测应用是否安装:" + pkgName);
            if (AppUtils.isAppExist(context, pkgName)) {
                try {
                    AppInstallStatisInfo info = new AppInstallStatisInfo(new JSONObject(jsonObjectString));
                    if (info.isValid()) {
                        LogUtils.i(TAG, "需要统计第一阶段安装");
                        AdInfoStatistic.uploadPhase1Install(context, info.mTitle, info.mBannerUrl, info.mDesc, info.mPkgName, info.mType);
                        if (info.mNeedPhase2Install) {
                            LogUtils.i(TAG, "需要统计第二阶段安装");
                            AdInfoStatistic.uploadPhase2Install(context, info.mTitle, info.mBannerUrl, info.mDesc, info.mPkgName, info.mType);
                        }
                    } else {
                        LogUtils.i(TAG, "应用超时");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtils.i(TAG, "onAppInstalled:statistic>error");
                }
                this.mInstallMonitor.remove(pkgName);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateAdTitleList() {
        AdInfoController.getInstance(this.mContext).getAdInfo(new BaseHttpConnector.ConnectListener() {
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    LogUtils.i(AdInfoStatisticManager.TAG, "请求title序列成功：" + jsonArray);
                    AdInfoStatisticManager.this.mAdInfoList.clear();
                    AdInfoStatisticManager.this.parseData(AdInfoStatisticManager.this.mAdInfoList, jsonArray);
                    AdInfoStatisticManager.this.mPreferences.edit().putString(AdInfoStatisticManager.KEY_AD_TITLE_LIST, response).commit();
                    AdInfoStatisticManager.this.mPreferences.edit().putBoolean(AdInfoStatisticManager.KEY_LAST_UPDATE_AD_TITLE_LIST_SUCCESS, true).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                    onFail(-111111);
                }
            }

            public void onFail(int reason) {
                AdInfoStatisticManager.this.mPreferences.edit().putBoolean(AdInfoStatisticManager.KEY_LAST_UPDATE_AD_TITLE_LIST_SUCCESS, false).commit();
                LogUtils.i(AdInfoStatisticManager.TAG, "fail:" + reason);
            }
        });
    }

    /* access modifiers changed from: private */
    public void parseData(List<String> list, JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            this.mAdInfoList.add(new AdInfo(jsonArray.optJSONObject(i)).mTitle);
        }
    }

    private class AdTitleListAlarmListener implements CustomAlarm.OnAlarmListener {
        private AdTitleListAlarmListener() {
        }

        public void onAlarm(int i) {
            LogUtils.i(AdInfoStatisticManager.TAG, "触发闹钟：获取最新title序列");
            AdInfoStatisticManager.this.mPreferences.edit().putLong(AdInfoStatisticManager.KEY_LAST_START_ALARM_TIME, System.currentTimeMillis()).commit();
            AdInfoStatisticManager.this.updateAdTitleList();
        }
    }

    private class NetStateChangedListener implements NetStateMonitor.INetStatusListener {
        private NetStateChangedListener() {
        }

        public void onNetStateChange(boolean b) {
            if (b) {
                LogUtils.i(AdInfoStatisticManager.TAG, "监听到网络打开");
                if (!AdInfoStatisticManager.this.mPreferences.getBoolean(AdInfoStatisticManager.KEY_LAST_UPDATE_AD_TITLE_LIST_SUCCESS, true)) {
                    LogUtils.i(AdInfoStatisticManager.TAG, "上次请求失败，现在重新请求title序列");
                    AdInfoStatisticManager.this.updateAdTitleList();
                }
            }
        }

        public void onWifiStateChange(boolean b) {
        }
    }
}
