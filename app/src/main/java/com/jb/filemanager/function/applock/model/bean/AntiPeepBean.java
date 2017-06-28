package com.jb.filemanager.function.applock.model.bean;

/**
 * 防偷窥数据Bean
 *
 * @author chenbenbin
 */
public class AntiPeepBean {
    /**
     * 查看应用的包名
     */
    private String mPackageName;
    /**
     * 保存的文件路径
     */
    private String mPath;
    /**
     * 是否已读
     */
    private boolean mIsRead;
    /**
     * 创建时间
     */
    private long mCreateTime;

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

    public boolean isRead() {
        return mIsRead;
    }

    public void setIsRead(boolean isRead) {
        mIsRead = isRead;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(long createTime) {
        mCreateTime = createTime;
    }

    @Override
    public String toString() {
        return "AntiPeepBean{" +
                "mPackageName='" + mPackageName + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mIsRead=" + mIsRead +
                ", mCreateTime=" + mCreateTime +
                '}';
    }
}
