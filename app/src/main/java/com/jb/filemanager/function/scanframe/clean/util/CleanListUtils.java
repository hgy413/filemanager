package com.jb.filemanager.function.scanframe.clean.util;


import com.jb.filemanager.function.scanframe.bean.common.god.BaseChildBean;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 垃圾清理数据列表工具类
 *
 * @author chenbenbin
 */
public class CleanListUtils {
    /**
     * 将队列按从大到小进行排序
     */
    public static void sortItemList(ArrayList<? extends ItemBean> itemList) {
        SizeComparator sizeComparator = new SizeComparator();
        int size = itemList.size();
        for (int i = 0; i < size; i++) {
            ItemBean item = itemList.get(i);
            ArrayList<SubItemBean> subItemList = item.getSubItemList();
            if (!subItemList.isEmpty()) {
                Collections.sort(subItemList, sizeComparator);
            }
        }
        Collections.sort(itemList, sizeComparator);
    }

    /**
     * 根据删除的数据队列更新内存数据
     *
     * @param list    内存数据队列
     * @param delList 删除的数据队列
     */
    public static void updateDataList(ArrayList<? extends ItemBean> list,
                                      List<ItemBean> delList) {
        for (ItemBean bean : delList) {
            ArrayList<SubItemBean> subItemList = bean.getSubItemList();
            if (list.contains(bean) && subItemList.isEmpty()) {
                // 当前内存数据包含这条被删除的数据 && 被删除的数据的三级列表为空
                list.remove(bean);
            } else {
                // 包含三级项的需要根据三级项的选中状态决定
                Iterator<SubItemBean> iterator = subItemList.iterator();
                while (iterator.hasNext()) {
                    SubItemBean subItem = iterator.next();
                    if (subItem.isChecked()) {
                        iterator.remove();
                    }
                }
                if (subItemList.isEmpty()) {
                    list.remove(bean);
                }
            }
        }
        // 清空"删除缓存队列"
        delList.clear();
    }

    /**
     * 对list从大到小排序的类
     *
     * @author wangying
     */
    static class SizeComparator implements Comparator<BaseChildBean> {
        @Override
        public int compare(BaseChildBean arg0, BaseChildBean arg1) {
            // 第一个比第二个大，返回-1
            if (arg0.getSize() > arg1.getSize()) {
                return -1;
                // 第一个和第二个相等，返回0
            } else if (arg0.getSize() == arg1.getSize()) {
                return 0;
                // 第一个比第二个小，返回1
            } else {
                return 1;
            }
        }
    }
}
