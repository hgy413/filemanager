package com.jb.filemanager.function.scanframe.bean.common.itemcommon;


import com.jb.filemanager.R;

/**
 * Created by xiaoyu on 2016/10/20.<br>
 * 一级列表显示的内容的类型<br>
 */

public enum GroupType {
    /**
     * 应用缓存
     */
    APP_CACHE(R.string.clean_group_cache,
//            R.drawable.cache, R.drawable.cache),
            R.drawable.ic_about_logo, R.drawable.ic_about_logo),
    /**
     * 系统缓存
     */
    DEEP_CACHE(R.string.clean_group_cache,
//            R.drawable.cache, R.drawable.cache),
            R.drawable.ic_about_logo, R.drawable.ic_about_logo),
    /**
     * 残留文件
     */
    RESIDUE(R.string.clean_group_residue,
            /*R.drawable.residue,
            R.drawable.child_residue),*/
            R.drawable.ic_about_logo,
            R.drawable.ic_about_logo),
    /**
     * 系统缓存
     */
    SYS_CACHE(R.string.clean_group_cache,
//            R.drawable.cache, R.drawable.cache),
            R.drawable.ic_about_logo, R.drawable.ic_about_logo),
    /**
     * 临时文件
     */
    /*TEMP(R.string.clean_group_temp, R.drawable.temp,
            R.drawable.child_temp),*/
    TEMP(R.string.clean_group_temp, R.drawable.ic_about_logo,
            R.drawable.ic_about_logo),
    /**
     * APK文件
     */
    /*APK(R.string.clean_group_apk, R.drawable.apk,
            R.drawable.apk),*/
    APK(R.string.clean_group_apk, R.drawable.ic_about_logo,
            R.drawable.ic_about_logo),
    /**
     * 大文件
     */
    BIG_FILE(R.string.clean_group_big_file,
            /*R.drawable.bf,
            R.drawable.child_bf),*/
            R.drawable.ic_about_logo,
            R.drawable.ic_about_logo),
    /**
     * 大文件夹
     */
    BIG_FOLDER(R.string.clean_group_big_file,
            /*R.drawable.bf,
            R.drawable.child_bf),*/
            R.drawable.ic_about_logo,
            R.drawable.ic_about_logo),
    /**
     * 广告
     */
    /*AD(R.string.clean_group_ad, R.drawable.ad,
            R.drawable.child_ad),*/
    AD(R.string.clean_group_ad, R.drawable.ic_about_logo,
            R.drawable.ic_about_logo),
    /**
     * 内存
     */
    /*MEMORY(R.string.clean_group_memory,
            R.drawable.memory,
            R.drawable.memory);*/
    MEMORY(R.string.clean_group_memory,
            R.drawable.ic_about_logo,
            R.drawable.ic_about_logo);
    /**
     * 标题ID
     */
    private int mNameId;
    /**
     * 组的图标ID
     */
    private int mGroupIconId;
    /**
     * 子项的图标ID
     */
    private int mChildIconId;

    GroupType(int name, int groupIcon, int childIcon) {
        mNameId = name;
        mGroupIconId = groupIcon;
        mChildIconId = childIcon;
    }

    public int getGroupIconId() {
        return mGroupIconId;
    }

    public int getChildIconId() {
        return mChildIconId;
    }

    public int getNameId() {
        return mNameId;
    }
}
