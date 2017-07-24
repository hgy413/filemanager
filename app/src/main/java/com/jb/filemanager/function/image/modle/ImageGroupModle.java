package com.jb.filemanager.function.image.modle;

import com.jb.filemanager.commomview.GroupSelectBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 17-7-3.
 * 图片分组数据
 */

public class ImageGroupModle {
    //时间
    public String mTimeDate;
    //图片数据列表 包含三个三个的数组列表
    public List<List<ImageModle>> mImageModleList;
    //图片分组模组数据
    public GroupSelectBox.SelectState mSelectState = GroupSelectBox.SelectState.NONE_SELECTED;

    public ImageGroupModle() {
        mImageModleList = new ArrayList<>();
    }

}
