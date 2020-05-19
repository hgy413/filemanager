package com.jiubang.commerce.ad.params;

import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import java.util.ArrayList;
import java.util.List;

public class AdSet {
    public static final int ADV_ADINFOBEAN = 0;
    public static final int ALL_ONLINE_AD_SHOW_TYPE = -1;
    private final List<AdType> mAdTypes;

    AdSet(Builder b) {
        this.mAdTypes = b.mAdTypes;
    }

    public boolean isContain(BaseModuleDataItemBean bean) {
        if (bean == null) {
            return false;
        }
        int advDataSource = bean.getAdvDataSource();
        int advShowType = bean.getOnlineAdvType();
        boolean ret = false;
        for (AdType adType : this.mAdTypes) {
            if (adType.mAdvDataSource == 0) {
                if (AdModuleInfoBean.isGomoAd(bean) || AdModuleInfoBean.isS2SAd(bean) || bean.isOfflineAdType()) {
                    ret = true;
                    continue;
                } else {
                    ret = false;
                    continue;
                }
            } else if (advDataSource != adType.mAdvDataSource) {
                continue;
            } else if (-1 == adType.mOnlineAdvType) {
                ret = true;
                continue;
            } else if (adType.mOnlineAdvType == advShowType) {
                ret = true;
                continue;
            } else {
                ret = false;
                continue;
            }
            if (ret) {
                break;
            }
        }
        return ret;
    }

    public static class Builder {
        List<AdType> mAdTypes = new ArrayList();

        public Builder add(AdType adType) {
            if (adType != null) {
                this.mAdTypes.add(adType);
            }
            return this;
        }

        public AdSet build() {
            return new AdSet(this);
        }
    }

    public static class AdType {
        final int mAdvDataSource;
        int mOnlineAdvType = -1;

        public AdType(int advDataSource, int adShowType) {
            this.mAdvDataSource = advDataSource;
            this.mOnlineAdvType = adShowType;
        }
    }
}
