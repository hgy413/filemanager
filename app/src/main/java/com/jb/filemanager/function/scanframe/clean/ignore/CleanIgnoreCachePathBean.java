package com.jb.filemanager.function.scanframe.clean.ignore;


import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubAppCacheBean;

/**
 * 清理白名单缓存路径Bean
 *
 * @author chenbenbin
 */
public class CleanIgnoreCachePathBean extends CleanIgnoreBean {
    private String mTitle;
    private String mPackageName;
    private String mSubTitle;
    private String mPath;

    public CleanIgnoreCachePathBean() {
        super(TYPE_CACHE_PATH);
    }

    public CleanIgnoreCachePathBean(SubAppCacheBean bean) {
        this();
        mTitle = bean.getTitle();
        mPackageName = bean.getPackageName();
        mPath = bean.getDBKey();
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

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public void setSubTitle(String subTitle) {
        mSubTitle = subTitle;
    }

    @Override
    public String toString() {
        return "CleanIgnoreCachePathBean{" +
                "mTitle='" + mTitle + '\'' +
                ", mPackageName='" + mPackageName + '\'' +
                ", mSubTitle='" + mSubTitle + '\'' +
                ", mPath='" + mPath + '\'' +
                '}';
    }
}
