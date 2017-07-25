package com.jb.filemanager.function.trashignore.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreGroupBean;
import com.jb.filemanager.function.trashignore.contract.Contract;
import com.jb.filemanager.function.trashignore.presenter.TrashIgnorePresenter;
import com.jb.filemanager.function.trashignore.view.adapter.TrashIgnoreAdapter;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.imageloader.IconLoader;

import java.util.List;


/**
 * Created by xiaoyu on 2017/2/28 11:30.
 */

public class TrashIgnoreActivity extends BaseActivity implements Contract.View {

    private TrashIgnorePresenter mPresenter;
    private TextView mTvCommonActionBarTitle;
    private View mNoDataTip;
    private View mHasDataTip;
    private RelativeLayout mLlTitle;
    private FloatingGroupExpandableListView mListView;
    private TrashIgnoreAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trash_ignore_view_layer);
        IconLoader.ensureInitSingleton(this);
        IconLoader.getInstance().bindServicer(this);
        initializeView();
        mPresenter = new TrashIgnorePresenter(this, getApplicationContext());
        mPresenter.onEnterActivity();
    }

    private void initializeView() {
        mLlTitle = (RelativeLayout) findViewById(R.id.ll_title);
        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mNoDataTip = findViewById(R.id.trash_ignore_no_data_tip);
        mHasDataTip = findViewById(R.id.trash_ignore_has_data_tip);
        mListView = (FloatingGroupExpandableListView) findViewById(R.id.trash_ignore_flv);
        setFinishListener(mTvCommonActionBarTitle);
    }

    @Override
    public void showSuitableView(boolean isHaveData) {
        if (isHaveData) {
            mNoDataTip.setVisibility(View.GONE);
            mHasDataTip.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            mLlTitle.setBackgroundResource(R.color.white);
        } else {
            mNoDataTip.setVisibility(View.VISIBLE);
            mHasDataTip.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            mLlTitle.setBackgroundResource(R.color.trash_ignore_bg);
        }
    }

    @Override
    public void setListData(List<CleanIgnoreGroupBean> data) {
        if (mListView != null) {
            mAdapter = new TrashIgnoreAdapter(data, this.getApplication());
            mAdapter.setListener(new TrashIgnoreAdapter.RemoveListener() {
                @Override
                public void onRemoveAll() {
                    showSuitableView(false);
                }
            });
            mListView.setAdapter(new WrapperExpandableListAdapter(mAdapter));
        }
    }

    @Override
    public void notifyListDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setGradientBg(View gradientBg) {
        GradientDrawable memoryBg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.parseColor("#0084ff"), Color.parseColor("#3bd6f2")});
        memoryBg.setShape(GradientDrawable.RECTANGLE);
        if (gradientBg != null) {
            APIUtil.setBackground(gradientBg, memoryBg);
        }
    }

    private void setFinishListener(View finishListener) {
        if (finishListener != null) {
            finishListener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        IconLoader.getInstance().unbindServicer(this);
        mPresenter.onExitActivity();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
