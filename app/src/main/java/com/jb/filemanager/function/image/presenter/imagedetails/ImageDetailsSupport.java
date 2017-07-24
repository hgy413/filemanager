package com.jb.filemanager.function.image.presenter.imagedetails;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.image.ImageFileDeleteEvent;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.util.file.FileUtil;

import java.io.IOException;

/**
 * Created by nieyh on 17-7-4.
 */

public class ImageDetailsSupport implements ImageDetailsContract.Support {

    @Override
    public void setWallPager(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(TheApplication.getAppContext(), R.string.toast_set_wallpaper_fail, Toast.LENGTH_LONG).show();
            return;
        }
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(TheApplication.getAppContext());
        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(TheApplication.getAppContext(), R.string.toast_set_wallpaper_success, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(TheApplication.getAppContext(), R.string.toast_set_wallpaper_fail, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void deleteImage(final ImageModle imageModle) {
        if (imageModle == null) {
            return;
        }
        TheApplication.postRunOnShortTaskThread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = FileUtil.deleteFile(imageModle.mImagePath);
                if (isSuccess) {
                    Toast.makeText(TheApplication.getAppContext(), R.string.toast_delete_file_success, Toast.LENGTH_SHORT).show();
                    TheApplication.getGlobalEventBus().post(new ImageFileDeleteEvent(imageModle));
                } else {
                    Toast.makeText(TheApplication.getAppContext(), R.string.toast_delete_file_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
