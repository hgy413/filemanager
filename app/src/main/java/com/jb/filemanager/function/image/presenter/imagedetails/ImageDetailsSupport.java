package com.jb.filemanager.function.image.presenter.imagedetails;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.jb.filemanager.TheApplication;

import java.io.IOException;

/**
 * Created by nieyh on 17-7-4.
 */

public class ImageDetailsSupport implements ImageDetailsContract.Support {

    @Override
    public void setWallPager(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(TheApplication.getAppContext(), "壁纸设置失败!!", Toast.LENGTH_LONG).show();
            return;
        }
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(TheApplication.getAppContext());
        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(TheApplication.getAppContext(), "壁纸设置成功!!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
