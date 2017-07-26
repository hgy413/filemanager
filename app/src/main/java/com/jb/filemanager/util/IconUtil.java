package com.jb.filemanager.util;

import android.widget.ImageView;

import com.jb.filemanager.R;
import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.manager.file.FileManager;

/**
 * Created by xiaoyu on 2017/7/26 16:41.
 */

public final class IconUtil {

    public static void displayIcon(String path, ImageView ivThumb) {
        int type = FileUtil.getFileType(path);
        switch (type) {
            case FileManager.IMAGE:
                ivThumb.setImageResource(R.drawable.file_type_photo);
                break;
            case FileManager.VIDEO:
                ivThumb.setImageResource(R.drawable.file_type_video);
                break;
            case FileManager.APP:
                ivThumb.setImageDrawable(PackageManagerLocker.getInstance().getApplicationIconByPath(path, 120, 120));
                break;
            case FileManager.AUDIO:
                ivThumb.setImageResource(R.drawable.file_type_music);
                break;
            case FileManager.OTHERS:
                ivThumb.setImageResource(R.drawable.file_type_default);
                break;
            case FileManager.TXT:
                ivThumb.setImageResource(R.drawable.txt_icon);
                break;
            case FileManager.PDF:
                ivThumb.setImageResource(R.drawable.pdf_icon);
                break;
            case FileManager.DOC:
                ivThumb.setImageResource(R.drawable.file_type_doc);
                break;
            case FileManager.ZIP:
                ivThumb.setImageResource(R.drawable.file_type_zip);
                break;
            default:
                break;
        }
    }
}
