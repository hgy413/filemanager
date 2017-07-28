package com.jb.filemanager.globaldata;

import android.os.Bundle;
import android.view.View;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;

/**
 * Created by xiaoyu on 2017/7/27 9:51.
 * <p>
 *     测试Activity
 * </p>
 */

public final class TestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void update(View v) {
        GlobalFileManager.getInstance().initAllData();
    }

    public void delete(View v) {

    }

    // 重新扫描数据库
    public void rescan(View v) {
        GlobalFileManager.getInstance().loadData();
    }
}