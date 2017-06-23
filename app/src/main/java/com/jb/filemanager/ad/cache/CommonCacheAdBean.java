package com.jb.filemanager.ad.cache;

import com.jb.filemanager.ad.data.AdWrapper;

import java.util.ArrayList;

/**
 * Created by wangying on 16/1/13.
 *
 */

public class CommonCacheAdBean extends BaseCacheAdBean {
    public CommonCacheAdBean(boolean isNeedCache, long effectiveTime) {
        super(isNeedCache, effectiveTime);
    }

    @Override
    public ArrayList<AdWrapper> filterNeedBeans() {
        return getCacheAdWrappers();
    }
}
