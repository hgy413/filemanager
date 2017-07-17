package com.jb.filemanager.function.image.presenter;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;

import com.jb.filemanager.function.image.modle.ImageGroupModle;

import java.util.List;

/**
 * Created by bill wang on 2017/6/27.
 */

public class ImageContract {

    public interface View {
        void bindData(List<ImageGroupModle> imageGroupModleList);
        void showSelected(int selectSize, int allSize);
        void notifyViewChg();
        void finish();
    }

    public interface Presenter {
        void handleBackClick();
        void handleCancel();
        void handleCheck(boolean isCheck);
        void handleSelected(List<ImageGroupModle> imageGroupModleList);
        void handleDataFinish(Cursor cursor);
    }

    public interface Support {
        Context getContext();
        Application getApplication();
    }
}
