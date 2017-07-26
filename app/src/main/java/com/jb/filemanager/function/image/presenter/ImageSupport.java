package com.jb.filemanager.function.image.presenter;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.util.FileUtil;

import java.io.File;

/**
 * Created by nieyh on 2017/6/27.
 */

public class ImageSupport implements ImageContract.Support {
    //是否是内部存储
    private boolean isInternalStorage = false;

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
                    FileUtil.deleteFile(imageModle.mImagePath);
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
    public void renameImage(final ImageModle imageModle) {
        if (imageModle == null) {
            return;
        }

        TheApplication.postRunOnShortTaskThread(new Runnable() {
            @Override
            public void run() {
//                //合成uri
//                Uri uri = Uri.withAppendedPath(isInternalStorage ? MediaStore.Images.Media.INTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imageModle.mImageId);
//                //删除指定文件
//                TheApplication.getAppContext().getContentResolver().delete(uri, null, null);
                //先删除这个数据库数据 然后指定文件扫描插入
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(Uri.fromFile(new File(imageModle.mImagePath)));
                TheApplication.getAppContext().sendBroadcast(scanIntent);
            }
        });
    }
}
