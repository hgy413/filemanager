package com.jb.filemanager.function.image.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.util.imageloader.ImageLoader;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by nieyh on 17-7-4.
 * 图片预览器适配器
 */

public class ImageDetailsPagerAdapter extends PagerAdapter {

    private List<ImageModle> mImageModleList;

    private ImageView mCurrentView;

    public ImageDetailsPagerAdapter(List<ImageModle> imageModleList) {
        mImageModleList = imageModleList;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ImageModle imageModle = mImageModleList.get(position);
        LayoutInflater inflater = LayoutInflater.from(collection.getContext());
        PhotoView img = (PhotoView) inflater.inflate(R.layout.view_image_details, collection, false);
        collection.addView(img);
        ImageLoader.getInstance(TheApplication.getAppContext()).displayImage(imageModle.mImagePath,
                img, R.drawable.common_default_app_icon);
        return img;
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
}
