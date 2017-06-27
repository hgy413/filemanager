package com.jb.filemanager.function.image;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;

/**
 * Created by bill wang on 2017/6/27.
 *
 */

public class ImageActivity extends BaseActivity implements View.OnClickListener, ImageContract.View {

    private ImageContract.Presenter mPresenter;

    private ExpandableListView mElvImages;
    private ImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        initView();

        mPresenter = new ImagePresenter(this, new ImageSupport());
        mPresenter.onCreate(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mPresenter != null) {
            mPresenter.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }

        super.onDestroy();
    }

    @Override
    protected void onPressedHomeKey() {
        if (mPresenter != null) {
            mPresenter.onPressHomeKey();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter != null) {
            mPresenter.onClickBackButton(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPresenter != null) {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }


    // private start

    private void initView() {
        TextView back = (TextView) findViewById(R.id.tv_common_action_bar_with_search_title);
        if (back != null) {
            back.getPaint().setAntiAlias(true);
            back.setText(R.string.image_title);
            back.setOnClickListener(this);
        }

        mElvImages = (ExpandableListView) findViewById(R.id.elv_image);
        if (mElvImages != null) {
            mAdapter = new ImageAdapter();
            mElvImages.setAdapter(mAdapter);
        }
    }

    // private end

    // implements View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_with_search_title:
                if (mPresenter != null) {
                    mPresenter.onClickBackButton(false);
                }
                break;
            case R.id.iv_common_action_bar_with_search_search:
                // TODO
                break;
            default:
                break;
        }
    }

    // implements ImageContract.View
    @Override
    public void finishActivity() {
        super.onBackPressed();
    }




    private static class ImageAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
