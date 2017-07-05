package com.jb.filemanager.function.applock.model.bean;

import com.jb.filemanager.function.scanframe.bean.BaseGroupsDataBean;

import java.util.ArrayList;

/**
 * 入侵者照片一级数据模型
 * Created by kvan on 3/4/2016.
 */
public class IntruderDisplayBean extends BaseGroupsDataBean<IntruderDisplaySubBean> {
    private String mTimeTitle;

    public IntruderDisplayBean(String timeTitle, ArrayList<IntruderDisplaySubBean> subList) {
        super(subList);
        mTimeTitle = timeTitle;
    }

    public String getTimeTitle() {
        return mTimeTitle;
    }

    @Override
    public String toString() {
        return "IntruderDisplayBean{" +
                "mTimeTitle='" + mTimeTitle + '\'' +
                '}';
    }
}
