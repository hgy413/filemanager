package com.jb.filemanager.function.image.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.ui.view.PinchImageView;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.util.List;

/**
 * Created by nieyh on 17-7-4.
 * 图片预览器适配器
 */

public class ImageDetailsPagerAdapter extends PagerAdapter {

    private List<ImageModle> mImageModleList;

    private ImageView mCurrentView;

    private View.OnClickListener mImgClickListener;

    public ImageDetailsPagerAdapter(List<ImageModle> imageModleList) {
        mImageModleList = imageModleList;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ImageModle imageModle = mImageModleList.get(position);
        LayoutInflater inflater = LayoutInflater.from(collection.getContext());
        PinchImageView img = (PinchImageView) inflater.inflate(R.layout.view_image_details, collection, false);
        collection.addView(img);
        ImageLoader.getInstance(TheApplication.getAppContext()).displayImage(imageModle.mImagePath,
                img);
        if (mImgClickListener != null) {
            img.setOnClickListener(mImgClickListener);
        }
        return img;
    }

    @Override
    public int getItemPosition(Object object) {
        if (mImageModleList == null || mImageModleList.size() == 0) {
            return POSITION_NONE;
        }
        //为了性能考虑 只有当前视图 返回None 强制删除 其他视图忽略
        if (object == mCurrentView) {
            //销毁当前的视图 也就是当前视图点击了删除
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mImageModleList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentView = (ImageView) object;
    }

    /**
     * 获取当前的视图
     * */
    public ImageView getCurrentView() {
        return mCurrentView;
    }

    /**
     * 设置图片点击事件
     * */
    public void setOnImgClickListener(View.OnClickListener imgClickListener) {
        this.mImgClickListener = imgClickListener;
    }
}
