package com.jb.filemanager.home.bean;

/**
 * Created by bill wang on 2017/6/23.
 */

public class CategoryBean {

    private int mCategoryIconResId;
    private String mCategoryName;

    public CategoryBean(int categoryIconResId, String categoryName) {
        mCategoryIconResId = categoryIconResId;
        mCategoryName = categoryName;
    }

    public int getCategoryIconResId() {
        return mCategoryIconResId;
    }

    public String getCategoryName() {
        return mCategoryName;
    }
}
