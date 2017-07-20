package com.jb.filemanager.function.samefile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.TimeUtil;
import com.jb.filemanager.util.images.ImageFetcher;
import com.jb.filemanager.util.images.ImageUtils;
import java.util.ArrayList;

/**
 * Created by boole on 17-7-13.
 *
 */

public class FileExpandableListAdapter extends BaseExpandableListAdapter implements View.OnClickListener{
    GroupList<String, FileInfo> mGroupList;
    private ItemChooseChangeListener mChooseChangeListener;
    private ImageFetcher mImageFetcher;
    private Context mContext;
    public FileExpandableListAdapter(Context context, @NonNull ItemChooseChangeListener chooseChangeListener) {
        mChooseChangeListener = chooseChangeListener;
        mContext = context;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int mImageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, dm);
        mImageFetcher = ImageUtils.createImageFetcher((SameFileActivity)context, mImageSize, R.drawable.img_picture);
    }

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
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_samefile_group, parent, false);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder)convertView.getTag();
        }
        groupViewHolder.mTvTitle.setText(mGroupList.keyAt(groupPosition) +
                "(" +mGroupList.valueAt(groupPosition).size() + ")");
        groupViewHolder.mIvSelect.setState(getGroupSelectState(groupPosition));
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
        if (0 == childPosition) {
            holder.mLlItemContainer.setBackgroundResource(R.drawable.bg_selector_samefile_list_item_with_top_line);
        } else {
            holder.mLlItemContainer.setBackgroundResource(R.drawable.bg_selector_samefile_list_item);
        }
        // Set divide line and group divide space
        if (getChildrenCount(groupPosition) -1 == childPosition) {
            holder.mViewChildDebideLine.setVisibility(View.GONE);
            holder.mViewGroupDevideSpace.setVisibility(View.VISIBLE);
        } else {
            holder.mViewChildDebideLine.setVisibility(View.VISIBLE);
            holder.mViewGroupDevideSpace.setVisibility(View.GONE);
        }
        FileInfo fileInfo = mGroupList.valueAt(groupPosition).get(childPosition);
        Const.FILE_TYPE fileType = mGroupList.valueAt(groupPosition).get(childPosition).mFileType;
        // Set Icon
        switch (fileType) {
            case APP:
                holder.mIvIcon.setImageResource(R.drawable.app_icon);
                holder.mIvIcon.setImageDrawable(PackageManagerLocker.getInstance()
                        .getApplicationIconByPath(fileInfo.mFullPath, 120, 120));
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
                mImageFetcher.loadImage(fileInfo.mFullPath, holder.mIvIcon);
                break;
            case PICTURE:
                holder.mIvIcon.setImageResource(R.drawable.img_picture);
                mImageFetcher.loadImage(fileInfo.mFullPath, holder.mIvIcon);
                break;
            case ZIP:
                holder.mIvIcon.setImageResource(R.drawable.zip_icon);
                break;
            case OTHER:
            default:
                holder.mIvIcon.setImageResource(R.drawable.unknown_icon);
        }
        if (fileInfo.isSelected) {
            holder.mIvSelect.setImageResource(R.drawable.choose_all);
        } else {
            holder.mIvSelect.setImageResource(R.drawable.choose_none);
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
                int group = (int)ivSelect.getTag();
                boolean selectResoult = true;
                if (GroupSelectBox.SelectState.ALL_SELECTED == getGroupSelectState(group)) {
                    selectResoult = false;
                }
                for ( FileInfo info : mGroupList.valueAt(group)) {
                    info.isSelected = selectResoult;
                }
                notifyDataSetChanged();
                break;
//            case R.id.ll_file_item_container:
//                // Todo handle file by Type
//                Toast.makeText(mContext, "dsfasd", Toast.LENGTH_LONG);
//
//                break;
            case R.id.iv_music_child_item_select:
                Binder binder = (Binder) v.getTag();
                if (binder.mFileInfo.isSelected) {
                    binder.mFileInfo.isSelected = false;
                    ((ImageView)v).setImageResource(R.drawable.choose_none);
                } else {
                    binder.mFileInfo.isSelected = true;
                    ((ImageView)v).setImageResource(R.drawable.choose_all);
                }
                notifyDataSetChanged();
                break;
        }
        mChooseChangeListener.onChooseNumChanged(getSelectCount());
    }

    /**
     * Group Item View
     */
    private class GroupViewHolder {
        // Title
        private TextView mTvTitle;
        // Select icon
        private GroupSelectBox mIvSelect;

        public GroupViewHolder(View itemView) {
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_file_group_item_title);
            mIvSelect = (GroupSelectBox) itemView.findViewById(R.id.iv_file_group_item_select);
            mIvSelect.setImageSource(R.drawable.choose_none, R.drawable.choose_part, R.drawable.choose_all);
            mIvSelect.setOnClickListener(FileExpandableListAdapter.this);
        }
    }

    /**
     * Child Item View
     */
    private class ItemViewHolder {
        RelativeLayout mLlItemContainer; // Have set tag for ItemViewHolder, can't set tag fot other.
        //LinearLayout mLlItemDivideLine;
        ImageView mIvIcon;
        TextView mTvName;
        TextView mTvInfo;
        ImageView mIvSelect;
        View mViewChildDebideLine;
        View mViewGroupDevideSpace;
        public ItemViewHolder(View itemView) {
            mLlItemContainer = (RelativeLayout)itemView.findViewById(R.id.ll_file_item_container);
            mIvIcon = (ImageView) itemView.findViewById(R.id.iv_music_child_item_cover);
            mTvName = (TextView) itemView.findViewById(R.id.tv_music_child_item_name);
            mTvInfo = (TextView) itemView.findViewById(R.id.tv_music_child_item_info);
            mIvSelect = (ImageView) itemView.findViewById(R.id.iv_music_child_item_select);
            mViewChildDebideLine = (View) itemView.findViewById(R.id.view_child_divide_line);
            mViewGroupDevideSpace = (View) itemView.findViewById(R.id.view_group_divide_space);
            mIvSelect.setOnClickListener(FileExpandableListAdapter.this);
            //mLlItemContainer.setOnClickListener(FileExpandableListAdapter.this);
        }

        void savePosition(int group, int child, FileInfo fileInfo) {
            if (fileInfo != null) {
                Binder binder = new Binder(group, child, fileInfo);
                //更新数据 用于直接修改
                mIvSelect.setTag(binder);
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

    public GroupSelectBox.SelectState getGroupSelectState(int group) {
        GroupSelectBox.SelectState selectState = GroupSelectBox.SelectState.NONE_SELECTED;
        if (0 <= group && group <= mGroupList.size()) {
            int selectCount = 0;
            for (FileInfo info : mGroupList.valueAt(group)) {
                if (info.isSelected) selectCount++;
            }

            if (selectCount == mGroupList.valueAt(group).size()) {
                selectState = GroupSelectBox.SelectState.ALL_SELECTED;
            } else if (selectCount > 0) {
                selectState = GroupSelectBox.SelectState.MULT_SELECTED;
            }
        }
        return selectState;
    }

    public int getSelectCount() {
        int count = 0;
        if (mGroupList == null) return count;
        for (int i = 0; i < mGroupList.size(); i++) {
            for (FileInfo info : mGroupList.valueAt(i)) {
                if (info.isSelected) count++;
            }
        }
        return count;
    }

    interface ItemChooseChangeListener {
        public void onChooseNumChanged(int num);
    }
}
