package com.jb.filemanager.function.applock.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.model.bean.AppLockGroupData;
import com.jb.filemanager.function.applock.model.bean.LockerItem;
import com.jb.filemanager.ui.widget.GroupSelectBox;

import java.util.List;


/**
 * Created by wangying on 15/12/7.
 */
public class AppLockAdapter extends AbsAdapter<AppLockGroupData> {

    public AppLockAdapter(List<AppLockGroupData> groups) {
        super(groups);
    }

    @Override
    public View onGetGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewViewHolder groupViewViewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applock_group, parent, false);
            groupViewViewHolder = new GroupViewViewHolder();
            groupViewViewHolder.mGroupTextView = (TextView) convertView.findViewById(R.id.activity_applock_group_title);
            groupViewViewHolder.mGroupSelectBox = (GroupSelectBox) convertView.findViewById(R.id.activity_applock_group_selectbox);
            groupViewViewHolder.mGroupSelectBox.setImageSource(R.drawable.app_lock_lock_off,
                    R.drawable.app_lock_lock_on, R.drawable.app_lock_lock_on);
            convertView.setTag(groupViewViewHolder);
        } else {
            groupViewViewHolder = (GroupViewViewHolder) convertView.getTag();
        }

        // 二级列表自动展开
        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);

        AppLockGroupData appLockGroupData = mGroups.get(groupPosition);
        groupViewViewHolder.mGroupTextView.setText(appLockGroupData.getLockerGroupTitle());
        if (appLockGroupData.isAllChecked()) {
            groupViewViewHolder.mGroupSelectBox.setState(GroupSelectBox.SelectState.ALL_SELECTED);
        } else {
            groupViewViewHolder.mGroupSelectBox.setState(GroupSelectBox.SelectState.NONE_SELECTED);
        }
        if (appLockGroupData.getChildren().isEmpty()) {
            convertView.setVisibility(View.GONE);
        } else {
            convertView.setVisibility(View.VISIBLE);
        }
        convertView.setBackgroundColor(Color.WHITE);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public View onGetChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final ItemViewViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_applock_item, parent, false);
            holder = new ItemViewViewHolder();
            holder.mAppLockItemIcon = (ImageView) convertView.findViewById(R.id.activity_applock_item_icon);
            holder.mAppLockItemAppName = (TextView) convertView.findViewById(R.id.activity_applock_item_appname);
            holder.mAppLockItemGroupSelectBox = (GroupSelectBox) convertView.findViewById(R.id.activity_applock_item_selectbox);
            holder.mAppLockItemGroupSelectBox.setImageSource(R.drawable.app_lock_lock_off,
                    R.drawable.app_lock_lock_on, R.drawable.app_lock_lock_on);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewViewHolder) convertView.getTag();
        }
        LockerItem lockerItem = mGroups.get(groupPosition).getChild(childPosition);
        holder.mAppLockItemAppName.setText(lockerItem.getTitle());
        Drawable icon = lockerItem.getIcon();
        if (icon != null) {
            holder.mAppLockItemIcon.setImageDrawable(icon);
        }
        if (lockerItem.isChecked) {
            holder.mAppLockItemGroupSelectBox.setState(GroupSelectBox.SelectState.ALL_SELECTED);
        } else {
            holder.mAppLockItemGroupSelectBox.setState(GroupSelectBox.SelectState.NONE_SELECTED);
        }
//        convertView.setBackgroundResource(R.drawable.selector_list_item);
        return convertView;
    }

    /**
     * Group ItemViewH的ViewHolder
     */
    class GroupViewViewHolder {
        public TextView mGroupTextView;
        public GroupSelectBox mGroupSelectBox;
    }

    /**
     * ItemView的Viewolder
     */
    class ItemViewViewHolder {
        public ImageView mAppLockItemIcon;
        public TextView mAppLockItemAppName;
        public GroupSelectBox mAppLockItemGroupSelectBox;

    }

    /**
     * 重新绑定数据
     */
    public void bindData(List<AppLockGroupData> groups) {
        mGroups = groups;
        notifyDataSetChanged();
    }

    /**
     * 群组点击
     */
    public void performGroupClick(int groupPos) {
        if (mGroups != null) {
            AppLockGroupData appLockGroupData = mGroups.get(groupPos);
            appLockGroupData.setAllChecked(!appLockGroupData.isAllChecked());
            for (LockerItem lockerItem : appLockGroupData.getChildren()) {
                lockerItem.isChecked = appLockGroupData.isAllChecked();
            }
            if (appLockGroupData.isAllChecked()) {
                Toast.makeText(TheApplication.getAppContext(), TheApplication.getAppContext().getString(R.string.app_lock_app_all_lock_tip), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TheApplication.getAppContext(), TheApplication.getAppContext().getString(R.string.app_lock_app_all_unlock_tip
                ), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 单项点击
     */
    public void performItemClick(int groupPos, int childPos) {
        if (mGroups != null) {
            List<LockerItem> lockerItems = mGroups.get(groupPos).getChildren();
            lockerItems.get(childPos).isChecked =
                    !mGroups.get(groupPos).getChild(childPos).isChecked;
            //提示用户
            chgAppInfo(lockerItems.get(childPos).getTitle(), lockerItems.get(childPos).isChecked);
            boolean isAllLocked = true;
            for (int i = 0; i < lockerItems.size(); i++) {
                if (!lockerItems.get(i).isChecked) {
                    isAllLocked = false;
                    break;
                }
            }
            mGroups.get(groupPos).setAllChecked(isAllLocked);
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
}
