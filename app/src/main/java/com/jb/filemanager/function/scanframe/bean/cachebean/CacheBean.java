package com.jb.filemanager.function.scanframe.bean.cachebean;


import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;

/**
 * Created by xiaoyu on 2016/10/21.
 */

public abstract class CacheBean extends ItemBean {
    public CacheBean() {
        super(GroupType.APP_CACHE);
    }
}
