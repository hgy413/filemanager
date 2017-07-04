package com.jb.filemanager.function.image.presenter;


import android.database.Cursor;
import android.provider.MediaStore;

import com.jb.filemanager.function.image.modle.ImageGroupModle;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by bill wang on 2017/6/27.
 *
 */

public class ImagePresenter implements ImageContract.Presenter {


    private ImageContract.View mView;
    private ImageContract.Support mSupport;

    public ImagePresenter(ImageContract.View view, ImageContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void handlePressHomeKey() {
        // nothing to do
    }

    @Override
    public void handleBackPressed() {

    }

    @Override
    public void handleBackClick() {

    }

    @Override
    public void handleDataFinish(Cursor cursor) {
        if (cursor != null) {
            List<ImageGroupModle> mImageGroupModle = new ArrayList<>();
            try {
                ImageGroupModle imageGroupModle = new ImageGroupModle();
                while (cursor.moveToNext()) {
                    Calendar now;
                    //获取图片的路径
                    String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    //此处返回数据为秒钟 所以乘以1000
                    long modifiedTime = cursor.getLong(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATE_ADDED)) * 1000;
                    int id = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    now = Calendar.getInstance();
                    now.setTimeInMillis(modifiedTime);
                    if (imageGroupModle.mCalendar == null || now.get(Calendar.DAY_OF_YEAR) > imageGroupModle.mCalendar.get(Calendar.DAY_OF_YEAR)) {
                        //如果不是同一天
                        imageGroupModle = new ImageGroupModle();
                        imageGroupModle.mCalendar = now;
                        imageGroupModle.mTimeDate = TimeUtil.getTime(modifiedTime);
                        mImageGroupModle.add(imageGroupModle);
                    }
                    imageGroupModle.mImageModleList.add(new ImageModle(path, id, false, modifiedTime));
                }
            } catch (Exception ex) {
              ex.printStackTrace();
            } finally {
                cursor.close();
            }
            if (mView != null) {
                mView.bindData(mImageGroupModle);
            }
        }
    }
}
