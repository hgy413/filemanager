package com.jb.filemanager.function.recent.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.bean.BlockItemFileBean;
import com.jb.filemanager.function.recent.listener.RecentItemCheckChangedListener;
import com.jb.filemanager.function.recent.util.RecentFileUtil;
import com.jb.filemanager.home.MainActivity;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.io.File;
import java.util.List;

import static com.jb.filemanager.home.MainPresenter.EXTRA_FOCUS_FILE;
import static com.jb.filemanager.home.MainPresenter.FILE_EXPLORER;

/**
 * Created by xiaoyu on 2017/7/17 19:44.
 */

public class RecentInnerPictureAdapter extends BaseAdapter {

    private final List<BlockItemFileBean> mItemFiles;
    private RecentItemCheckChangedListener mListener;

    public RecentInnerPictureAdapter(BlockBean blockBean) {
        mItemFiles = blockBean.getItemFiles();
    }

    @Override
    public int getCount() {
        if (mItemFiles.size() > 3) return 2;
        return 1;
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
            convertView = View.inflate(context, R.layout.inner_item_picture, null);
            holder.images[0] = (ImageView) convertView.findViewById(R.id.inner_item_pic1);
            holder.images[1] = (ImageView) convertView.findViewById(R.id.inner_item_pic2);
            holder.images[2] = (ImageView) convertView.findViewById(R.id.inner_item_pic3);
            holder.btns[0] = (ImageView) convertView.findViewById(R.id.inner_item_pic_select1);
            holder.btns[1] = (ImageView) convertView.findViewById(R.id.inner_item_pic_select2);
            holder.btns[2] = (ImageView) convertView.findViewById(R.id.inner_item_pic_select3);
            holder.moreMask = (TextView) convertView.findViewById(R.id.inner_item_pic_mask);

            convertView.setTag(R.layout.inner_item_picture, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.layout.inner_item_picture);
        }
        int size = mItemFiles.size();
        for (int i = 0; i < 3; i++) {
            int index = position * 3 + i;
            if (index < size) {
                final BlockItemFileBean bean = mItemFiles.get(index);
                ImageLoader.getInstance(context).displayImage(bean.getFilePath(), holder.images[i], R.drawable.common_default_app_icon);
                holder.btns[i].setImageResource(bean.isSelected() ? R.drawable.select_all : R.drawable.select_none_image);
                if (index == 5) {
                    holder.moreMask.setVisibility(View.VISIBLE);
                    holder.moreMask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(TheApplication.getAppContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra(FILE_EXPLORER, true);
                            intent.putExtra(EXTRA_FOCUS_FILE, bean.getFilePath());
                            TheApplication.getAppContext().startActivity(intent);
                        }
                    });
                }
                holder.images[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentFileUtil.openFile(context, new File(bean.getFilePath()));
                    }
                });
                holder.btns[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bean.setSelected(!bean.isSelected());
                        if (mListener != null) {
                            mListener.onItemCheckChanged();
                        }
                        notifyDataSetChanged();
                    }
                });
            } else {
                holder.images[i].setVisibility(View.GONE);
                holder.btns[i].setVisibility(View.GONE);
                if (index == 5) {
                    holder.moreMask.setVisibility(View.GONE);
                }
            }
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView[] images = new ImageView[3];
        ImageView[] btns = new ImageView[3];
        TextView moreMask;
    }

    public void setCheckListener(RecentItemCheckChangedListener l) {
        mListener = l;
    }
}
