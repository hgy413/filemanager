package com.jb.filemanager.function.trash;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;

public class NoNeedCleanActivity extends BaseActivity {

    private LinearLayout mLlTitle;
    private TextView mTvCommonActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_need_clean);
        initView();
        initClick();
    }

    private void initView() {
        mLlTitle = (LinearLayout) findViewById(R.id.ll_title);
        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
    }

    private void initClick() {
        mTvCommonActionBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
