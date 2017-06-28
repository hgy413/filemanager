package com.jb.filemanager.function.applock.model.bean;

/**
 * 入侵者照片二级级数据模型
 * Created by kvan on 3/4/2016.
 */
public class IntruderDisplaySubBean {
    private String mPath;
    private String mDate;
    private boolean mIsReaded;

    public IntruderDisplaySubBean(String path, String date) {
        mPath = path;
        mDate = date;
    }

    public String getPath() {
        return mPath;
    }

    public String getDate() {
        return mDate;
    }

    public boolean isReaded() {
        return mIsReaded;
    }

    public void setIsReaded(boolean isReaded) {
        mIsReaded = isReaded;
    }

    @Override
    public String toString() {
        return "IntruderDisplaySubBean{" +
                "mPath='" + mPath + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mIsReaded=" + mIsReaded +
                '}';
    }
}
