package com.jb.filemanager.function.image.presenter.imagedetails;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;

import com.jb.filemanager.function.image.modle.ImageModle;

import java.util.List;

/**
 * Created by nieyh on 17-7-4.
 */

public interface ImageDetailsContract {

    interface View {
        void bindData(List<ImageModle> imageModleList);
        void setViewPos(int pos);
        void chgTitle(String title);
        Bitmap getCurrentBitmap();
    }

    interface Support {
        void setWallPager(Bitmap bitmap);
    }

    interface Presenter {
        void handleExtras(List<Parcelable> imageModleList, int pos);
        void handlePagerChange(int pos);
        void handleSetWallPaper();
    }
}
