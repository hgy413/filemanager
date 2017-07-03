package com.jb.filemanager.function.zipfile;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.commomview.ProgressWheel;
import com.jb.filemanager.function.zipfile.bean.ZipFileGroupBean;
import com.jb.filemanager.function.zipfile.bean.ZipFileItemBean;
import com.jb.filemanager.function.zipfile.dialog.ZipFileOperationDialog;
import com.jb.filemanager.util.ConvertUtils;

import java.util.List;

/**
 * Created by xiaoyu on 2017/6/29 17:01.
 */

public class ZipFileActivity extends BaseActivity implements ZipActivityContract.View {

    private ZipFileActivityPresenter mPresenter = new ZipFileActivityPresenter(this);
    private ProgressWheel mProgress;
    private ExpandableListView mListView;
    private ZipListAdapter mAdapter;
    private ZipFileOperationDialog mOperationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_file);
        /*// /data
        Log.e("Environment", Environment.getDataDirectory().getPath());
        // mounted
        Log.e("Environment", Environment.getExternalStorageState());
        // /system
        Log.e("Environment", Environment.getRootDirectory().getPath());
        // /storage/emulated/0
        Log.e("Environment", Environment.getExternalStorageDirectory().getPath());
        // /storage/emulated/0/Android/data/com.jb.filemanager/cache
        Log.e("Environment", getExternalCacheDir().getPath());
        // /data/data/com.jb.filemanager/cache
        Log.e("Environment", getCacheDir().getPath());
        // /data/data/com.jb.filemanager/files
        Log.e("Environment", getFilesDir().getPath());
        // /storage/emulated/0/Android/data/com.jb.filemanager/files/Test
        Log.e("Environment", getExternalFilesDir("Test").getPath());*/
        mProgress = (ProgressWheel) findViewById(R.id.zip_progress);
        mListView = (ExpandableListView) findViewById(R.id.zip_expand_lv);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                if (mPresenter != null) {
                    mPresenter.onItemClick(groupPosition, childPosition);
                }
                return true;
            }
        });
        mPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void setWidgetsState(boolean isLoading) {
        mProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mListView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setListData(List<ZipFileGroupBean> data) {
        mAdapter = new ZipListAdapter(data);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showOperationDialog(ZipFileItemBean fileItem) {
        if (mOperationDialog != null && mOperationDialog.isShowing()) {
            mOperationDialog.dismiss();
            mOperationDialog = null;
        }
        mOperationDialog = new ZipFileOperationDialog(this, fileItem);
        mOperationDialog.show();
    }

    private class ZipListAdapter extends BaseExpandableListAdapter {

        private List<ZipFileGroupBean> mGroupList;

        ZipListAdapter(List<ZipFileGroupBean> data) {
            mGroupList = data;
        }

        @Override
        public int getGroupCount() {
            return mGroupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroupList.get(groupPosition).getChildCount();
        }

        @Override
        public ZipFileGroupBean getGroup(int groupPosition) {
            return mGroupList.get(groupPosition);
        }

        @Override
        public ZipFileItemBean getChild(int groupPosition, int childPosition) {
            return mGroupList.get(groupPosition).getChild(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(parent.getContext().getApplicationContext(), R.layout.group_zip_file, null);
                holder.groupTime = (TextView) convertView.findViewById(R.id.group_zip_time);
                holder.groupCheckBox = (ImageView) convertView.findViewById(R.id.group_zip_iv);
                convertView.setTag(R.layout.group_zip_file, holder);
            } else {
                holder = (ViewHolder) convertView.getTag(R.layout.group_zip_file);
            }
            ZipFileGroupBean group = getGroup(groupPosition);
            holder.groupTime.setText(group.getGroupTimeStr());
            holder.groupCheckBox.setImageResource(R.drawable.choose_all);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(parent.getContext().getApplicationContext(), R.layout.item_zip_file, null);
                holder.icon = (ImageView) convertView.findViewById(R.id.item_zip_icon);
                holder.name = (TextView) convertView.findViewById(R.id.item_zip_name);
                holder.size = (TextView) convertView.findViewById(R.id.item_zip_size);
                holder.itemCheckBox = (ImageView) convertView.findViewById(R.id.item_zip_checkbox);
                convertView.setTag(R.layout.item_zip_file, holder);
            } else {
                holder = (ViewHolder) convertView.getTag(R.layout.item_zip_file);
            }
            ZipFileItemBean child = getChild(groupPosition, childPosition);
            holder.icon.setImageResource(R.drawable.common_default_app_icon);
            holder.name.setText(child.getFileName());
            holder.size.setText(ConvertUtils.formatFileSize(child.getFileSize()));
            holder.itemCheckBox.setImageResource(R.drawable.choose_all);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private class ViewHolder {
        // group
        TextView groupTime;
        ImageView groupCheckBox;
        // item
        ImageView icon;
        TextView name;
        TextView size;
        ImageView itemCheckBox;
    }
}
