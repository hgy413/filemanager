package com.jb.filemanager.function.search.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.search.modle.FileInfo;
import com.jb.filemanager.function.search.util.FilePlus;

import java.util.List;

/**
 * Created by nieyh on 17-7-5.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ItemViewHolder> {

    //图片分组数据列表
    private List<FileInfo> mFileInfoList;

    public SearchResultAdapter(List<FileInfo> fileInfoList) {
        this.mFileInfoList = fileInfoList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        FileInfo fileInfo = mFileInfoList.get(position);
        if (FilePlus.isDirectoryType(fileInfo.mFileType)) {
            //展示文件夹图标
//            holder.mIcon.set
        } else {
            //展示对应文件图标
        }
        holder.mPath.setText(fileInfo.mFileAbsolutePath);
        holder.mName.setText(fileInfo.mFileName);
    }

    @Override
    public int getItemCount() {
        if (mFileInfoList == null) {
            return 0;
        }
        return mFileInfoList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIcon;
        public TextView mPath;
        public TextView mName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.item_search_result_icon);
            mPath = (TextView) itemView.findViewById(R.id.item_search_result_path);
            mName = (TextView) itemView.findViewById(R.id.item_search_result_name);
        }
    }
}
