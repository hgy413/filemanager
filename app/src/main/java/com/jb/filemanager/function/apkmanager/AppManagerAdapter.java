package com.jb.filemanager.function.apkmanager;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.applock.adapter.AbsAdapter;
import com.jb.filemanager.function.trash.adapter.ItemCheckBox;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.imageloader.IconLoader;

import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/6/29 15:23
 */

class AppManagerAdapter extends AbsAdapter<AppGroupBean> {


    private static final String TAG = "mCheckedCount";
    private int mCheckedCount;
    private OnItemChosenListener mOnItemChosenListener;

    AppManagerAdapter(List<AppGroupBean> groups) {
        super(groups);
        mCheckedCount = groups.get(0).getchildrenSize();//默认用户应用都选中
    }

    @Override
    public View onGetGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applock_group, parent, false);
        GroupItemViewHolder viewHolder = new GroupItemViewHolder(convertView);
        final AppGroupBean appGroupBean = mGroups.get(groupPosition);
        viewHolder.mTvGroupTitle.setText(appGroupBean.mGroupTitle);
        viewHolder.mSelectBox
                .setImageSource(R.drawable.choose_none,
                        R.drawable.choose_part,
                        R.drawable.choose_all);

        viewHolder.mSelectBox.setState(appGroupBean.mSelectState);
        viewHolder.mSelectBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appGroupBean.mSelectState == GroupSelectBox.SelectState.ALL_SELECTED) {
                    appGroupBean.mSelectState = GroupSelectBox.SelectState.NONE_SELECTED;
                    for (AppChildBean childBean : appGroupBean.getChildren()) {
                        childBean.mIsCheckd = false;
                    }
                } else {
                    appGroupBean.mSelectState = GroupSelectBox.SelectState.ALL_SELECTED;
                    for (AppChildBean childBean : appGroupBean.getChildren()) {
                        childBean.mIsCheckd = true;
                    }
                }
                handleCheckedCount();
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public View onGetChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_child, parent, false);
        ChildItemViewHolder viewHolder = new ChildItemViewHolder(convertView);
        final AppGroupBean appGroupBean = mGroups.get(groupPosition);
        final AppChildBean child = appGroupBean.getChild(childPosition);
        IconLoader.getInstance().displayImage(child.mPackageName, viewHolder.mIvAppIcon);
        viewHolder.mTvAppName.setText(child.mAppName);
        viewHolder.mTvAppSize.setText(child.mAppSize);
        viewHolder.mItemCheckBox.setImageRes(R.drawable.choose_none,
                R.drawable.choose_all);
        viewHolder.mItemCheckBox.setChecked(child.mIsCheckd);
        viewHolder.mItemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                child.mIsCheckd = !child.mIsCheckd;
                updateGroupState(appGroupBean);
            }
        });
        return convertView;
    }

    private void updateGroupState(AppGroupBean appGroupBean) {
        boolean isAllSelect = true;
        boolean isPartSelect = false;
        for (AppChildBean childBean : appGroupBean.getChildren()) {
            isAllSelect = isAllSelect && childBean.mIsCheckd;
            isPartSelect = isPartSelect || childBean.mIsCheckd;
        }
        if (isAllSelect) {
            appGroupBean.mSelectState = GroupSelectBox.SelectState.ALL_SELECTED;
        } else if (isPartSelect) {
            appGroupBean.mSelectState = GroupSelectBox.SelectState.MULT_SELECTED;
        } else {
            appGroupBean.mSelectState = GroupSelectBox.SelectState.NONE_SELECTED;
        }
        handleCheckedCount();
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void handleCheckedCount() {
        mCheckedCount = 0;
        for (AppGroupBean groupBean : mGroups) {
            List<AppChildBean> children = groupBean.getChildren();
            for (AppChildBean childBean : children) {
                if (childBean.mIsCheckd) {
                    mCheckedCount++;
                }
            }
            if (mOnItemChosenListener != null) {
                mOnItemChosenListener.onItemChosen(mCheckedCount);
            }
        }
        Logger.d(TAG, ":    " + mCheckedCount);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class GroupItemViewHolder {
        View mView;
        TextView mTvGroupTitle;
        GroupSelectBox mSelectBox;

        GroupItemViewHolder(View convertView) {
            mView = convertView;
            mSelectBox = (GroupSelectBox) convertView.findViewById(R.id.activity_applock_group_selectbox);
            mTvGroupTitle = (TextView) convertView.findViewById(R.id.activity_applock_group_title);
        }
    }

    private static class ChildItemViewHolder {
        ImageView mIvAppIcon;
        TextView mTvAppName;
        TextView mTvAppSize;
        ItemCheckBox mItemCheckBox;

        ChildItemViewHolder(View convertView) {
            mIvAppIcon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            mTvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
            mTvAppSize = (TextView) convertView.findViewById(R.id.tv_app_size);
            mItemCheckBox = (ItemCheckBox) convertView.findViewById(R.id.icb_child_check);
        }
    }

    public void setListData(List<AppGroupBean> groups){
        mGroups.clear();
        mGroups.addAll(groups);
        mCheckedCount = groups.get(0).getchildrenSize();//默认用户应用都选中
        notifyDataSetChanged();
    }

    public void setOnItemChosenListener(@NonNull OnItemChosenListener itemChosnListener) {
        this.mOnItemChosenListener = itemChosnListener;
    }

    interface OnItemChosenListener {
        void onItemChosen(int chosenCount);
    }
}
