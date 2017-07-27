package com.jb.filemanager.function.image.presenter;

import android.database.Cursor;

import com.jb.filemanager.function.image.modle.ImageGroupModle;
import com.jb.filemanager.function.image.modle.ImageModle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill wang on 2017/6/27.
 */

public class ImageContract {

    public interface View {
        void bindData(List<ImageGroupModle> imageGroupModleList);
        void showSelected(int selectSize, int allSize);
        void dismissBobar();
        void showBobar();
        void notifyViewChg();
        void finish();
        void gotoStoragePage();
    }

    public interface Presenter {
        void handleBackClick();
        void handleCancel();
        void handleCheck(boolean isCheck);
        void handleSelected(List<ImageGroupModle> imageGroupModleList);
        void handleDataFinish(Cursor cursor);
        void handleCopy();
        void handleCut();
        void handleDeleted();
        void handleRename();
        void handleDeletedBg(ImageModle imageModle);
        ArrayList<File> getCurrentSelectedFiles();
        void release();
    }

    public interface Support {
        void deleteImage(ImageModle imageModle);
        int deleteImageInDb(ImageModle imageModle);
        void renameImage(ImageModle imageModle);
        void saveImageModle(List<ImageModle> imageModle);
        void copyFile(File oldFile, File newFile);
        void cutFile(File oldFile, File newFile);
    }
}
