package com.jb.filemanager.function.scanframe.clean.ignore;

/**
 * 清理白名单Bean基类
 *
 * @author chenbenbin
 */
public abstract class CleanIgnoreBean {
    public static final int TYPE_CACHE_APP = 1;
    public static final int TYPE_CACHE_PATH = 2;
    public static final int TYPE_RESIDUE = 3;
    public static final int TYPE_AD = 4;
    private int mType;

    public CleanIgnoreBean(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public abstract String getTitle();

    public abstract void setTitle(String title);
}
