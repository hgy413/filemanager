package com.jiubang.commerce.ad.sdk;

import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.manager.AdControlManager;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;

public interface SdkAdSourceInterface {
    void loadAdMobAdInfo(AdSdkParamsBuilder adSdkParamsBuilder, BaseModuleDataItemBean baseModuleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener);

    void loadFaceBookAdInfo(AdSdkParamsBuilder adSdkParamsBuilder, BaseModuleDataItemBean baseModuleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener);

    void loadIronScrAdInfo(AdSdkParamsBuilder adSdkParamsBuilder, BaseModuleDataItemBean baseModuleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener);

    void loadLoopMeAdInfo(AdSdkParamsBuilder adSdkParamsBuilder, BaseModuleDataItemBean baseModuleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener);

    void loadMoPubAdInfo(AdSdkParamsBuilder adSdkParamsBuilder, BaseModuleDataItemBean baseModuleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener);

    void loadMobileCoreAdInfo(AdSdkParamsBuilder adSdkParamsBuilder, BaseModuleDataItemBean baseModuleDataItemBean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener);
}
