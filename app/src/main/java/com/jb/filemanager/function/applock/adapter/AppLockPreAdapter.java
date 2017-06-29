package com.jb.filemanager.function.applock.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.applock.model.bean.LockerItem;

import java.util.List;

/**
 * Created by nieyh on 2016/12/30.
 */

public class AppLockPreAdapter extends BaseAdapter {

    private List<LockerItem> mLockerBeans = null;

    public AppLockPreAdapter(List<LockerItem> lockerItemList) {
        mLockerBeans = lockerItemList;
    }

    @Override
    public int getCount() {
        if (mLockerBeans != null) {
            return mLockerBeans.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mLockerBeans != null && mLockerBeans.size() > position && position >= 0) {
            return mLockerBeans.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ItemViewViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_applock_item, parent, false);
            holder = new ItemViewViewHolder();
            holder.mAppLockItemIcon = (ImageView) convertView.findViewById(R.id.activity_applock_item_icon);
            holder.mAppLockItemAppName = (TextView) convertView.findViewById(R.id.activity_applock_item_appname);
            holder.mAppLockItemGroupSelectBox = (GroupSelectBox) convertView.findViewById(R.id.activity_applock_item_selectbox);
            holder.mAppLockItemGroupSelectBox.setImageSource(R.drawable.choose_none,
                    R.drawable.choose_part, R.drawable.choose_all);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewViewHolder) convertView.getTag();
        }
        holder.mAppLockItemData = (LockerItem) getItem(position);
        holder.mAppLockItemAppName.setText(holder.mAppLockItemData.getTitle());
        Drawable icon = holder.mAppLockItemData.getIcon();
        if (icon != null) {
            holder.mAppLockItemIcon.setImageDrawable(icon);
        }
        if (holder.mAppLockItemData.isChecked) {
            holder.mAppLockItemGroupSelectBox.setState(GroupSelectBox.SelectState.ALL_SELECTED);
        } else {
            holder.mAppLockItemGroupSelectBox.setState(GroupSelectBox.SelectState.NONE_SELECTED);
        }
        return convertView;
    }

    /**
     * ItemView的Viewolder
     */
    class ItemViewViewHolder {
        public ImageView mAppLockItemIcon;
        public TextView mAppLockItemAppName;
        public GroupSelectBox mAppLockItemGroupSelectBox;
        public LockerItem mAppLockItemData;
    }

    /**
     * 绑定数据
     */
    public void bindData(List<LockerItem> lockerItemList) {
        mLockerBeans = lockerItemList;
        notifyDataSetChanged();
    }

    /**
     * 列表点击
     */
    public void performItemClick(int pos) {
        if (mLockerBeans != null && mLockerBeans.size() > pos) {
            LockerItem lockerItem = mLockerBeans.get(pos);
            lockerItem.isChecked = !lockerItem.isChecked;
            notifyDataSetChanged();
        }
    }

}