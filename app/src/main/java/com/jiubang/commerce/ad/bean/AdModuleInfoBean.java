package com.jiubang.commerce.ad.bean;

import android.content.Context;
import com.jiubang.commerce.ad.gomo.GomoAd;
import com.jiubang.commerce.ad.gomo.GomoAdModuleInfo;
import com.jiubang.commerce.ad.http.bean.BaseAppInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseIntellModuleBean;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.http.bean.BaseOnlineAdInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseOnlineModuleInfoBean;
import com.jiubang.commerce.ad.install.InstalledFilter;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdInfoBean;
import java.util.List;

public class AdModuleInfoBean {
    public static final int AD_OBJECT_TYPE_ADMOB_BANNER = 4;
    public static final int AD_OBJECT_TYPE_ADMOB_INTERSTITIAL = 5;
    public static final int AD_OBJECT_TYPE_ADMOB_NATIVE = 6;
    public static final int AD_OBJECT_TYPE_APPLOVIN = 11;
    public static final int AD_OBJECT_TYPE_CHEETAH = 12;
    public static final int AD_OBJECT_TYPE_CHEETAH_VIDEO = 15;
    public static final int AD_OBJECT_TYPE_FACEBOOK_BANNER = 1;
    public static final int AD_OBJECT_TYPE_FACEBOOK_INTERSTITIAL = 2;
    public static final int AD_OBJECT_TYPE_FACEBOOK_NATIVE = 3;
    public static final int AD_OBJECT_TYPE_GOMO = 13;
    public static final int AD_OBJECT_TYPE_IRONSCR = 14;
    public static final int AD_OBJECT_TYPE_LOOPME_BANNER = 7;
    public static final int AD_OBJECT_TYPE_LOOPME_INTERSTITIAL = 8;
    public static final int AD_OBJECT_TYPE_MOBILECORE = 9;
    public static final int AD_OBJECT_TYPE_MOPUB = 16;
    public static final int AD_OBJECT_TYPE_OFFLINE_ONLINEAPI = 0;
    public static final int AD_OBJECT_TYPE_VUNGLE = 10;
    public static final int AD_TYPE_INTELL = 3;
    public static final int AD_TYPE_OFFLINE = 0;
    public static final int AD_TYPE_ONLINE = 1;
    public static final int AD_TYPE_SDK = 2;
    private List<AdInfoBean> mAdInfoList;
    private int mAdType;
    private List<GomoAd> mGomoAdInfoList;
    private GomoAdModuleInfo mGomoModuleInfoBean;
    private BaseIntellModuleBean mIntellModuleBean;
    private boolean mIsRequestData = false;
    private BaseModuleDataItemBean mModuleDataItemBean;
    private List<BaseAppInfoBean> mOfflineAdInfoList;
    private List<BaseOnlineAdInfoBean> mOnlineAdInfoList;
    private BaseOnlineModuleInfoBean mOnlineModuleInfoBean;
    private SdkAdSourceAdInfoBean mSdkAdSourceAdInfoBean;

    public int getVirtualModuleId() {
        if (this.mModuleDataItemBean != null) {
            return this.mModuleDataItemBean.getVirtualModuleId();
        }
        return -1;
    }

    public int getAdType() {
        return this.mAdType;
    }

    public List<AdInfoBean> getAdInfoList() {
        return this.mAdInfoList;
    }

    public void setAdInfoList(List<AdInfoBean> adInfoList) {
        this.mAdInfoList = adInfoList;
    }

    public SdkAdSourceAdInfoBean getSdkAdSourceAdInfoBean() {
        return this.mSdkAdSourceAdInfoBean;
    }

    public void setSdkAdSourceAdInfoBean(SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean) {
        this.mSdkAdSourceAdInfoBean = sdkAdSourceAdInfoBean;
    }

    public List<BaseAppInfoBean> getOfflineAdInfoList() {
        return this.mOfflineAdInfoList;
    }

    public void setOfflineAdInfoList(Context context, BaseModuleDataItemBean moduleDataItemBean, List<BaseAppInfoBean> offlineAdInfoList) {
        this.mAdType = 0;
        this.mModuleDataItemBean = moduleDataItemBean;
        this.mOfflineAdInfoList = offlineAdInfoList;
        this.mAdInfoList = InstalledFilter.filter(context, AdInfoBean.conversionFormAppInfoBean(this.mOfflineAdInfoList));
        setExtraInfos(moduleDataItemBean);
    }

    public List<BaseOnlineAdInfoBean> getOnlineAdInfoList() {
        return this.mOnlineAdInfoList;
    }

    public List<GomoAd> getGomoAdInfoList() {
        return this.mGomoAdInfoList;
    }

