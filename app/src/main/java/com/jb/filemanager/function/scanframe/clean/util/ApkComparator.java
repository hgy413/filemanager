package com.jb.filemanager.function.scanframe.clean.util;

/**
 * ApkComparator
 *
 * @author chenbenbin
 */


import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;

import java.util.Comparator;

/**
 * 对APK进行排序，按照勾选与否排，再按大小排
 *
 * @author chenbenbin
 */
public class ApkComparator implements Comparator<ItemBean> {
    @Override
    public int compare(ItemBean arg0, ItemBean arg1) {
        // 勾选排序
        if (arg0.isAllSelected() && !arg1.isAllSelected()) {
            return -1;
        } else if (!arg0.isAllSelected() && arg1.isAllSelected()) {
            return 1;
        }
        // 大小排序
        if (arg0.getSize() > arg1.getSize()) {
            return -1;
        } else if (arg0.getSize() == arg1.getSize()) {
            return 0;
        } else {
            return 1;
        }
    }
}
