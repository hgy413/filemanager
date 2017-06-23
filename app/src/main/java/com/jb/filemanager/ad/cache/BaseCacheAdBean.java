package com.jb.filemanager.ad.cache;

import com.jb.filemanager.ad.data.AdWrapper;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;

import java.util.ArrayList;

/**
 * ZBoost的缓存广告基本类
 * Created by wangying on 16/1/11.
 */
public abstract class BaseCacheAdBean {

    /**
     * 是否需要缓存
     */
    private boolean mNeedCache;
    /**
     * 广告的缓存时间
     */
    // 缓存的起始时间
    private long mCacheTime;
    // 有效缓存时间
    private long mEffectiveTime;

    /**
     * 广告数据
     */
    private AdModuleInfoBean mAdModuleInfoBean;
    private ArrayList<AdWrapper> mCacheAdWrappers = new ArrayList<>();

    /**
     * @param isNeedCache   是否需要缓存
     * @param effectiveTime 缓存的有效时间
     */
    public BaseCacheAdBean(boolean isNeedCache, long effectiveTime) {
        mNeedCache = isNeedCache;
        mEffectiveTime = effectiveTime;
        mCacheTime = System.currentTimeMillis();
    }

    /**
     * 是否需要缓存
     *
     * @return
     */
    public boolean isNeedCache() {
        return mNeedCache;
    }

    /**
     * 设置缓存开始的时间
     *
     * @param cacheTime
     */
    public void setCacheTime(long cacheTime) {
        mCacheTime = cacheTime;
    }

    /**
     * 是否在缓存有效时间内，子类可以复写从而实现具体的有效判断
     *
     * @return
     */
    public boolean isEffective() {
        return (System.currentTimeMillis() - mCacheTime) < mEffectiveTime;
    }

    /**
     * 获取缓存广告的数目
     *
     * @return
     */
    public int getEffectiveAdCounts() {
        if (!isEffective()) {
            return 0;
        }
        return mCacheAdWrappers.size();
    }

    /**
     * 获取缓存的广告数据
     *
     * @return
     */
    public ArrayList<AdWrapper> getCacheAdWrappers() {
        if (isEffective()) {
            return mCacheAdWrappers;
        }
        return null;
    }


    public void setCacheAdWrappers(ArrayList<AdWrapper> cacheAdWrappers) {
        mCacheAdWrappers = cacheAdWrappers;
    }

    /**
     * @return
     */
    public AdModuleInfoBean getAdModuleInfoBean() {
        return mAdModuleInfoBean;
    }

    /**
     * @param adModuleInfoBean
     */
    public void setAdModuleInfoBean(AdModuleInfoBean adModuleInfoBean) {
        mAdModuleInfoBean = adModuleInfoBean;
    }

    /**
     * 帅选规则
     *
     * @return
     */
    public abstract ArrayList<AdWrapper> filterNeedBeans();

    /**
     * @param zBoostAdWrapper
     * @return
     */
    public boolean removeOneCacheAd(AdWrapper zBoostAdWrapper) {
        // TODO 后续这里是用getEffectiveCount？？？
        if (mCacheAdWrappers.size() >= 1) {
            return mCacheAdWrappers.remove(zBoostAdWrapper);
        }
        return false;
    }

}
