package com.jb.filemanager.function.applock.model.bean;

/**
 * 推荐加锁应用
 *
 * @author chenbenbin
 */
public class RecommendLockApp {
    public static final String PACKAGE_NAME = "pn";
    public static final String LEVEL = "l";
    private String mPackageName;
    private int mLevel;

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    @Override
    public String toString() {
        return "RecommendLockApp{" +
                "mPackageName='" + mPackageName + '\'' +
                ", mLevel=" + mLevel +
                '}';
    }
}
