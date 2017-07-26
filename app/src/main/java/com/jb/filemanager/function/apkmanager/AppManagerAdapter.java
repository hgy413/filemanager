package com.jb.filemanager.function.apkmanager;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.applock.adapter.AbsAdapter;
import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.function.trash.adapter.ItemCheckBox;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.imageloader.IconLoader;

import java.util.Iterator;
import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/6/29 15:23
 */

class AppManagerAdapter extends AbsAdapter<AppGroupBean> {

    private boolean[] mGroupExpandState = {true, false};//默认是user打开   system关闭
    private static final String TAG = "mCheckedCount";
    private int mCheckedCount;
    private OnItemChosenListener mOnItemChosenListener;

    AppManagerAdapter(List<AppGroupBean> groups) {
        super(groups);
//        mCheckedCount = groups.get(0).getchildrenSize();//默认用户应用都选中
    }

    @Override
    public View onGetGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applock_group, parent, false);
        GroupItemViewHolder viewHolder = new GroupItemViewHolder(convertView);
        final AppGroupBean appGroupBean = mGroups.get(groupPosition);
        viewHolder.mTvGroupTitle.setText(appGroupBean.mGroupTitle);
        viewHolder.mTvGroupTitleItemCount.setText(TheApplication.getAppContext().getString(R.string.item_count, appGroupBean.getchildrenSize()));
        if (appGroupBean.mAppType == AppGroupBean.SYSTEM_APP) {//系统app不显示选择框
            viewHolder.mSelectBox.setVisibility(View.GONE);
            return convertView;
        }
        viewHolder.mSelectBox
                .setImageSource(R.drawable.choose_none,
                        R.drawable.choose_part,
                        R.drawable.choose_all);

        viewHolder.mSelectBox.setState(appGroupBean.mSelectState);
        viewHolder.mSelectBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statisticsGroupSelectBoxClick();
                if (appGroupBean.mSelectState == GroupSelectBox.SelectState.ALL_SELECTED) {
                    appGroupBean.mSelectState = GroupSelectBox.SelectState.NONE_SELECTED;
                    for (AppItemInfo childBean : appGroupBean.getChildren()) {
                        childBean.mIsChecked = false;
                    }
                } else {
                    appGroupBean.mSelectState = GroupSelectBox.SelectState.ALL_SELECTED;
                    for (AppItemInfo childBean : appGroupBean.getChildren()) {
                        childBean.mIsChecked = true;
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
        /*boolean isTheLastOne = true;
        for (int i = groupPosition; i < getGroupCount(); i++) {
            isTheLastOne = isTheLastOne && !mGroupExpandState[i];//下面只要有一个展开的  就不是最后一个
        }*/
        if (isLastChild) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_child_with_bottom_space_10, parent, false);
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_child, parent, false);
        }

        ChildItemViewHolder viewHolder = new ChildItemViewHolder(convertView);
        final AppGroupBean appGroupBean = mGroups.get(groupPosition);
        final AppItemInfo child = appGroupBean.getChild(childPosition);
        IconLoader.getInstance().displayImage(child.mAppPackageName, viewHolder.mIvAppIcon, R.drawable.app_icon_default);
        viewHolder.mTvAppName.setText(child.mAppName);
        viewHolder.mTvAppSize.setText(ConvertUtils.formatFileSize(child.mAppCodeSize));
        if (appGroupBean.mAppType == AppGroupBean.SYSTEM_APP) {//系统app不显示选择框
            viewHolder.mItemCheckBox.setVisibility(View.GONE);
            return convertView;
        }
        viewHolder.mItemCheckBox.setImageRes(R.drawable.choose_none,
                R.drawable.choose_all);
        viewHolder.mItemCheckBox.setChecked(child.mIsChecked);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statisticsItemSelectBoxClick();
                child.mIsChecked = !child.mIsChecked;
                updateGroupState(appGroupBean);
            }
        });
        /*viewHolder.mItemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                child.mIsChecked = !child.mIsChecked;
                updateGroupState(appGroupBean);
            }
        });*/
        return convertView;
    }

    private void updateGroupState(AppGroupBean appGroupBean) {
        boolean isAllSelect = true;
        boolean isPartSelect = false;
        for (AppItemInfo childBean : appGroupBean.getChildren()) {
            isAllSelect = isAllSelect && childBean.mIsChecked;
            isPartSelect = isPartSelect || childBean.mIsChecked;
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
            List<AppItemInfo> children = groupBean.getChildren();
            for (AppItemInfo childBean : children) {
                if (childBean.mIsChecked) {
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

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        if (groupPosition < mGroupExpandState.length) {
            mGroupExpandState[groupPosition] = false;
        }
        statisticsCollapseGroup();
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        if (groupPosition < mGroupExpandState.length) {
            mGroupExpandState[groupPosition] = true;
        }
    }

    public boolean isTargetGroupExpand(int position) {
        return mGroupExpandState[position];
    }

    private static class GroupItemViewHolder {
        View mView;
        TextView mTvGroupTitle;
        TextView mTvGroupTitleItemCount;
        GroupSelectBox mSelectBox;

        GroupItemViewHolder(View convertView) {
            mView = convertView;
            mSelectBox = (GroupSelectBox) convertView.findViewById(R.id.activity_applock_group_selectbox);
            mTvGroupTitle = (TextView) convertView.findViewById(R.id.activity_applock_group_title);
            mTvGroupTitleItemCount = (TextView) convertView.findViewById(R.id.activity_applock_group_title_item_count);
        }
    }

    private static class ChildItemViewHolder {
        View mView;
        ImageView mIvAppIcon;
        TextView mTvAppName;
        TextView mTvAppSize;
        ItemCheckBox mItemCheckBox;

        ChildItemViewHolder(View convertView) {
            mIvAppIcon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            mTvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
            mTvAppSize = (TextView) convertView.findViewById(R.id.tv_app_size);
            mItemCheckBox = (ItemCheckBox) convertView.findViewById(R.id.icb_child_check);
            mView = convertView;
        }
    }

    public void setListData(List<AppGroupBean> groups){
        PackageManager packageManager = TheApplication.getAppContext().getPackageManager();//获取packagemanager
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息

        Iterator<AppItemInfo> iterator = groups.get(0).getChildren().iterator();
        while (iterator.hasNext()) {
            AppItemInfo next = iterator.next();
            boolean isAppStillInstall = false;
            for (PackageInfo packageInfo : packageInfoList) {
                if (packageInfo.packageName.equals(next.getAppPackageName())) {
                    isAppStillInstall = true;
                    break;
                }
            }

            if (!isAppStillInstall) {
                iterator.remove();
                Logger.d(TAG, next.getAppName() + "已卸载");
            }
        }

        mGroups.clear();
        mGroups.addAll(groups);
//        mCheckedCount = groups.get(0).getchildrenSize();//默认用户应用都选中
        notifyDataSetChanged();
    }

    public void setOnItemChosenListener(@NonNull OnItemChosenListener itemChosnListener) {
        this.mOnItemChosenListener = itemChosnListener;
    }

    interface OnItemChosenListener {
        void onItemChosen(int chosenCount);
    }
    //====================统计代码  Start =====================
    private void statisticsCollapseGroup() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.APP_CLICK_COLLAPSE_GROUP;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "app 点击折叠分类---" + bean.mOperateId);
    }

    private void statisticsGroupSelectBoxClick() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.APP_CLICK_GROUP_SELECT_BOX;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "app 点击组的选择框---" + bean.mOperateId);
    }

    private void statisticsItemSelectBoxClick() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.APP_CLICK_ITEM_SELECT_BOX;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "app 点击条目选择框---" + bean.mOperateId);
    }
    //====================统计代码  end =====================
}
