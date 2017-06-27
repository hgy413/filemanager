package com.jb.filemanager.function.scanframe.clean.ignore;


import com.jb.filemanager.function.scanframe.bean.cachebean.AppCacheBean;

/**
 * 清理白名单缓存应用Bean
 *
 * @author chenbenbin
 */
public class CleanIgnoreCacheAppBean extends CleanIgnoreBean {
    private String mTitle;
    private String mPackageName;

    public CleanIgnoreCacheAppBean() {
        super(TYPE_CACHE_APP);
    }

    public CleanIgnoreCacheAppBean(AppCacheBean bean) {
        this();
        mTitle = bean.getTitle();
        mPackageName = bean.getPackageName();
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    @Override
    public String toString() {
        return "CleanIgnoreCacheAppBean{" +
                "mTitle='" + mTitle + '\'' +
                ", mPackageName='" + mPackageName + '\'' +
                '}';
    }
}
