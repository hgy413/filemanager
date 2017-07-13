package com.jb.filemanager.function.samefile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.TimeUtil;

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
                holder.mIvCover.setImageResource(R.drawable.app_icon);
                break;
            case DOC:
                holder.mIvCover.setImageResource(R.drawable.doc_icon);
                break;
            case PDF:
                holder.mIvCover.setImageResource(R.drawable.img_pdf);
                break;
            case TXT:
                holder.mIvCover.setImageResource(R.drawable.img_txt);
                break;
            case MUSIC:
                holder.mIvCover.setImageResource(R.drawable.img_music);
                break;
            case VIDEO:
                holder.mIvCover.setImageResource(R.drawable.video_icon);
                break;
            case PICTURE:
                holder.mIvCover.setImageResource(R.drawable.img_picture);
                break;
            case ZIP:
                holder.mIvCover.setImageResource(R.drawable.zip_icon);
                break;
            case OTHER:
            default:
                holder.mIvCover.setImageResource(R.drawable.unknown_icon);

        }
        holder.mTvName.setText(fileInfo.mName);
        holder.mTvInfo.setText(fileInfo.mArtist + "  " +
                ConvertUtils.getReadableSize(fileInfo.mSize) + "  " +
                TimeUtil.getMSTime(fileInfo.mDuration));
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
                break;
            case R.id.iv_music_child_item_select:

                break;
        }
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

        ImageView mIvCover;
        TextView mTvName;
        TextView mTvInfo;
        ImageView mIvSelect;

        public ItemViewHolder(View itemView) {
            mIvCover = (ImageView) itemView.findViewById(R.id.iv_music_child_item_cover);
            mTvName = (TextView) itemView.findViewById(R.id.tv_music_child_item_name);
            mTvInfo = (TextView) itemView.findViewById(R.id.tv_music_child_item_info);
            mIvSelect = (ImageView) itemView.findViewById(R.id.iv_music_child_item_select);
            mIvSelect.setOnClickListener(FileExpandableListAdapter.this);
        }
    }

    private class Binder {
        public int groupPos;
        public int childPos;
        public FileInfo mImageModle;

        public Binder(int groupPos, int childPos, FileInfo imageModle) {
            this.groupPos = groupPos;
            this.childPos = childPos;
            this.mImageModle = imageModle;
        }
    }
}
