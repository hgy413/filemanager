package com.jb.filemanager.function.trash.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.commomview.ProgressWheel;
import com.jb.filemanager.function.scanframe.bean.CleanGroupsBean;
import com.jb.filemanager.util.ConvertUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/12 14:24
 */

public class TrashGroupAdapter extends RecyclerView.Adapter<TrashGroupAdapter.GroupHolder> {

    private List<CleanGroupsBean> mDataGroup;
    private List<Long> mDataGroupSize;
    private OnItemRemoveListener mOnItemRemoveListener;

    public TrashGroupAdapter(List<CleanGroupsBean> dataGroup, List<Long> groupSize) {
        mDataGroup = dataGroup;
        mDataGroupSize = groupSize;
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clean_trash_group, parent, false);
        return new GroupHolder(inflate);
    }

    @Override
    public void onBindViewHolder(GroupHolder holder, int position) {
        CleanGroupsBean cleanGroupsBean = mDataGroup.get(position);
        holder.mItemGroupIvSelect.setVisibility(View.VISIBLE);
        holder.mItemGroupIvSelect.setState(cleanGroupsBean.getState());
        holder.mItemGroupName.setText(cleanGroupsBean.getTitle());
        String result = ConvertUtils.formatFileSize(mDataGroupSize.get(position));
        holder.mItemGroupSize.setText(result);
        holder.mVGroupDividerLine.setVisibility(View.INVISIBLE);
        holder.mItemGroupPb.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mDataGroup.size();
    }

    public void removeAllItem() {

        int count = mDataGroup.size();
        if (count == 0) {
            return;
        }

        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                TheApplication.postRunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mDataGroup.size() == 0) {
                            if (mOnItemRemoveListener != null) {
                                mOnItemRemoveListener.onLastItemRemoved();
                            }
                            timer.cancel();
                            return;
                        }
                        mDataGroup.remove(mDataGroup.size() - 1);
                        notifyItemRemoved(mDataGroup.size());
                    }
                });
            }
        };

        timer.schedule(timerTask, 0, 300);
    }

    public void setOnItemRemoveListener(OnItemRemoveListener listener) {
        this.mOnItemRemoveListener = listener;
    }

    public interface OnItemRemoveListener {
        void onLastItemRemoved();
    }

    static class GroupHolder extends RecyclerView.ViewHolder {
        private TextView mItemGroupName;
        private TextView mItemGroupSize;
        private ProgressWheel mItemGroupPb;
        private GroupSelectBox mItemGroupIvSelect;
        private View mVGroupDividerLine;

        public GroupHolder(View itemView) {
            super(itemView);
            mItemGroupName = (TextView) itemView.findViewById(R.id.item_group_name);
            mItemGroupSize = (TextView) itemView.findViewById(R.id.item_group_size);
            mItemGroupPb = (ProgressWheel) itemView.findViewById(R.id.item_group_pb);
            mItemGroupIvSelect = (GroupSelectBox) itemView.findViewById(R.id.item_group_iv_select);
            mVGroupDividerLine = (View) itemView.findViewById(R.id.v_group_divider_line);
        }
    }
}