    public void setOnlineAdInfoList(Context context, BaseModuleDataItemBean moduleDataItemBean, BaseOnlineModuleInfoBean onlineModuleInfoBean, List<BaseOnlineAdInfoBean> onlineAdInfoList, List<String> installFilterException) {
        this.mAdType = 1;
        this.mModuleDataItemBean = moduleDataItemBean;
        this.mOnlineModuleInfoBean = onlineModuleInfoBean;
        this.mOnlineAdInfoList = onlineAdInfoList;
        this.mAdInfoList = InstalledFilter.filter(context, AdInfoBean.conversionFormOnlineAdInfoBean(this.mOnlineAdInfoList), installFilterException);
        setExtraInfos(moduleDataItemBean);
    }

    public void setGomoAdInfoList(Context context, BaseModuleDataItemBean moduleDataItemBean, GomoAdModuleInfo gomoModuleInfoBean, List<GomoAd> onlineAdInfoList, List<String> installFilterException) {
        this.mAdType = 0;
        this.mModuleDataItemBean = moduleDataItemBean;
        this.mGomoModuleInfoBean = gomoModuleInfoBean;
        this.mGomoAdInfoList = onlineAdInfoList;
        this.mAdInfoList = InstalledFilter.simpleFilter(context, AdInfoBean.conversionFormGomoAdInfoBean(this.mGomoAdInfoList), installFilterException);
        setExtraInfos(moduleDataItemBean);
    }

    public void setIntellAdInfoList(Context context, BaseIntellModuleBean moduleBean) {
        this.mAdType = 3;
        this.mIntellModuleBean = moduleBean;
        this.mAdInfoList = InstalledFilter.filter(context, AdInfoBean.conversionFormIntellAdInfoBean(moduleBean.getmAdvs()));
    }

    private void setExtraInfos(BaseModuleDataItemBean moduleDataItemBean) {
        if (this.mAdInfoList != null && moduleDataItemBean != null) {
            for (int i = 0; i < this.mAdInfoList.size(); i++) {
                this.mAdInfoList.get(i).setOnlineAdvType(moduleDataItemBean.getOnlineAdvType());
            }
        }
    }

    public BaseModuleDataItemBean getSdkAdControlInfo() {
        return this.mModuleDataItemBean;
    }

    public void setSdkAdControlInfo(BaseModuleDataItemBean moduleDataItemBean) {
        this.mAdType = 2;
        this.mModuleDataItemBean = moduleDataItemBean;
    }

    public void setFakeFbNativeControlInfo(BaseModuleDataItemBean moduleDataItemBean, List<AdInfoBean> adInfoList) {
        this.mAdType = 0;
        this.mAdInfoList = adInfoList;
        moduleDataItemBean.setAdvDataSourceType(Integer.parseInt(BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE));
        this.mModuleDataItemBean = moduleDataItemBean;
    }

    public BaseModuleDataItemBean getModuleDataItemBean() {
        return this.mModuleDataItemBean;
    }

    public BaseOnlineModuleInfoBean getOnlineModuleInfoBean() {
        return this.mOnlineModuleInfoBean;
    }

    public GomoAdModuleInfo getGomoModuleInfoBean() {
        return this.mGomoModuleInfoBean;
    }

    public BaseIntellModuleBean getIntellModuleBean() {
        return this.mIntellModuleBean;
    }

    public boolean isRequestData() {
        return this.mIsRequestData;
    }

    public void setIsRequestData(boolean isRequestData) {
        this.mIsRequestData = isRequestData;
    }

