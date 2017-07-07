package com.jb.filemanager.function.image.presenter;


import android.database.Cursor;
import android.provider.MediaStore;

import com.jb.filemanager.function.image.modle.ImageGroupModle;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by bill wang on 2017/6/27.
 */

public class ImagePresenter implements ImageContract.Presenter {

    private ImageContract.View mView;
    private ImageContract.Support mSupport;
    //选择图片列表
    private List<ImageModle> mSelectedImageList = new ArrayList<>();
    private List<ImageGroupModle> mImageGroupModleList = new ArrayList<>();
    private int mTotalSize = 0;

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
        if (mView != null) {
            mView.finish();
        }
    }

    @Override
    public void handleCancel() {
        for (int i = 0; i < mImageGroupModleList.size(); i++) {
            ImageGroupModle imageGroupModle = mImageGroupModleList.get(i);
            imageGroupModle.isCheck = false;
        }
        for (int i = 0; i < mSelectedImageList.size(); i++) {
            mSelectedImageList.get(i).isChecked = false;
        }
        mView.notifyViewChg();
        mSelectedImageList.clear();
        if (mView != null) {
            mView.showNoSelected();
        }
    }

    @Override
    public void handleCheck(boolean isCheck) {
        if (isCheck) {
            //改成全取消
            handleCancel();
        } else {
            //改成全选
            if (mImageGroupModleList != null) {
                mSelectedImageList.clear();
                for (int i = 0; i < mImageGroupModleList.size(); i++) {
                    ImageGroupModle imageGroupModle = mImageGroupModleList.get(i);
                    imageGroupModle.isCheck = true;
                    for (int i1 = 0; i1 < imageGroupModle.mImageModleList.size(); i1++) {
                        List<ImageModle> imageModles = imageGroupModle.mImageModleList.get(i1);
                        for (int i2 = 0; i2 < imageModles.size(); i2++) {
                            ImageModle imageModle = imageModles.get(i2);
                            imageModle.isChecked = true;
                            mSelectedImageList.add(imageModle);
                        }
                    }
                }
                //全选
                mView.showAllSelected();
                mView.notifyViewChg();
            }
        }
    }

    @Override
    public void handleSelected(List<ImageGroupModle> imageGroupModleList) {
        if (imageGroupModleList != null) {
            mTotalSize = 0;
            mSelectedImageList.clear();
            for (int i = 0; i < imageGroupModleList.size(); i++) {
                ImageGroupModle imageGroupModle = imageGroupModleList.get(i);
                for (int i1 = 0; i1 < imageGroupModle.mImageModleList.size(); i1++) {
                    List<ImageModle> imageModles = imageGroupModle.mImageModleList.get(i1);
                    for (int i2 = 0; i2 < imageModles.size(); i2++) {
                        ImageModle imageModle = imageModles.get(i2);
                        if (imageModle.isChecked) {
                            mSelectedImageList.add(imageModle);
                        }
                    }
                    mTotalSize += imageModles.size();
                }
            }
            if (mView != null) {
                if (mTotalSize == mSelectedImageList.size()) {
                    //全选
                    mView.showAllSelected();
                } else if (mSelectedImageList.size() > 0) {
                    //选择了一部分
                    mView.showSelected(mSelectedImageList.size());
                } else {
                    //没有选择
                    mView.showNoSelected();
                }
            }
        }
    }

    @Override
    public void handleDataFinish(Cursor cursor) {
        if (cursor != null) {
            try {
                ImageGroupModle imageGroupModle = new ImageGroupModle();
                List<ImageModle> imageModleList = null;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
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
                        imageGroupModle.mTimeDate = TimeUtil.getTime(modifiedTime, simpleDateFormat);
                        mImageGroupModleList.add(imageGroupModle);
                    }
                    if (imageModleList == null) {
                        imageModleList = new ArrayList<>(3);
                        imageModleList.add(new ImageModle(path, id, false, modifiedTime));
                        imageGroupModle.mImageModleList.add(imageModleList);
                    }
                    if (imageModleList.size() < 3) {
                        imageModleList.add(new ImageModle(path, id, false, modifiedTime));
                    } else {
                        imageModleList = new ArrayList<>(3);
                        imageModleList.add(new ImageModle(path, id, false, modifiedTime));
                        imageGroupModle.mImageModleList.add(imageModleList);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                cursor.close();
            }
            if (mView != null) {
                mView.bindData(mImageGroupModleList);
            }
        }
    }
}
