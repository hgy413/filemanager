package com.jb.filemanager.function.apkmanager.searchresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.apkmanager.AppManagerActivity;
import com.jb.filemanager.util.imageloader.IconLoader;

import java.util.ArrayList;


/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/3 16:41
 */

public class AppManagerSearchResultActivity extends BaseActivity {

    private RecyclerView mRlSearchResult;
    private ArrayList<SearchResultBean> mResultList;
    private SearchResultAdapter mResultAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager_search_result);
        Intent intent = getIntent();
        mResultList = intent.getParcelableArrayListExtra(AppManagerActivity.SEARCH_RESULT);
        IconLoader.ensureInitSingleton(this);
        IconLoader.getInstance().bindServicer(this);
        initView();
        initData();
    }

    private void initView() {
        mRlSearchResult = (RecyclerView) findViewById(R.id.rl_search_result);
    }

    private void initData() {
        mRlSearchResult.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mResultAdapter = new SearchResultAdapter(mResultList);
        mRlSearchResult.setAdapter(mResultAdapter);
        mResultAdapter.setOnItemClickListener(new SearchResultAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int data) {
                Toast.makeText(AppManagerSearchResultActivity.this, "这是第" + data + "个", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IconLoader.getInstance().unbindServicer(this);
    }
}
