package com.jb.filemanager.util.imageloader;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.jb.filemanager.R;
import com.jb.filemanager.manager.PackageManagerLocker;

/**
 * 加载图片任务：应用图标&APK
 *
 * @author chenhewen
 */
class IconLoadTask extends AbstractImageLoadTask {
    public IconLoadTask(IconLoadTask.Builder builder) {
        super(builder);
    }

    protected Bitmap tryLoadBitmap() throws TaskCancelledException {
        Bitmap bitmap = null;
        try {

            //checkTaskNotActual();
            Drawable drawable = getApplicationDrawable(mUri);
            if (drawable != null) {
                if (drawable instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    bitmap = bitmapDrawable.getBitmap();
                } else {
                    final int badgedWidth = drawable.getIntrinsicWidth();
                    final int badgedHeight = drawable.getIntrinsicHeight();
                    if (badgedWidth > 0 || badgedHeight > 0) {
                        bitmap = Bitmap.createBitmap(badgedWidth, badgedHeight, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, badgedWidth, badgedHeight);
                        drawable.draw(canvas);
                    }
                }
            } else if (drawable == null || bitmap == null) {
                // 兼容未安装应用
                BitmapDrawable drawable2 = (BitmapDrawable) getApplicationDrawableIfNotInstalled(mUri);
                if (drawable2 != null) {
                    bitmap = drawable2.getBitmap();
                } else {
                    BitmapDrawable drawable3 = (BitmapDrawable) mContext
                            .getResources().getDrawable(
                                    R.drawable.common_default_app_icon);
                    bitmap = drawable3.getBitmap();
                }

            }

        } catch (Exception e) {

        }
        return bitmap;
    }

    private Drawable getApplicationDrawable(String pkgName) {
        Drawable drawable = null;
        try {
            drawable = PackageManagerLocker.getInstance().getApplicationIcon(pkgName);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return drawable;
    }

    private Drawable getApplicationDrawableIfNotInstalled(String path) {
        PackageInfo packageInfo = PackageManagerLocker.getInstance().getPackageArchiveInfo(path,
                PackageManager.GET_ACTIVITIES);

        if (packageInfo != null) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            appInfo.sourceDir = path;
            appInfo.publicSourceDir = path;
            try {
                return PackageManagerLocker.getInstance().getApplicationIcon(appInfo);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}