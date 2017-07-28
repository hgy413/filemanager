package com.jb.filemanager.function.image.presenter;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by nieyh on 2017/6/27.
 */

public class ImageSupport implements ImageContract.Support {
    //是否是内部存储
    private boolean isInternalStorage = false;
    //临时存储的图片列表
    private List<ImageModle> mTempImageModleList;

    public ImageSupport(boolean isInternalStorage) {
        this.isInternalStorage = isInternalStorage;
    }

    @Override
    public void deleteImage(final ImageModle imageModle) {
        if (imageModle == null) {
            return;
        }
        TheApplication.postRunOnShortTaskThread(new Runnable() {
            @Override
            public void run() {
                int count = deleteImageInDb(imageModle);
                if (count > 0) {
                    Toast.makeText(TheApplication.getAppContext(), R.string.toast_delete_file_success, Toast.LENGTH_SHORT).show();
                    FileUtil.deleteFile(imageModle.mImagePath);
                } else {
                    Toast.makeText(TheApplication.getAppContext(), R.string.toast_delete_file_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int deleteImageInDb(ImageModle imageModle) {
        //合成uri
        Uri uri = Uri.withAppendedPath(isInternalStorage ? MediaStore.Images.Media.INTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "" + imageModle.mImageId);
        //删除指定文件
        return TheApplication.getAppContext().getContentResolver().delete(uri, null, null);
    }

    @Override
    public void saveImageModle(List<ImageModle> imageModleList) {
        if (imageModleList == null || imageModleList.size() == 0) {
            return;
        }
        mTempImageModleList = new ArrayList<>(imageModleList.size());
        mTempImageModleList.addAll(imageModleList);
    }

    @Override
    public void copyFile(File oldFile, File newFile) {
        if (oldFile == null || newFile == null) {
            return;
        }
        if (oldFile.isDirectory() || newFile.isDirectory()) {
            return;
        }
        if (!newFile.exists()) {
            return;
        }
        MediaScannerConnection.scanFile(TheApplication.getAppContext(), new String[]{newFile.getAbsolutePath()}, null, null);
    }

    @Override
    public void cutFile(File oldFile, File newFile) {
        if (oldFile == null || newFile == null) {
            return;
        }
        if (oldFile.isDirectory() || newFile.isDirectory()) {
            return;
        }
        if (!newFile.exists()) {
            return;
        }
        if (mTempImageModleList == null) {
            return;
        }
        Iterator<ImageModle> iterator = mTempImageModleList.iterator();
        //删除数据库中老数据
        while (iterator.hasNext()) {
            ImageModle imageModle = iterator.next();
            if (imageModle.mImagePath.equals(oldFile.getAbsolutePath())) {
                deleteImageInDb(imageModle);
                iterator.remove();
                break;
            }
        }
        //扫描新文件数据
        MediaScannerConnection.scanFile(TheApplication.getAppContext(), new String[]{newFile.getAbsolutePath()}, null, null);
    }

    @Override
    public void renameFile(File oldFile, File newFile) {
        cutFile(oldFile, newFile);
    }
}
