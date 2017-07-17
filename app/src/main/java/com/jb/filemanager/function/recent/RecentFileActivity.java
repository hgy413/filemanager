package com.jb.filemanager.function.recent;

import android.os.Bundle;
import android.widget.ListView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.ui.view.SearchTitleView;

/**
 * Created by xiaoyu on 2017/7/17 14:14.
 */

public class RecentFileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_file);

        initViews();
    }

    private void initViews() {
        SearchTitleView searchTitleView = (SearchTitleView) findViewById(R.id.search_title);
        ListView listView = (ListView) findViewById(R.id.recent_expand_lv);
    }
}
