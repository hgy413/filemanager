package com.jb.filemanager.function.docmanager;

import java.util.ArrayList;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/21 17:38
 */

public interface DocScanListener {
    void onScanStart();
    void onScanFinish(ArrayList<DocGroupBean> arrayList, boolean keepCheck);
    void onLoadError();
    void onLoadProgress(int progress);
}
