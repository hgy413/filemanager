package com.jb.filemanager.util;

import com.jb.filemanager.TheApplication;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

public class DrawUtils {

    /**
     * dip/dp转像素
     *
     * @param dipValue dip或 dp大小
     * @return 像素值
     */
    public static int dip2px(float dipValue) {
        float density = TheApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * density + 0.5f);
    }

    /**
     * 像素转dip/dp
     *
     * @param pxValue 像素大小
     * @return dip值
     */
    public static int px2dip(float pxValue) {
        float density = TheApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }
}
