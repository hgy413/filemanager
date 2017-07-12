package com.jb.filemanager.function.zipfile.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.zipfile.bean.ZipFileGroupBean;
import com.jb.filemanager.function.zipfile.bean.ZipFileItemBean;
import com.jb.filemanager.function.zipfile.listener.ZipListAdapterClickListener;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.TimeUtil;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/4 15:45.
 */

public class ZipListAdapter extends BaseExpandableListAdapter {

    private List<ZipFileGroupBean> mGroupList;
    private ZipListAdapterClickListener mListener;

    public ZipListAdapter(List<ZipFileGroupBean> data) {
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
        final ZipFileGroupBean group = getGroup(groupPosition);
        holder.groupTime.setText(group.getGroupTimeStr());
        int selectedState = group.getSelectedState();
        if (selectedState == 1) {
            holder.groupCheckBox.setImageResource(R.drawable.select_none);
        } else if (selectedState == 2) {
            holder.groupCheckBox.setImageResource(R.drawable.select_multi);
        } else {
            holder.groupCheckBox.setImageResource(R.drawable.select_all);
        }
        holder.groupCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.switchGroupState();
                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.onSwitchClick();
                }
            }
        });
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
            holder.divider = convertView.findViewById(R.id.item_zip_divider);
            holder.wideDivider = convertView.findViewById(R.id.item_zip_wide_divider);
            convertView.setTag(R.layout.item_zip_file, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.layout.item_zip_file);
        }
        final ZipFileItemBean child = getChild(groupPosition, childPosition);
        holder.icon.setImageResource(R.drawable.zip_icon);
        holder.name.setText(child.getFileName());
        holder.size.setText(ConvertUtils.formatFileSize(child.getFileSize()) + " " + TimeUtil.getTime(child.getLastModifiedTime()));
        holder.itemCheckBox.setImageResource(child.isSelected() ? R.drawable.select_all : R.drawable.select_none);
        holder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                child.setSelected(!child.isSelected());
                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.onSwitchClick();
                }
            }
        });
        // 最后一个子项
        if (childPosition == getChildrenCount(groupPosition) - 1 && groupPosition != getGroupCount() - 1) {
            holder.divider.setVisibility(View.GONE);
            holder.wideDivider.setVisibility(View.VISIBLE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
            holder.wideDivider.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
        View divider;
        View wideDivider;
    }

    public void setListener(ZipListAdapterClickListener listener) {
        mListener = listener;
    }
}