package com.jb.filemanager.eventbus;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/24 14:52
 */

public class DocFileScanFinishEvent {
    public int mDocFileCount;

    public DocFileScanFinishEvent(int docFileCount) {
        mDocFileCount = docFileCount;
    }
}
