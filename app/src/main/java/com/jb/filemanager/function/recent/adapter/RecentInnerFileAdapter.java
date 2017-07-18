package com.jb.filemanager.function.recent.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.bean.BlockItemFileBean;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/17 21:28.
 */

public class RecentInnerFileAdapter extends BaseAdapter {

    private final List<BlockItemFileBean> mItemFiles;

    public RecentInnerFileAdapter(BlockBean bean) {
        mItemFiles = bean.getItemFiles();
    }

    @Override
    public int getCount() {
        return Math.min(3, mItemFiles.size());
    }

    @Override
    public BlockItemFileBean getItem(int position) {
        return mItemFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(parent.getContext().getApplicationContext(),
                    R.layout.inner_item_file, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.inner_item_file_icon);
            holder.name = (TextView) convertView.findViewById(R.id.inner_item_file_name);
            holder.selectBtn = (ImageView) convertView.findViewById(R.id.inner_item_file_select);

            convertView.setTag(R.layout.inner_item_file, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.layout.inner_item_file);
        }
        final BlockItemFileBean item = getItem(position);
        holder.icon.setImageResource(R.drawable.img_file);
        holder.name.setText(item.getFileName());
        holder.selectBtn.setImageResource(item.isSelected() ? R.drawable.select_all : R.drawable.select_none);
        holder.selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelected(!item.isSelected());
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView icon;
        TextView name;
        ImageView selectBtn;
    }
}
