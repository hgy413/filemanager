package com.jiubang.commerce.ad.gomo;

import android.content.Context;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.utils.FileCacheUtils;
import com.jiubang.commerce.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class GomoAdModuleInfo {
    private int mAdPos;
    private List<GomoAd> mAdvs;
    private String mErrorMessage;
    private String mHasShowAdUrlList;
    private long mSaveDataTime;
    private int mSuccess;

    public int getmAdPos() {
        return this.mAdPos;
    }

    public void setmAdPos(int mAdPos2) {
        this.mAdPos = mAdPos2;
    }

    public boolean isSuccess() {
        return 1 == this.mSuccess;
    }

    public String getmErrorMessage() {
        return this.mErrorMessage;
    }

    public void setmErrorMessage(String mErrorMessage2) {
        this.mErrorMessage = mErrorMessage2;
    }

    public List<GomoAd> getmAdvs() {
        return this.mAdvs;
    }

    public void setmAdvs(List<GomoAd> mAdvs2) {
        this.mAdvs = mAdvs2;
    }

    public long getSaveDataTime() {
        return this.mSaveDataTime;
    }

    public void setHasShowAdUrlList(String hasShowAdUrlList) {
        this.mHasShowAdUrlList = hasShowAdUrlList;
    }

    public String getHasShowAdUrlList() {
        return this.mHasShowAdUrlList;
    }

    public static GomoAdModuleInfo parseJSONObject(Context context, int adPos, int advDataSource, JSONObject json, int vmid, int moduleId) {
        if (json == null || json.length() < 1) {
            return null;
        }
        GomoAdModuleInfo bean = new GomoAdModuleInfo();
        if (json.has(AdSdkContants.SAVE_DATA_TIME)) {
            bean.mSaveDataTime = json.optLong(AdSdkContants.SAVE_DATA_TIME);
        }
        bean.mAdPos = adPos;
        bean.mSuccess = json.optInt("success", 0);
        bean.mErrorMessage = json.optString("message");
        bean.mAdvs = GomoAd.parseJsonArray(context, json.optJSONArray("advs"), adPos, advDataSource, vmid, moduleId);
        if (!json.has(AdSdkContants.HAS_SHOW_AD_URL_LIST)) {
            return bean;
        }
        bean.mHasShowAdUrlList = json.optString(AdSdkContants.HAS_SHOW_AD_URL_LIST, "");
        return bean;
    }

    public static AdModuleInfoBean getAdModuleInfoBean(Context context, BaseModuleDataItemBean moduleDataItemBean, int vmid, int adPos, int adCount, boolean needShownFilter, List<String> installFilterException, JSONObject onlineAdJsonObj) {
        GomoAdModuleInfo onlineModuleInfoBean = parseJSONObject(context, adPos, moduleDataItemBean != null ? moduleDataItemBean.getAdvDataSource() : 0, onlineAdJsonObj, vmid, moduleDataItemBean != null ? moduleDataItemBean.getModuleId() : 0);
        List<GomoAd> onlineAdInfoList = onlineModuleInfoBean != null ? onlineModuleInfoBean.getmAdvs() : null;
        List<GomoAd> resultOnlineAds = new ArrayList<>();
        if (onlineAdInfoList == null || onlineAdInfoList.isEmpty()) {
            return null;
        }
        String hasShowAdUrls = onlineModuleInfoBean.getHasShowAdUrlList();
        if (!needShownFilter || TextUtils.isEmpty(hasShowAdUrls)) {
            resultOnlineAds.addAll(onlineAdInfoList);
        } else {
            for (GomoAd onlineAdInfoBean : onlineAdInfoList) {
                if (hasShowAdUrls.indexOf(AdSdkContants.SYMBOL_DOUBLE_LINE + onlineAdInfoBean.getTargetUrl() + AdSdkContants.SYMBOL_DOUBLE_LINE) < 0) {
                    resultOnlineAds.add(onlineAdInfoBean);
                }
            }
        }
        AdModuleInfoBean adInfoBean = new AdModuleInfoBean();
        adInfoBean.setGomoAdInfoList(context, moduleDataItemBean, onlineModuleInfoBean, resultOnlineAds, installFilterException);
        if (!LogUtils.isShowLog()) {
            return adInfoBean;
        }
        for (GomoAd onlineAdInfoBean2 : onlineAdInfoList) {
            if (onlineAdInfoBean2 != null) {
                LogUtils.d("Ad_SDK", "[GomoAdPos:" + adPos + "]info::>(count:" + onlineAdInfoList.size() + "--" + ", MapId:" + onlineAdInfoBean2.getMapId() + ", packageName:" + onlineAdInfoBean2.getPackageName() + ", Name:" + onlineAdInfoBean2.getAppName() + ", AdPos:" + onlineAdInfoBean2.getAdPos() + ")");
            }
        }
        return adInfoBean;
    }

    public static String getCacheFileName(int adPos) {
        return AdSdkContants.GOMO_AD_CACHE_FILE_NAME_PREFIX + adPos;
    }

    public static boolean checkOnlineAdInfoValid(long loadDataTime) {
        if (loadDataTime <= 0 || loadDataTime > System.currentTimeMillis() - AdSdkContants.GOMO_AD_VALID_CACHE_DURATION) {
            return true;
        }
        return false;
    }

    public static boolean saveAdDataToSdcard(int adPos, JSONObject cacheDataJsonObject) {
        if (cacheDataJsonObject == null || cacheDataJsonObject.length() < 1) {
            return false;
        }
        if (!cacheDataJsonObject.has(AdSdkContants.SAVE_DATA_TIME)) {
            try {
                cacheDataJsonObject.put(AdSdkContants.SAVE_DATA_TIME, System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            return FileCacheUtils.saveCacheDataToSdcard(getCacheFileName(adPos), StringUtils.toString(cacheDataJsonObject), true);
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
