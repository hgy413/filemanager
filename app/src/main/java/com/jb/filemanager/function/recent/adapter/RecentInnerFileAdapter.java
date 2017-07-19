package com.jb.filemanager.function.recent.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.bean.BlockItemFileBean;
import com.jb.filemanager.function.recent.listener.RecentItemCheckChangedListener;
import com.jb.filemanager.function.recent.util.RecentFileUtil;
import com.jb.filemanager.function.zipfile.bean.ZipFileItemBean;
import com.jb.filemanager.function.zipfile.dialog.ZipFileOperationDialog;
import com.jb.filemanager.function.zipfile.util.ZipUtils;

import java.io.File;
import java.util.List;

/**
 * Created by xiaoyu on 2017/7/17 21:28.
 */

public class RecentInnerFileAdapter extends BaseAdapter {

    private final List<BlockItemFileBean> mItemFiles;
    private Activity mActivity;
    private RecentItemCheckChangedListener mListener;

    public RecentInnerFileAdapter(Activity activity, BlockBean bean) {
        mActivity = activity;
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
        final Context context = parent.getContext().getApplicationContext();
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context,
                    R.layout.inner_item_file, null);
            holder.root = convertView.findViewById(R.id.inner_item_file_root);
            holder.icon = (ImageView) convertView.findViewById(R.id.inner_item_file_icon);
            holder.name = (TextView) convertView.findViewById(R.id.inner_item_file_name);
            holder.selectBtn = (ImageView) convertView.findViewById(R.id.inner_item_file_select);
            holder.divider = convertView.findViewById(R.id.inner_item_file_divider);
            convertView.setTag(R.layout.inner_item_file, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.layout.inner_item_file);
        }
        final BlockItemFileBean item = getItem(position);
        holder.icon.setImageResource(R.drawable.img_file);
        holder.name.setText(item.getFileName());
        holder.selectBtn.setImageResource(item.isSelected() ? R.drawable.select_all : R.drawable.select_none);
        // divider问题
        if (mItemFiles.size() > 3) {
            // 有More按钮
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            // 没有More按钮 最后一个隐藏
            holder.divider.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);
        }
        holder.selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelected(!item.isSelected());
                if (mListener != null) {
                    mListener.onItemCheckChanged();
                }
                notifyDataSetChanged();
            }
        });
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ZipUtils.isZipFormatFile(new File(item.getFilePath()))
                        || ZipUtils.isRarFormatFile(new File(item.getFilePath()))) {
                    ZipFileOperationDialog zipFileOperationDialog = new ZipFileOperationDialog(mActivity,
                            new ZipFileItemBean(new File(item.getFilePath())));
                    zipFileOperationDialog.show();
                } else {
                    RecentFileUtil.openFile(context, new File(item.getFilePath()));
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        View root;
        ImageView icon;
        TextView name;
        ImageView selectBtn;
        View divider;
    }

    public void setCheckListener(RecentItemCheckChangedListener l) {
        mListener = l;
    }
}
