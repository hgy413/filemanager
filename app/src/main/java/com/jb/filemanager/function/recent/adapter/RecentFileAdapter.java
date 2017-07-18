package com.jb.filemanager.function.recent.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.util.RecentFileUtil;
import com.jb.filemanager.util.DrawUtils;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/17 14:49.
 */

public class RecentFileAdapter extends BaseAdapter {

    private List<BlockBean> mBlockList;

    public RecentFileAdapter(List<BlockBean> data) {
        mBlockList = data;
    }

    @Override
    public int getCount() {
        return mBlockList.size();
    }

    @Override
    public BlockBean getItem(int position) {
        return mBlockList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Context context = parent.getContext().getApplicationContext();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context,
                    R.layout.item_recent_file_block, null);
            holder.titleView = convertView.findViewById(R.id.item_block_title_view_group);
            holder.dirName = (TextView) convertView.findViewById(R.id.item_dir_name);
            holder.withinTime = (TextView) convertView.findViewById(R.id.item_within_time);
            holder.listView = (ListView) convertView.findViewById(R.id.item_block_list_view);
            holder.tvMoreBtn = convertView.findViewById(R.id.item_btn_more);
            convertView.setTag(R.layout.item_recent_file_block, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.layout.item_recent_file_block);
        }

        BlockBean item = getItem(position);
        holder.dirName.setText(item.getBlockDirName());
        holder.withinTime.setText(RecentFileUtil.formatWithinTime(context, item.getWithinTime()));
        if (item.isPictureType()) {
            // 设置图片类型适配器
            holder.listView.setAdapter(new RecentInnerPictureAdapter(item));
            // 图片高度 + divider 2dp
            int height = DrawUtils.dip2px(2) + (context.getResources().getDisplayMetrics().widthPixels - DrawUtils.dip2px(30)) / 3;
            int totalHeight = holder.listView.getAdapter().getCount() * height;
            ViewGroup.LayoutParams params = holder.listView.getLayoutParams();
            params.height = totalHeight;
            holder.listView.setLayoutParams(params);
        } else {
            // 设置文件类型适配器
            holder.listView.setAdapter(new RecentInnerFileAdapter(item));
            setListViewHeightBasedOnChildren(holder.listView);
        }
        holder.tvMoreBtn.setVisibility(!item.isPictureType() && item.isHaveMore() ? View.VISIBLE : View.GONE);

        return convertView;
    }

    private class ViewHolder {
        View titleView;
        TextView dirName;
        TextView withinTime;
        ListView listView;
        View tvMoreBtn;
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void setPicListViewHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i, null, listView);
            view.measure(0, 0);
            totalHeight += view.getMeasuredHeight();
            Log.e("picAdapter", "height = " + view.getHeight());
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
