package com.jb.filemanager.function.image.modle;

import com.jb.filemanager.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nieyh on 17-7-3.
 * 图片分组数据
 */

public class ImageGroupModle {
    //时间
    public Calendar mCalendar;
    //时间
    public String mTimeDate;
    //图片数据列表
    public List<ImageModle> mImageModleList;
    //图片分组模组数据

    public ImageGroupModle() {
        mImageModleList = new ArrayList<>();
    }

    public ImageGroupModle(long date, List<ImageModle> imageModleList) {
        this.mCalendar = Calendar.getInstance();
        this.mCalendar.setTimeInMillis(date);
        this.mTimeDate = TimeUtil.getStringDate(mCalendar.getTime());
        this.mImageModleList = imageModleList;
    }
}
