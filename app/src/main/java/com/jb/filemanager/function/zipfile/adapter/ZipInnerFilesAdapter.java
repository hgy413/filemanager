package com.jb.filemanager.function.zipfile.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jb.filemanager.function.zipfile.listener.ZipListAdapterClickListener;
import com.jb.filemanager.function.zipfile.util.FileUtils;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.TimeUtil;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/4 15:36.
 */

public class ZipInnerFilesAdapter extends BaseAdapter {

    private List<ZipPreviewFileBean> mData;
    private ZipListAdapterClickListener mListener;

    public ZipInnerFilesAdapter(List<ZipPreviewFileBean> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public ZipPreviewFileBean getItem(int position) {
        return mData.get(position);
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
            convertView = View.inflate(parent.getContext().getApplicationContext(), R.layout.item_zip_pre, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.item_zip_pre_icon);
            holder.name = (TextView) convertView.findViewById(R.id.item_zip_pre_name);
            holder.size = (TextView) convertView.findViewById(R.id.item_zip_pre_size);
            holder.checkbox = (ImageView) convertView.findViewById(R.id.item_zip_pre_checkbox);
            holder.divider = convertView.findViewById(R.id.item_zip_pre_divider);
            convertView.setTag(R.layout.item_zip_pre, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.layout.item_zip_pre);
        }
        final ZipPreviewFileBean item = getItem(position);
        String extension = FileUtils.getFileExtension(item.getFileName());
        if (item.isDirectory()) {
            holder.icon.setImageResource(R.drawable.img_folder);
        } else if ("png".equalsIgnoreCase(extension) || "jpg".equalsIgnoreCase(extension)) {
            holder.icon.setImageResource(R.drawable.img_picture);
        } else if ("mp3".equalsIgnoreCase(extension)) {
            holder.icon.setImageResource(R.drawable.img_music);
        } else if ("mp4".equalsIgnoreCase(extension)) {
            holder.icon.setImageResource(R.drawable.video_icon);
        } else if ("zip".equalsIgnoreCase(extension) || "rar".equalsIgnoreCase(extension)) {
            holder.icon.setImageResource(R.drawable.zip_icon);
        } else if ("doc".equalsIgnoreCase(extension) || "docx".equalsIgnoreCase(extension)) {
            holder.icon.setImageResource(R.drawable.doc_icon);
        } else if ("txt".equalsIgnoreCase(extension)) {
            holder.icon.setImageResource(R.drawable.img_file);
        } else {
            holder.icon.setImageResource(R.drawable.unknown_icon);
        }
        holder.name.setText(item.getFileName());
        if (item.isDirectory()) {
            holder.size.setText(TimeUtil.getTime(item.getLastModifyTime()));
        } else {
            holder.size.setText(ConvertUtils.formatFileSize(item.getSize()) + " " + TimeUtil.getTime(item.getLastModifyTime()));
        }
        holder.divider.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);
        holder.checkbox.setImageResource(item.isSelected() ? R.drawable.choose_all : R.drawable.choose_none);
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelected(!item.isSelected());
                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.onSwitchClick();
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView size;
        public TextView date;
        public ImageView checkbox;
        public View divider;
    }

    public void setListener(ZipListAdapterClickListener listener) {
        mListener = listener;
    }

}

