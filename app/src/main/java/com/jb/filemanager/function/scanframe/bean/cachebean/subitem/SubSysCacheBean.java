package com.jb.filemanager.function.scanframe.bean.cachebean.subitem;


import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemType;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xiaoyu on 2016/10/21.<br>
 * 缓存文件下系统缓存下的项<br>
 */

public class SubSysCacheBean extends SubItemBean {
    private AppItemInfo mInfo;

    public SubSysCacheBean(AppItemInfo info) {
        super(SubItemType.SYS);
        mInfo = info;
    }

    @Override
    public String getTitle() {
        return mInfo.getAppName();
    }

    @Override
    public long getSize() {
        return mInfo.getAppCacheSize();
    }

    @Override
    public String getPath() {
        return mInfo.getAppPackageName();
    }

    @Override
    public void setPath(String path) {

    }

    public String getPackageName() {
        return mInfo.getAppPackageName();
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setSize(long size) {

    }

    @Override
    public String getKey() {
        return mInfo.getAppPackageName();
    }

    public static ArrayList<SubItemBean> createFromAppItemInfo(List<AppItemInfo> list) {
        ArrayList<SubItemBean> resultList = new ArrayList<>();
        for (AppItemInfo info : list) {
            SubSysCacheBean bean = new SubSysCacheBean(info.clone());
            bean.setChecked(true);
            resultList.add(bean);
        }
        return resultList;
    }

    @Override
    public boolean isChecked() {
        return mInfo.isChecked();
    }

    @Override
    public void setChecked(boolean isChecked) {
        mInfo.setChecked(isChecked);
    }

    @Override
    public boolean isDefaultCheck() {
        return true;
    }
}
