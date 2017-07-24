package com.jb.filemanager.function.image;

import com.jb.filemanager.function.image.modle.ImageModle;

/**
 * Created by nieyh on 17-7-24.
 */

public class ImageFileDeleteEvent {

    public ImageModle mImageModle;

    public ImageFileDeleteEvent(ImageModle imageModle) {
        mImageModle = imageModle;
    }
}
