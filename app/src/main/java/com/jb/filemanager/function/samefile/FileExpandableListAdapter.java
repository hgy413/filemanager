package com.jb.filemanager.function.samefile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.image.adapter.ImageExpandableAdapter;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.TimeUtil;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.util.ArrayList;

/**
 * Created by boole on 17-7-13.
 *
 */

public class FileExpandableListAdapter extends BaseExpandableListAdapter implements View.OnClickListener{
    GroupList<String, FileInfo> mGroupList;
    @Override
    public int getGroupCount() {
        return mGroupList == null ? 0 : mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList childrenList = mGroupList.valueAt(groupPosition);
        return childrenList == null ? 0 : childrenList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupList == null ? null : mGroupList.valueAt(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroupList.valueAt(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mGroupList.valueAt(groupPosition).hashCode();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mGroupList.valueAt(groupPosition).get(childPosition).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (groupPosition == 0) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_samefile_group, parent, false);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_samefile_group_divider, parent, false);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.mTvTitle.setText(mGroupList.keyAt(groupPosition));
        groupViewHolder.mIvSelect.setTag(groupPosition);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_samefile_child, parent, false);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        FileInfo fileInfo = mGroupList.valueAt(groupPosition).get(childPosition);
        Const.FILE_TYPE fileType = mGroupList.valueAt(groupPosition).get(childPosition).mFileType;

        switch (fileType) {
            case APP:
                holder.mIvIcon.setImageResource(R.drawable.app_icon);
                break;
            case DOC:
                holder.mIvIcon.setImageResource(R.drawable.doc_icon);
                break;
            case PDF:
                holder.mIvIcon.setImageResource(R.drawable.img_pdf);
                break;
            case TXT:
                holder.mIvIcon.setImageResource(R.drawable.img_txt);
                break;
            case MUSIC:
                holder.mIvIcon.setImageResource(R.drawable.img_music);
                break;
            case VIDEO:
                holder.mIvIcon.setImageResource(R.drawable.video_icon);
                break;
            case PICTURE:
                holder.mIvIcon.setImageResource(R.drawable.img_picture);
                break;
            case ZIP:
                holder.mIvIcon.setImageResource(R.drawable.zip_icon);
                break;
            case OTHER:
            default:
                holder.mIvIcon.setImageResource(R.drawable.unknown_icon);
        }
        holder.mTvName.setText(fileInfo.mName);
        holder.mTvInfo.setText(fileInfo.mArtist + "  " +
                ConvertUtils.getReadableSize(fileInfo.mSize) + "  " +
                TimeUtil.getMSTime(fileInfo.mDuration));
        holder.savePosition(groupPosition, childPosition, fileInfo);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public void reflaceDate(GroupList groupList) {
        mGroupList = groupList;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_file_group_item_select:
                ImageView ivSelect = (ImageView)v;
                ivSelect.setImageResource(R.drawable.choose_all);
                updateGroupView((int)v.getTag());
                break;
            case R.id.ll_file_item_container:
                // Todo Show file by Type
                break;
            case R.id.iv_music_child_item_select:
                Binder binder = (Binder) v.getTag();
                if (binder.mFileInfo.isSelected) {
                    binder.mFileInfo.isSelected = false;
                    ((ImageView)v).setImageResource(R.drawable.choose_none);
                } else {
                    binder.mFileInfo.isSelected = true;
                    ((ImageView)v).setImageResource(R.drawable.choose_all);
                }
                updateGroupView(binder.groupPos);
                break;
        }
    }

    private void updateGroupView(int groupPosition) {

    }


    /**
     * Group Item View
     */
    private class GroupViewHolder {
        // Title
        private TextView mTvTitle;
        // Select icon
        private ImageView mIvSelect;

        public GroupViewHolder(View itemView) {
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_file_group_item_title);
            mIvSelect = (ImageView) itemView.findViewById(R.id.iv_file_group_item_select);
            mIvSelect.setOnClickListener(FileExpandableListAdapter.this);
        }
    }

    /**
     * Child Item View
     */
    private class ItemViewHolder {
        LinearLayout mLlItemContainer;
        ImageView mIvIcon;
        TextView mTvName;
        TextView mTvInfo;
        ImageView mIvSelect;
        public ItemViewHolder(View itemView) {
            mLlItemContainer = (LinearLayout)itemView.findViewById(R.id.ll_file_item_container);
            mIvIcon = (ImageView) itemView.findViewById(R.id.iv_music_child_item_cover);
            mTvName = (TextView) itemView.findViewById(R.id.tv_music_child_item_name);
            mTvInfo = (TextView) itemView.findViewById(R.id.tv_music_child_item_info);
            mIvSelect = (ImageView) itemView.findViewById(R.id.iv_music_child_item_select);
            mIvSelect.setOnClickListener(FileExpandableListAdapter.this);
        }

        void savePosition(int group, int child, FileInfo fileInfo) {
            if (fileInfo != null) {
                Binder binder = new Binder(group, child, fileInfo);
                //更新数据 用于直接修改
                mLlItemContainer.setTag(binder);
                mIvSelect.setTag(binder);
                mIvSelect.setOnClickListener(FileExpandableListAdapter.this);
                mIvSelect.setOnClickListener(FileExpandableListAdapter.this);
            }
        }
    }

    private class Binder {
        public int groupPos;
        public int childPos;
        public FileInfo mFileInfo;

        public Binder(int groupPos, int childPos, FileInfo info) {
            this.groupPos = groupPos;
            this.childPos = childPos;
            this.mFileInfo = info;
        }
    }
}
