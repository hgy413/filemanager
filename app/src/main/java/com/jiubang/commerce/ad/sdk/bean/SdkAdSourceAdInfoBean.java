package com.jiubang.commerce.ad.sdk.bean;

import java.util.ArrayList;
import java.util.List;

public class SdkAdSourceAdInfoBean {
    private List<SdkAdSourceAdWrapper> mAdViewList;
    private boolean mIsFakeFbNative = false;

    public void addAdViewList(String fbId, List<Object> adViewList) {
        if (this.mAdViewList == null) {
            this.mAdViewList = new ArrayList();
        }
        if (adViewList != null && !adViewList.isEmpty()) {
            for (Object obj : adViewList) {
                this.mAdViewList.add(new SdkAdSourceAdWrapper(fbId, obj));
            }
        }
    }

    public List<SdkAdSourceAdWrapper> getAdViewList() {
        return this.mAdViewList;
    }

    public boolean isFakeFbNative() {
        return this.mIsFakeFbNative;
    }

    public void setFakeFbNative(boolean b) {
        this.mIsFakeFbNative = b;
    }
}
