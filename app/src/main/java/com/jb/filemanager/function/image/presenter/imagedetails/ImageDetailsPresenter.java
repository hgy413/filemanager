package com.jb.filemanager.function.image.presenter.imagedetails;

import android.graphics.Bitmap;
import android.os.Parcelable;

import com.jb.filemanager.function.image.modle.ImageModle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 17-7-4.
 */

public class ImageDetailsPresenter implements ImageDetailsContract.Presenter {

    private ImageDetailsContract.View mView;
    private ImageDetailsContract.Support mSupport;
    private List<ImageModle> mImageModleList;
    private int mCurrentPos;

    public ImageDetailsPresenter(ImageDetailsContract.View view, ImageDetailsContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void handleExtras(List<Parcelable> parcelableList, int pos) {
        mImageModleList = new ArrayList<>(parcelableList.size());
        mCurrentPos = pos;
        for (int i = 0; i < parcelableList.size(); i++) {
            Parcelable parcelable = parcelableList.get(i);
            if (parcelable instanceof ImageModle) {
                mImageModleList.add((ImageModle) parcelable);
            }
        }
//        if (mView != null) {
//            StringBuffer stringBuffer = new StringBuffer();
//            stringBuffer.append(mCurrentPos + 1);
//            stringBuffer.append("/");
//            stringBuffer.append(mImageModleList.size());
//            mView.chgTitle(stringBuffer.toString());
//        }
        mView.bindData(mImageModleList);
        mView.setViewPos(mCurrentPos);
    }

    @Override
    public void handlePagerChange(int pos) {
        mCurrentPos = pos;
//        if (mView != null) {
//            StringBuffer stringBuffer = new StringBuffer();
//            stringBuffer.append(mCurrentPos + 1);
//            stringBuffer.append("/");
//            stringBuffer.append(mImageModleList.size());
//            mView.chgTitle(stringBuffer.toString());
//        }
    }

    @Override
    public void handleSetWallPaper() {
        if (mView != null && mSupport != null) {
            Bitmap bitmap = mView.getCurrentBitmap();
            mSupport.setWallPager(bitmap);
        }
    }
}
