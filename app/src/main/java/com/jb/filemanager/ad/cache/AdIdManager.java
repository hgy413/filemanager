package com.jb.filemanager.ad.cache;


import com.jb.filemanager.ad.AdEntry;
import com.jb.filemanager.util.Logger;

/**
 * 获取虚拟ID的管理类
 * Created by wangying on 16/1/12.
 */
public class AdIdManager {

    private static final String LOGER_TAG = "AdManager";

    /**
     * 应用分发的错误虚拟广告ID
     */
    public static final int WORNG_ID = 10;

    /**
     *  广告测试ID
     */
    private static final int TEST_ID  = 3266;

    /**
     * 获取广告位对应的虚拟ID
     *
     * @return vid
     */
    public int getAdId(int entrance) {

        int id = WORNG_ID;

        switch (entrance) {
            case AdEntry.ENTRANCE_TEST:
                id = TEST_ID;
                break;
            default:
                break;
        }

        Logger.i(LOGER_TAG, "entrance:" + entrance + "----->" + "getAppAdId("
                + entrance + ")" + "的ID=" + id);

        return id;
    }
}
