package com.jb.filemanager.function.image.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.image.ImageDetailFragment;
import com.jb.filemanager.function.image.app.BaseFragment;
import com.jb.filemanager.function.image.modle.ImageGroupModle;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 17-7-3.
 */

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    //图片分组数据列表
    private List<ImageGroupModle> mImageGroupModleList;
    private BaseFragment mCurrentFragment;

    public ImageAdapter(List<ImageGroupModle> imageGroupModleList, BaseFragment currentFragment) {
        this.mImageGroupModleList = imageGroupModleList;
        this.mCurrentFragment = currentFragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType > 0) {
            return new ViewItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_result, parent, false));
        }
        return new ViewGroupHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_image_result, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return isImageModle(position) ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewGroupHolder) {
            //时间数据
            ImageGroupModle imageGroupModle = getImageGroup(position);
            if (imageGroupModle != null) {
                ((ViewGroupHolder) holder).mDate.setText(imageGroupModle.mTimeDate);
            }
        } else if (holder instanceof ViewItemHolder) {
            //图片数据
            ImageModle imageModle = getImageModle(position);
            if (imageModle != null) {
                ImageLoader.getInstance(TheApplication.getAppContext()).displayImage(imageModle.mImagePath,
                        ((ViewItemHolder) holder).mPhoto, R.drawable.common_default_app_icon);
                if (imageModle.isChecked) {
                    ((ViewItemHolder) holder).mGroupSelectBox.setState(GroupSelectBox.SelectState.ALL_SELECTED);
                } else {
                    ((ViewItemHolder) holder).mGroupSelectBox.setState(GroupSelectBox.SelectState.NONE_SELECTED);
                }
                ((ViewItemHolder) holder).mPhoto.setTag(position);
                ((ViewItemHolder) holder).mPhoto.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        //局部刷新
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if (holder instanceof ViewItemHolder) {
                if (getImageModle(position).isChecked) {
                    ((ViewItemHolder) holder).mGroupSelectBox.setState(GroupSelectBox.SelectState.ALL_SELECTED);
                } else {
                    ((ViewItemHolder) holder).mGroupSelectBox.setState(GroupSelectBox.SelectState.NONE_SELECTED);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mImageGroupModleList != null) {
            int size = 0;
            for (int i = 0; i < mImageGroupModleList.size(); i++) {
                ImageGroupModle imageGroupModle = mImageGroupModleList.get(i);
                int num = imageGroupModle.mImageModleList.size();
                //+ 1 代表标题
                size += 1;
                //+ 图片数据的个数
                size += num;
            }
            return size;
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        //图片
        if (v.getId() == R.id.item_image_result_photo) {
            //点击图片 选中图片
//            notifyItemChanged((Integer) v.getTag());
            startDetailPager((int) v.getTag());
        }
    }

    /**
     * 打开详情页
     * */
    private void startDetailPager(int pos) {
        int titleNum = 0;
        int size = 0;
        int currentPos = 0;
        boolean isSet = false;
        ArrayList<ImageModle> imageModleList = new ArrayList<>();
        for (int i = 0; i < mImageGroupModleList.size(); i++) {
            size += 1;
            titleNum  += 1;
            ImageGroupModle imageGroupModle = mImageGroupModleList.get(i);
            imageModleList.addAll(imageGroupModle.mImageModleList);
            size += imageGroupModle.mImageModleList.size();
            if (size > pos && !isSet) {
                isSet = true;
                currentPos = pos - titleNum;
            }
        }
        ImageDetailFragment imageDetailFragment = new ImageDetailFragment();
        imageDetailFragment.setExtras(imageModleList, currentPos);
        mCurrentFragment.pushFragment(imageDetailFragment);
    }

    /**
     * 获取图片数据
     * */
    private ImageModle getImageModle(int position) {
        if (mImageGroupModleList != null) {
            int size = 0;
            for (int i = 0; i < mImageGroupModleList.size(); i++) {
                ImageGroupModle imageGroupModle = mImageGroupModleList.get(i);
                int num = imageGroupModle.mImageModleList.size();
                int startPos = size;
                //+ 1 代表标题
                size += 1;
                //+ 图片数据的个数
                size += num;
                int endPos = size;
                if (endPos > position && position > startPos) {
                    //如果索引正好在这个范围内
                    return imageGroupModle.mImageModleList.get(position - startPos - 1);
                }
            }
        }
        return null;
    }

    /**
     * 获取图片分组时间数据
     * */
    private ImageGroupModle getImageGroup(int position) {
        if (mImageGroupModleList != null) {
            int size = 0;
            for (int i = 0; i < mImageGroupModleList.size(); i++) {
                ImageGroupModle imageGroupModle = mImageGroupModleList.get(i);
                int num = imageGroupModle.mImageModleList.size();
                if (position == size) {
                    return imageGroupModle;
                }
                //+ 1 代表标题
                size += 1;
                //+ 图片数据的个数
                size += num;
            }
        }
        return null;
    }

    /**
     * 是否是图片数据
     * */
    private boolean isImageModle(int position) {
        int size = 0;
        for (int i = 0; i < mImageGroupModleList.size(); i++) {
            ImageGroupModle imageGroupModle = mImageGroupModleList.get(i);
            if (position == size) {
                return false;
            }
            //+ 1 代表标题
            size += 1;
            //+ 图片数据的个数
            size += imageGroupModle.mImageModleList.size();
            if (size - 1 >= position) {
                //如果索引正好在这个范围内
                return true;
            }
        }
        return false;
    }

    /**
     * 时间组视图
     * */
    private class ViewGroupHolder extends RecyclerView.ViewHolder {
        //时间
        private TextView mDate;

        public ViewGroupHolder(View itemView) {
            super(itemView);
            mDate = (TextView) itemView;
        }
    }

    /**
     * 图片分组视图
     * */
    private class ViewItemHolder extends RecyclerView.ViewHolder {
        //图片
        private ImageView mPhoto;
        //图片选项
        private GroupSelectBox mGroupSelectBox;

        public ViewItemHolder(View itemView) {
            super(itemView);
            mPhoto = (ImageView) itemView.findViewById(R.id.item_image_result_photo);
            mGroupSelectBox = (GroupSelectBox) itemView.findViewById(R.id.item_image_result_gsb);
            mGroupSelectBox.setImageSource(R.drawable.choose_none, R.drawable.choose_all, R.drawable.choose_all);
        }
    }
}
