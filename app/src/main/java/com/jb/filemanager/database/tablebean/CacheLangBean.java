package com.jb.filemanager.database.tablebean;

/**
 * Created by xiaoyu on 2016/10/25.
 */

public class CacheLangBean {
    public String mId;
    public int mTextId;
    public String mLang;
    public String mTitle;
    public String mDescription;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public int getTextId() {
        return mTextId;
    }

    public void setTextId(int textId) {
        this.mTextId = textId;
    }

    public String getLang() {
        return mLang;
    }

    public void setLang(String lang) {
        this.mLang = lang;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    @Override
    public String toString() {
        return "CleanCacheLangBean [mId=" + mId + ", mTextId=" + mTextId
                + ", mLang=" + mLang + ", mTitle=" + mTitle
                + ", mDescription=" + mDescription + "]";
    }

}
