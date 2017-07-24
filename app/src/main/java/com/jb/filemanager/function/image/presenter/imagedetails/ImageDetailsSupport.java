package com.jb.filemanager.function.image.presenter.imagedetails;

import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.image.ImageFileDeleteEvent;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.util.file.FileUtil;

/**
 * Created by nieyh on 17-7-4.
 */

public class ImageDetailsSupport implements ImageDetailsContract.Support {

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