    public static boolean isFaceBookAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || (moduleDataItemBean.getAdvDataSource() != 2 && moduleDataItemBean.getAdvDataSource() != 11)) {
            return false;
        }
        return true;
    }

    public static boolean isAdMobAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || moduleDataItemBean.getAdvDataSource() != 8) {
            return false;
        }
        return true;
    }

    public static boolean isMobileCoreAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || (moduleDataItemBean.getAdvDataSource() != 9 && moduleDataItemBean.getAdvDataSource() != 10)) {
            return false;
        }
        return true;
    }

    public static boolean isLoopMeAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || moduleDataItemBean.getAdvDataSource() != 16) {
            return false;
        }
        return true;
    }

    public static boolean isVungleAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || moduleDataItemBean.getAdvDataSource() != 34) {
            return false;
        }
        return true;
    }

    public static boolean isApplovinAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || moduleDataItemBean.getAdvDataSource() != 20) {
            return false;
        }
        return true;
    }

    public static boolean isCheetahAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || moduleDataItemBean.getAdvDataSource() != 21) {
            return false;
        }
        return true;
    }

    public static boolean isGomoAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || moduleDataItemBean.getAdvDataSource() != 35) {
            return false;
        }
        return true;
    }

    public static boolean isS2SAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || moduleDataItemBean.getAdvDataSource() != 36) {
            return false;
        }
        return true;
    }

    public static boolean isIronScrAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || moduleDataItemBean.getAdvDataSource() != 37) {
            return false;
        }
        return true;
    }

    public static boolean isMoPubAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || moduleDataItemBean.getAdvDataSource() != 39) {
            return false;
        }
        return true;
    }

    public static boolean isCheetahVideoAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || moduleDataItemBean.getAdvDataSource() != 38) {
            return false;
        }
        return true;
    }

    public static boolean isContainFaceBookAdSource(List<Integer> advDataSourceList) {
        if (advDataSourceList == null || (!advDataSourceList.contains(2) && !advDataSourceList.contains(11))) {
            return false;
        }
        return true;
    }

    public static boolean isContainAdMobAdSource(List<Integer> advDataSourceList) {
        if (advDataSourceList == null || !advDataSourceList.contains(8)) {
            return false;
        }
        return true;
    }

    public static boolean isContainMobileCoreAdSource(List<Integer> advDataSourceList) {
        if (advDataSourceList == null || (!advDataSourceList.contains(9) && !advDataSourceList.contains(10))) {
            return false;
        }
        return true;
    }

    public static boolean isContainLoopMeAdSource(List<Integer> advDataSourceList) {
        if (advDataSourceList == null || !advDataSourceList.contains(16)) {
            return false;
        }
        return true;
    }

    public static boolean isContainVungleAdSource(List<Integer> advDataSourceList) {
        if (advDataSourceList == null || !advDataSourceList.contains(34)) {
            return false;
        }
        return true;
    }

    public static boolean isContainApplovinAdSource(List<Integer> advDataSourceList) {
        if (advDataSourceList == null || !advDataSourceList.contains(20)) {
            return false;
        }
        return true;
    }

    public static boolean isContainCheetahAdSource(List<Integer> advDataSourceList) {
        if (advDataSourceList == null || !advDataSourceList.contains(21)) {
            return false;
        }
        return true;
    }

    public static boolean isContainCheetahVideoAdSource(List<Integer> advDataSourceList) {
        if (advDataSourceList == null || !advDataSourceList.contains(38)) {
            return false;
        }
        return true;
    }

    public static boolean isContainGomoAdSource(List<Integer> advDataSourceList) {
        if (advDataSourceList == null || !advDataSourceList.contains(35)) {
            return false;
        }
        return true;
    }

    public static boolean isContainIronScrAdSource(List<Integer> advDataSourceList) {
        if (advDataSourceList == null || !advDataSourceList.contains(37)) {
            return false;
        }
        return true;
    }

    public static boolean isContainMoPubAdSource(List<Integer> advDataSourceList) {
        if (advDataSourceList == null || !advDataSourceList.contains(39)) {
            return false;
        }
        return true;
    }

    public static boolean isSupportFacebookAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean)) {
            return isContainValue(supportAdObjectTypes, 1);
        }
        if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
            return isContainValue(supportAdObjectTypes, 2);
        }
        return isContainValue(supportAdObjectTypes, 3);
    }

    public static boolean isSupportAdmobAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean) || BaseModuleDataItemBean.isBannerAd300_250(moduleDataItemBean)) {
            return isContainValue(supportAdObjectTypes, 4);
        }
        if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
            return isContainValue(supportAdObjectTypes, 5);
        }
        return isContainValue(supportAdObjectTypes, 6);
    }

    public static boolean isSupportLoopmeAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean)) {
            return isContainValue(supportAdObjectTypes, 7);
        }
        if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
            return isContainValue(supportAdObjectTypes, 8);
        }
        return false;
    }

    public static boolean isSupportMobileCoreAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        return isContainValue(supportAdObjectTypes, 9);
    }

    public static boolean isSupportVungleAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        return isContainValue(supportAdObjectTypes, 10);
    }

    public static boolean isSupportApplovinAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        return isContainValue(supportAdObjectTypes, 11);
    }

    public static boolean isSupportCheetahAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        return isContainValue(supportAdObjectTypes, 12);
    }

    public static boolean isSupportCheetahVideoAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        return isContainValue(supportAdObjectTypes, 15);
    }

    public static boolean isSupportGomoAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        return isContainValue(supportAdObjectTypes, 13);
    }

    public static boolean isSupportIronScrAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        return isContainValue(supportAdObjectTypes, 14);
    }

    public static boolean isSupportMoPubAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        return isContainValue(supportAdObjectTypes, 16);
    }

    public static boolean isSupportOfflineOnlineapiAdObjectType(BaseModuleDataItemBean moduleDataItemBean, int[] supportAdObjectTypes) {
        return isContainValue(supportAdObjectTypes, 0);
    }

    public static boolean isContainValue(int[] values, int value) {
        if (values == null) {
            return false;
        }
        for (int item : values) {
            if (item == value) {
                return true;
            }
        }
        return false;
    }
}
