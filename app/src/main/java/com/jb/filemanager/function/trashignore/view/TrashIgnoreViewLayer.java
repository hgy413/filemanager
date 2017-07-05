package com.jb.filemanager.function.trashignore.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreGroupBean;
import com.jb.filemanager.function.trashignore.contract.Contract;
import com.jb.filemanager.function.trashignore.view.adapter.TrashIgnoreAdapter;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.APIUtil;

import java.util.List;

/**
 * Created by xiaoyu on 2017/2/28 13:59.
 */

public class TrashIgnoreViewLayer implements Contract.View {

    private Context mContext;
    private Activity mActivity;
    private View mNoDataTip;
    private View mHasDataTip;
    private FloatingGroupExpandableListView mListView;
    private TrashIgnoreAdapter mAdapter;
    private View mView;

    public TrashIgnoreViewLayer(BaseActivity baseActivity) {
        mActivity = baseActivity;
        mContext = baseActivity.getApplicationContext();
        initializeView();
    }

    private void initializeView() {
        mView = View.inflate(mContext, R.layout.trash_ignore_view_layer, null);
        View titleGroup = mView.findViewById(R.id.trash_ignore_title_group);
        setGradientBg(titleGroup);
        View ivBack = mView.findViewById(R.id.trash_ignore_iv_back);
        View tvBack = mView.findViewById(R.id.trash_ignore_tv_back);
        setFinishListener(ivBack);
        setFinishListener(tvBack);
        mNoDataTip = mView.findViewById(R.id.trash_ignore_no_data_tip);
        mHasDataTip = mView.findViewById(R.id.trash_ignore_has_data_tip);
        mListView = (FloatingGroupExpandableListView) mView.findViewById(R.id.trash_ignore_flv);
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void showSuitableView(boolean isHaveData) {
        if (isHaveData) {
            mNoDataTip.setVisibility(View.GONE);
            mHasDataTip.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mNoDataTip.setVisibility(View.VISIBLE);
            mHasDataTip.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setListData(List<CleanIgnoreGroupBean> data) {
        if (mListView != null) {
            mAdapter = new TrashIgnoreAdapter(data, mContext);
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
                    if (mActivity != null) {
                        mActivity.finish();
                    }
                }
            });
        }
    }
}
