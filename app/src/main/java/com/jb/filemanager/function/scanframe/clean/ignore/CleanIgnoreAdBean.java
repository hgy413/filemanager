package com.jb.filemanager.function.scanframe.clean.ignore;


import com.jb.filemanager.function.scanframe.bean.adbean.AdBean;

/**
 * 清理白名单广告Bean
 *
 * @author chenbenbin
 */
public class CleanIgnoreAdBean extends CleanIgnoreBean {
    private String mTitle;
    private String mPath;

    public CleanIgnoreAdBean() {
        super(TYPE_AD);
    }

    public CleanIgnoreAdBean(AdBean bean) {
        this();
        mTitle = bean.getTitle();
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

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    @Override
    public String toString() {
        return "CleanIgnoreAdBean{" +
                "mTitle='" + mTitle + '\'' +
                ", mPath='" + mPath + '\'' +
                '}';
    }
}
