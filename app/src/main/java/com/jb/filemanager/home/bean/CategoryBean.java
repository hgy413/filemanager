package com.jb.filemanager.home.bean;

/**
 * Created by bill wang on 2017/6/23.
 */

public class CategoryBean {

    private int mCategoryIconResId;
    private String mCategoryName;
    private int mCategoryNumber;

    public CategoryBean(int categoryIconResId, String categoryName, int categoryNumber) {
        mCategoryIconResId = categoryIconResId;
        mCategoryName = categoryName;
        mCategoryNumber = categoryNumber;
    }

    public int getCategoryIconResId() {
        return mCategoryIconResId;
    }

    public int getCategoryNumber() {
        return mCategoryNumber;
    }

    public String getCategoryName() {
        return mCategoryName;
    }
}
