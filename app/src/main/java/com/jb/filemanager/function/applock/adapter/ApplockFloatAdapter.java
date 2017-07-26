package com.jb.filemanager.function.applock.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.applock.model.bean.AppLockGroupData;
import com.jb.filemanager.function.applock.model.bean.LockerItem;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.util.imageloader.IconLoader;

import java.util.List;


/**
 * Created by wangying on 15/12/7.
 */
public class ApplockFloatAdapter extends AbsAdapter<AppLockGroupData> {

    private int mWidthSpliterHeight;
    private int mNarrowSpliterHeight;
    private int mMarginLeft;

    public ApplockFloatAdapter(List<AppLockGroupData> groups) {
        super(groups);
        mWidthSpliterHeight = DrawUtils.dip2px(10);
        mNarrowSpliterHeight = DrawUtils.dip2px(1);
        mMarginLeft = DrawUtils.dip2px(68);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).getChild(childPosition).hashCode();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mGroups.get(groupPosition).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getGroupCount() {
        if (mGroups == null) {
            return 0;
        }
        return super.getGroupCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mGroups.get(groupPosition) == null) {
            return 0;
        }
        return super.getChildrenCount(groupPosition);
    }


    @Override
    public View onGetGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applock_float_view_group, parent, false);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        AppLockGroupData appLockGroupData = mGroups.get(groupPosition);
        groupViewHolder.mTitle.setText(appLockGroupData.getLockerGroupTitle());
        if (isExpanded) {
            groupViewHolder.mArrow.setRotation(180);
        } else {
            groupViewHolder.mArrow.setRotation(0);
        }
        convertView.setBackgroundResource(R.drawable.common_item_selector);
        return convertView;
    }

    @Override
    public View onGetChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemViewHolder itemViewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applock_float_view, parent, false);
            itemViewHolder = new ItemViewHolder(convertView);
            convertView.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }

        LockerItem lockerItem = mGroups.get(groupPosition).getChild(childPosition);
        IconLoader.getInstance().displayImage(lockerItem.getPackageName(), itemViewHolder.mAppLockItemIcon);

        itemViewHolder.mAppLockItemAppName.setText(lockerItem.getTitle());
        if (lockerItem.isChecked) {
            itemViewHolder.mAppLockItemGroupSelectBox.setState(GroupSelectBox.SelectState.ALL_SELECTED);
        } else {
            itemViewHolder.mAppLockItemGroupSelectBox.setState(GroupSelectBox.SelectState.NONE_SELECTED);
        }

        itemViewHolder.mContentView.setBackgroundResource(R.drawable.common_item_selector);
        if (isLastChild) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) itemViewHolder.mSpliter.getLayoutParams();
            layoutParams.height = mWidthSpliterHeight;
            layoutParams.leftMargin = 0;
            itemViewHolder.mSpliter.setLayoutParams(layoutParams);
        } else {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) itemViewHolder.mSpliter.getLayoutParams();
            layoutParams.height = mNarrowSpliterHeight;
            layoutParams.leftMargin = mMarginLeft;
            itemViewHolder.mSpliter.setLayoutParams(layoutParams);
        }
        return convertView;
    }

    /**
     * 重新绑定数据
     */
    public void bindData(List<AppLockGroupData> groups) {
        mGroups = groups;
        notifyDataSetChanged();
    }

    /**
     * 列表点击
     */
    public void performItemClick(int group, int child) {
        if (mGroups != null && mGroups.size() > group && mGroups.get(group).getChildren().size() > child) {
            LockerItem lockerItem = mGroups.get(group).getChild(child);
            lockerItem.isChecked = !lockerItem.isChecked;
            if (lockerItem.isChecked) {
                switch (group) {
                    case 0:
                        StatisticsTools.upload(StatisticsConstants.APPLOCK_RE_LOCK);
                        break;
                    case 1:
                        StatisticsTools.upload(StatisticsConstants.APPLOCK_OTHER_LOCK);
                        break;
                }
            } else {
                switch (group) {
                    case 0:
                        StatisticsTools.upload(StatisticsConstants.APPLOCK_RE_UNLOCK);
                        break;
                    case 1:
                        StatisticsTools.upload(StatisticsConstants.APPLOCK_OTHER_UNLOCK);
                        break;
                }
            }
            chgAppInfo(lockerItem.getTitle(), lockerItem.isChecked);
            notifyDataSetChanged();
        }
    }

    /**
     * 修改应用信息
     */
    private void chgAppInfo(final String title, final boolean isLock) {
        TheApplication.postRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isLock) {
                    Toast.makeText(TheApplication.getAppContext(), TheApplication.getAppContext().getString(R.string.app_lock_app_lock_tip,
                            title), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TheApplication.getAppContext(), TheApplication.getAppContext().getString(R.string.app_lock_app_unlock_tip,
                            title), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //ItemView的Viewolder
    class ItemViewHolder {
        public ImageView mAppLockItemIcon;
        public TextView mAppLockItemAppName;
        public GroupSelectBox mAppLockItemGroupSelectBox;
        public View mContentView;
        public View mSpliter;

        ItemViewHolder(View parent) {
            mAppLockItemIcon = (ImageView) parent.findViewById(R.id.item_applock_float_view_icon);
            mAppLockItemAppName = (TextView) parent.findViewById(R.id.item_applock_float_view_appname);
            mContentView = parent.findViewById(R.id.item_applock_float_view_content);
            mSpliter = parent.findViewById(R.id.item_applock_float_view_bottom);
            mAppLockItemGroupSelectBox = (GroupSelectBox) parent.findViewById(R.id.item_applock_float_view_selectbox);
            mAppLockItemGroupSelectBox.setImageSource(R.drawable.app_lock_lock_off, R.drawable.app_lock_lock_on, R.drawable.app_lock_lock_on);
        }
    }

    //GroupViewHolder
    class GroupViewHolder {
        public TextView mTitle;
        public ImageView mArrow;

        GroupViewHolder(View parent) {
            mTitle = (TextView) parent.findViewById(R.id.item_applock_float_view_group_title);
            mArrow = (ImageView) parent.findViewById(R.id.item_applock_float_view_group_arrow);
        }
    }

}
